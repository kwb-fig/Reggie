package com.kong.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kong.dto.SetmealDto;
import com.kong.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐
    void saveWithDish(SetmealDto setmealDto);

    //修改套餐时回显数据
    SetmealDto queryById(Long id);

    //修改套餐信息
    void updateWithDish(SetmealDto setmealDto);

    //修改套餐状态
    void updateStatus(Long id);

    //删除套餐
    void deleteWithDish(Long id);
}
