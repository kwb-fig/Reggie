package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kong.Service.CategoryService;
import com.kong.Service.DishFlavorService;
import com.kong.Service.DishService;
import com.kong.common.R;
import com.kong.dto.DishDto;
import com.kong.entity.Category;
import com.kong.entity.Dish;
import com.kong.entity.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    RedisTemplate redisTemplate;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        Set keys=redisTemplate.keys("dishId*");
        redisTemplate.delete(keys);
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }
    @GetMapping("/page")
    public R<IPage> page(int page,int pageSize,String name){
        //构造器
        Page<Dish> pageInfo =new Page<>(page,pageSize);

        //扩展pageInfo
        Page<DishDto> dishDtoPage =new Page<>();

        //records就是查询出来的信息,与pageInfo重复,去重
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        //添加条件
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,lambdaQueryWrapper);

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> dishDtoList=new ArrayList<>();

        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String CategoryName = category.getName();

                dishDto.setCategoryName(CategoryName);
            }
            dishDtoList.add(dishDto);
        }

        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    //修改菜品中回显数据
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByWithFlavor(id);
        if(dishDto!=null){
            return R.success(dishDto);
        }
        return R.error("没有查到菜品信息");
    }

    //保存修改信息
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        Set keys=redisTemplate.keys("dishId*");
        redisTemplate.delete(keys);
        return R.success("修改菜品信息成功");
    }

    //删除菜品信息
    @DeleteMapping
    public R<String> delete(@RequestParam Long[] id){
        Set keys=redisTemplate.keys("dishId*");
        redisTemplate.delete(keys);
        for (Long aLong : id) {
            dishService.delete(aLong);
        }
        return R.success("删除成功");
    }

    //修改菜品状态信息
    @PostMapping(value = {"/status/0","/status/1"})
    public R<String > updateStatusById(@RequestParam Long[] id){
        Set keys=redisTemplate.keys("dishId*");
        redisTemplate.delete(keys);
        for (Long aLong : id) {
            dishService.updateStatus(aLong);
        }
        return R.success("修改成功");
    }

    //添加套餐中展示要新增的菜品
    @GetMapping("/list")
    public R<List<DishDto>> getByCategoryId(Dish dish){
        List<DishDto> dishDtoList=null;

        String key="dishId"+dish.getCategoryId()+"_1";

        //先从redis中查询菜品数据,如果有，就直接返回
        dishDtoList =(List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());

        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //不显示禁售菜品
        lambdaQueryWrapper.ne(Dish::getStatus,"0");

        List<Dish> dishList = dishService.list(lambdaQueryWrapper);

        //拓展Dish


        dishDtoList=new ArrayList<>();

        for (Dish dish1 : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            //每一个菜品id;
            Long id = dish1.getId();
            log.info("id={}",id);

            //根据每一个菜品id查询每一个相应的口味集合
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();

            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            dishDtoList.add(dishDto);
        }
        //redis没有数据，则查询数据库，并将数据存入redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
