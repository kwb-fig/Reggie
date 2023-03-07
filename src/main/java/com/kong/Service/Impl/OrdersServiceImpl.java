package com.kong.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Exception.addressExcetption;
import com.kong.Exception.submitException;
import com.kong.Mapper.OrderDetailMapper;
import com.kong.Mapper.OrdersMapper;
import com.kong.Service.*;
import com.kong.common.BaseContext;
import com.kong.common.R;
import com.kong.dto.OrdersDto;
import com.kong.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    ShoppingCartService shoppingCartService;

    @Autowired
    UserService userService;

    @Autowired
    AddressBookService addressBookService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    OrdersMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    //提交订单
    @Override
    @Transactional
    public void submit(Orders orders) {
        Long currentId = BaseContext.getCurrentId();

        //根据用户id查询购物车内容
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaQueryWrapper);

        if(shoppingCartList==null || shoppingCartList.size()==0){
            throw new submitException("购物车为空");
        }

        //拿到用户数据
        User user = userService.getById(currentId);

        //判断地址信息
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook==null){
            throw new addressExcetption("地址为空");
        }

        long orderId = IdWorker.getId();  // 订单号

        //遍历购物车列表，来算一下总金额
        //  购物车中 商品 的总金额 需要保证在多线程的情况下 也是能计算正确的，故需要使用原子类
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());

            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;

        }).collect(Collectors.toList());


        //填充订单对象信息（CV的）
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额，需要 遍历购物车，计算相关金额来得到
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        // 向订单表插入数据,一条数据，插入数据之前，需要填充如上属性
        this.save(orders);

        // 向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //调用接口清空购物车
        //shoppingCartController.clean();
        shoppingCartService.remove(lambdaQueryWrapper);

    }

    @Override
    public R<Page> userPage(Page pageInfo) {

        try {
            Page<OrdersDto> dtoPage = new Page<>();
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(Orders::getOrderTime);
            queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());

            //orderMapper.selectPage(pageInfo,queryWrapper);
            this.page(pageInfo,queryWrapper);
            BeanUtils.copyProperties(pageInfo,dtoPage,"records");

            List<Orders> records = pageInfo.getRecords();
            List<OrdersDto> ordersDtoList = new ArrayList<>();

            for (Orders record : records) {
                OrdersDto ordersDto = new OrdersDto();
                BeanUtils.copyProperties(record,ordersDto);

                Long userId = record.getUserId();
                LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(OrderDetail::getOrderId,record.getId());

               // List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper1);
                List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper1);
                ordersDto.setOrderDetails(orderDetails);

                //User user = userMapper.selectById(userId);
                User user = userService.getById(userId);

                if (user != null){
                    String name = user.getNickName();
                    ordersDto.setUserName(name);
                }
                ordersDtoList.add(ordersDto);
            }
            dtoPage.setRecords(ordersDtoList);
            return R.success(dtoPage);
        }catch (Exception e){
            e.printStackTrace();
            return R.error("订单数据加载失败");
        }

    }

    //后台订单明细表
    @Override
    public R<Page> pageQuery(Page pageInfo, String number, String beginTime, String endTime) {
        LambdaQueryWrapper<Orders> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number);

        lambdaQueryWrapper.between(beginTime!=null || endTime!=null,Orders::getOrderTime,beginTime,endTime);

        lambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

         this.page(pageInfo, lambdaQueryWrapper);

         //拓展Orders,获取用户名
        Page<OrdersDto> page=new Page<>();
        BeanUtils.copyProperties(pageInfo,page,"records");

        List<OrdersDto> ordersDtoList=new ArrayList<>();

        List<Orders> ordersList = this.list(lambdaQueryWrapper);

        for (Orders orders : ordersList) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders,ordersDto);

            Long userId = orders.getUserId();
            User user = userService.getById(userId);
            if(user!=null) {
                String name = user.getName();
                ordersDto.setUserName(name);
            }

            ordersDtoList.add(ordersDto);
        }

        page.setRecords(ordersDtoList);
        return R.success(page);
    }



    @Override
    public R<Map> hotSeal() {
        Map<String,Object> data = new HashMap<>();

        QueryWrapper<OrderDetail> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.select("COUNT(dish_id) as dishCount,name ").groupBy("name").having("COUNT(dish_id)").orderByDesc("dishCount").last("LIMIT 2");
        List<Map<String,Object>> orderDetails = orderDetailMapper.selectMaps(detailQueryWrapper);
        //count
        List<String> dishCount = new ArrayList<>();
        for (Map<String, Object> orderDetail : orderDetails) {
            String count = String.valueOf(orderDetail.get("dishCount"));
            dishCount.add(count);
        }
        data.put("dishCount",orderDetails);




        QueryWrapper<OrderDetail> detailQueryWrapper1 = new QueryWrapper<>();
        detailQueryWrapper1.select("COUNT(setmeal_id) as setmealCount,name ").groupBy("name").having("COUNT(setmeal_id)").orderByDesc("setmealCount").last("LIMIT 2");
        List<Map<String,Object>> setmealDetails = orderDetailMapper.selectMaps(detailQueryWrapper1);
        //count
        List<String> setmealCount = new ArrayList<>();
        for (Map<String, Object> setmealDetail : setmealDetails) {
            String count = String.valueOf(setmealDetail.get("setmealCount"));
            setmealCount.add(count);
        }
        data.put("setmealCount",setmealDetails);


        return R.success(data);
    }

    @Override
    public R<Map> OneWeekOrder() {

        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-7);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(-7);
        LocalDate startDay = LocalDate.now().plusDays(-7);

        Map<String,Object> map = new HashMap<>();
        List<Long> orderCount = new ArrayList<>();
        List<LocalDate> days = new ArrayList<>();
        for (int i = 0;i<7;i++){
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.between(Orders::getOrderTime,start,end);
            Long aLong = orderMapper.selectCount(queryWrapper);
            orderCount.add(aLong);
            start = start.plusDays(1);
            end = end.plusDays(1);
            days.add(startDay);
            startDay = startDay.plusDays(1);
        }
        map.put("orderCount",orderCount);
        map.put("days",days);
        return R.success(map);
    }

    @Override
    public R<Map> OneWeekLiuShui() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-7);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(-7);
        LocalDate startDay = LocalDate.now().plusDays(-7);

        Map<String,Object> map = new HashMap<>();
        List<Double> amount = new ArrayList<>();
        List<LocalDate> days = new ArrayList<>();
        for (int i = 0;i<7;i++){
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.between(Orders::getOrderTime,start,end);
            List<Orders> orders = orderMapper.selectList(queryWrapper);
            double orderAmount = 0;
            for (Orders order : orders) {
                orderAmount += order.getAmount().doubleValue();
            }
            amount.add(orderAmount);
            start = start.plusDays(1);
            end = end.plusDays(1);
            days.add(startDay);
            startDay = startDay.plusDays(1);

        }
        map.put("amount",amount);
        map.put("days",days);
        return R.success(map);
    }

    @Override
    public R<Long> countYesDayOrder() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).plusDays(-1);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).plusDays(-1);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime,start,end);
        Long aLong = orderMapper.selectCount(queryWrapper);


        return R.success(aLong);
    }

    @Override
    public R<Long> countToDayOrder() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime,start,end);
        Long aLong = orderMapper.selectCount(queryWrapper);
        return R.success(aLong);
    }

}
