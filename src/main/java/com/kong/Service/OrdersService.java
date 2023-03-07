package com.kong.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kong.common.R;
import com.kong.entity.Orders;

import java.util.Map;

public interface OrdersService extends IService<Orders> {

    //提交订单
    void submit(Orders orders);

    //前台订单列表
    R<Page> userPage(Page pageInfo);


    //后台订单明细表
    R<Page> pageQuery(Page pageInfo, String number, String beginTime, String endTime);


    /**
     * 查询今日订单量
     * @return
     */
    R<Long> countToDayOrder();

    /**
     * 查询昨日订单量
     * @return
     */
    R<Long> countYesDayOrder();

    /**
     * 查询近一周流水
     * @return
     */
    R<Map> OneWeekLiuShui();

    /**
     * 查询近一周订单数量
     * @return
     */
    R<Map> OneWeekOrder();

    /**
     * 查询热卖套餐
     * @return
     */
    R<Map> hotSeal();

}
