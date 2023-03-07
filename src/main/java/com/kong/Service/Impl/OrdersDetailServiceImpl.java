package com.kong.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.OrderDetailMapper;
import com.kong.Service.OrderDetailService;
import com.kong.common.R;
import com.kong.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>implements OrderDetailService {

    @Override
    public R<List<OrderDetail>> getByOrderId(Long id){
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> list = this.list(queryWrapper);
        return R.success(list);
    }
}
