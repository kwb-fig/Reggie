package com.kong.Service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Exception.CustomerException;
import com.kong.Mapper.CategoryMapper;
import com.kong.Mapper.DishMapper;
import com.kong.Mapper.SetmealMapper;
import com.kong.Service.CategoryService;
import com.kong.entity.Category;
import com.kong.entity.Dish;
import com.kong.entity.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    //自己拓展删除方法，如果分类中关联了菜品或者套餐，则不能删除
    @Override
    public void remove(Long id) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();

        //查询菜品关联的分类id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        //统计数量
        Long dishCount = dishMapper.selectCount(dishLambdaQueryWrapper);

        if(dishCount>0){
            //抛出异常
            throw new CustomerException("分类关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        Long setmealCount = setmealMapper.selectCount(setmealLambdaQueryWrapper);

        if(setmealCount>0){
            //抛出异常
            throw new CustomerException("分类关联了套餐，不能删除");
        }

        super.removeById(id);
    }
}
