package com.kong.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kong.dto.DishDto;
import com.kong.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品
    void saveWithFlavor(DishDto dishDto);

    //修改菜品时回显菜品信息
    DishDto getByWithFlavor(Long id);

    //保存修改菜品信息
    void updateWithFlavor(DishDto dishDto);

    //批量删除菜品
    void delete(Long id);

    //批量修改菜品状态信息
    void updateStatus(Long id);
}
