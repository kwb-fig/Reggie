package com.kong.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Exception.deleteException;
import com.kong.Mapper.SetmealMapper;
import com.kong.Service.DishService;
import com.kong.Service.SetmealDishService;
import com.kong.Service.SetmealService;
import com.kong.dto.SetmealDto;
import com.kong.entity.Setmeal;
import com.kong.entity.SetmealDish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    DishService dishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先可以保存Setmeal
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        //再去添加绑定菜品
        setmealDishService.saveBatch(setmealDishes);
    }

    //修改套餐时回显数据
    @Override
    public SetmealDto queryById(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto=new SetmealDto();

        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //根据套餐id查询菜品
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);

        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);


        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    //修改套餐信息
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        //先删除菜品信息，然后再添加菜品信息
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(lambdaQueryWrapper);

        //添加菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        setmealDishService.saveBatch(setmealDishes);
    }


    //修改套餐状态
    @Override
    public void updateStatus(Long id) {
        Setmeal setmeal = this.getById(id);

        if(setmeal.getStatus()==0){
            setmeal.setStatus(1);
        }
        else setmeal.setStatus(0);

        this.updateById(setmeal);
    }

    //删除套餐
    @Override
    public void deleteWithDish(Long id) {
        //先查看状态，如果是在售，则不能删除
        Setmeal setmeal = this.getById(id);
        Integer status = setmeal.getStatus();

        if(status==1){
            throw new deleteException("套餐在售，不能删除");
        }
        //Setmeal表删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getId,id);

        this.remove(setmealLambdaQueryWrapper);

        //SetmealDish表删除
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper=new LambdaQueryWrapper<>();

        setmealDishLambdaQueryWrapper.eq(SetmealDish::getId,id);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }
}
