package com.kong.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kong.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
