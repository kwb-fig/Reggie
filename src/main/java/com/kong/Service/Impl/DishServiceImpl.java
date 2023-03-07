package com.kong.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Exception.deleteException;
import com.kong.Mapper.DishMapper;
import com.kong.Service.DishFlavorService;
import com.kong.Service.DishService;
import com.kong.Service.SetmealDishService;
import com.kong.dto.DishDto;
import com.kong.entity.Dish;
import com.kong.entity.DishFlavor;
import com.kong.entity.SetmealDish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        //mp先用雪花算法生成id赋给实体，后插入数据库
        Long dishId = dishDto.getId();

        //再操作dishflavor表
        List<DishFlavor> flavors = dishDto.getFlavors();

        //用流读入
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //添加到表中去
        dishFlavorService.saveBatch(flavors);
    }

    //修改菜品时回显菜品信息
    @Override
    public DishDto getByWithFlavor(Long id) {
        Dish dish = this.getById(id);

        DishDto dishDto=new DishDto();

        //填充dish到disthDto中去
        BeanUtils.copyProperties(dish,dishDto);

        //根据菜品id查询菜品口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());

        List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //修改口味信息时，先将口味删除，再新增口味信息
        //先可以直接修改dish类
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        //先将口味信息删除
        dishFlavorService.remove(lambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        //再插入菜品id
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    //批量删除菜品
    @Override
    public void delete(Long id) {
        Dish dish = this.getById(id);
        //判断状态，在售状态不能删除，只有停售后才能删除
        if(dish.getStatus()==1){
            throw new deleteException("在售，不能删除");
        }
        //判断该菜品是否关联套餐，如果关联，则不能删除,要现在套餐中删除该菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(SetmealDish::getDishId,id);

        long count = setmealDishService.count(lambdaQueryWrapper);

        if(count>0){
            throw new deleteException("与套餐关联，不能删除");
        }

        //最后执行删除，进行逻辑删除，将delete置为一，表示删除

        dish.setIsDeleted(1);

        this.updateById(dish);
    }


    //批量修改菜品状态
    @Override
    public void updateStatus(Long id) {
        Dish dish = this.getById(id);

        if(dish.getStatus()==1){
            dish.setStatus(0);
        }
        else dish.setStatus(1);

        this.updateById(dish);
    }

}
