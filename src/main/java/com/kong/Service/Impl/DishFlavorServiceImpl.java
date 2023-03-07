package com.kong.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.DishFlavorMapper;
import com.kong.Service.DishFlavorService;
import com.kong.entity.DishFlavor;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
