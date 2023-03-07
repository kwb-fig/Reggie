package com.kong.Controller;

import com.kong.Service.OrderDetailService;
import com.kong.Service.OrdersService;
import com.kong.common.R;
import com.kong.entity.OrderDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    OrdersService ordersService;

    @Autowired
    OrderDetailService orderDetailService;

    //后台查看订单信息
    @GetMapping("/{id}")
    public R<List<OrderDetail>> getById(@PathVariable Long id){

        return  orderDetailService.getByOrderId(id);

    }
}
