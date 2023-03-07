package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kong.Service.OrderDetailService;
import com.kong.Service.OrdersService;
import com.kong.common.BaseContext;
import com.kong.common.R;
import com.kong.dto.OrdersDto;
import com.kong.entity.OrderDetail;
import com.kong.entity.Orders;
import com.kong.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrdersService ordersService;

    @Autowired
    OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    //前台订单分页
    @GetMapping("/userPage")
    public R<Page> userpage(int page,int pageSize){
        Page pageInfo=new Page(page,pageSize);
        return ordersService.userPage(pageInfo);
    }

    //后台订单分页
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number,String beginTime,String endTime){
        Page pageInfo=new Page(page,pageSize);
        return ordersService.pageQuery(pageInfo, number, beginTime, endTime);
    }
    //修改订单状态
    @PutMapping
    public R<String> status(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改订单状态成功！");
    }





    @DeleteMapping
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteOrder(Long id){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        orderDetailService.remove(queryWrapper);
        ordersService.removeById(id);
        return R.success("删除成功！");
    }


    @PostMapping("/again")
    public R<String> addOrderAgain(@RequestBody Orders orders){
        if (orders.getId() != null){
            return R.success("成功！");
        }
        return R.error("失败!");
    }

    @GetMapping("/getToDayOrder")
    public R<Long> getToDayOrder(){
        return ordersService.countToDayOrder();
    }

    @GetMapping("/getYesDayOrder")
    public R<Long> getYesDayOrder(){
        return ordersService.countYesDayOrder();
    }

    @GetMapping("/getOneWeekLiuShui")
    public R<Map> getOneWeekLiuShui(){
        return ordersService.OneWeekLiuShui();
    }

    @GetMapping("/getOneWeekOrder")
    public R<Map> getOneWeekOrder(){
        return ordersService.OneWeekOrder();
    }

    @GetMapping("/getHotSeal")
    public R<Map> getHotSeal(){
        return ordersService.hotSeal();
    }
}
