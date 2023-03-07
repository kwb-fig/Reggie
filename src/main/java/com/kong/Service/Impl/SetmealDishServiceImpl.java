package com.kong.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.SetmealDishMapper;
import com.kong.Service.SetmealDishService;
import com.kong.entity.SetmealDish;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
