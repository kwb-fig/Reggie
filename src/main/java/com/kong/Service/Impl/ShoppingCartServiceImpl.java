package com.kong.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.ShoppingCartMapper;
import com.kong.Service.ShoppingCartService;
import com.kong.entity.ShoppingCart;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {
}
