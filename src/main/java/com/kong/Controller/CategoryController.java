package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kong.Service.CategoryService;
import com.kong.common.R;
import com.kong.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增菜品分类");
        categoryService.save(category);
        return R.success("新增成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);

        //构造条件构造器，用于排序
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //进行降序排序
        lambdaQueryWrapper.orderByDesc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    //菜品管理中新增菜品中要添加到的菜品分类
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //排序条件
        lambdaQueryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);

        return R.success(categoryList);
    }
}
