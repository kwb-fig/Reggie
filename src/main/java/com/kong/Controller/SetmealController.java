package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kong.Service.CategoryService;
import com.kong.Service.SetmealDishService;
import com.kong.Service.SetmealService;
import com.kong.common.R;
import com.kong.dto.SetmealDto;
import com.kong.entity.Category;
import com.kong.entity.Setmeal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

    //添加套餐信息
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    //分页显示数据
    @GetMapping("/page")
    public R<IPage> page(int page,int pageSize,String name){

        Page<Setmeal> pageInfo =new Page<>(page,pageSize);

        //拓展Setmeal，用SetmealDto去封装套餐分类名称
        Page<SetmealDto> setmealDtoPage=new Page<>();

        //去除重复数据
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //模糊查询
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(name),Setmeal::getName,name);

        //排序
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,lambdaQueryWrapper);

        //SetmealDto
        List<SetmealDto> setmealDtoList=new ArrayList<>();

        List<Setmeal> records = pageInfo.getRecords();

        for (Setmeal record : records) {
            SetmealDto setmealDto=new SetmealDto();

            BeanUtils.copyProperties(record,setmealDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            setmealDtoList.add(setmealDto);
        }

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    //修改数据时可以回显数据
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.queryById(id);
        return R.success(setmealDto);
    }

    //修改套餐信息
    @PutMapping
    public R<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    //修改套餐状态
    @PostMapping(value = {"/status/0","/status/1"})
    public R<String> updateStatus(Long[] id){
        for (Long aLong : id) {
            setmealService.updateStatus(aLong);
        }
        return R.success("修改成功");
    }

    //删除套餐
    @DeleteMapping
    public R<String> delete(Long[] id){
        for (Long aLong : id) {
            setmealService.deleteWithDish(aLong);
        }
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());

        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());

        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }

}
