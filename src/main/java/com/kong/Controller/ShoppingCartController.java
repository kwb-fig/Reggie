package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.kong.Exception.subException;
import com.kong.Service.ShoppingCartService;
import com.kong.common.BaseContext;
import com.kong.common.R;
import com.kong.entity.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    //查看购物车
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    //添加菜品或者套餐去购物车
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart cart){
        cart.setCreateTime(LocalDateTime.now());

        Long currentId = BaseContext.getCurrentId();
        log.info("id={}",currentId);
        cart.setUserId(currentId);

        //查询当前菜品是否在购物车中
        Long dishId = cart.getDishId();

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId!=null){
            //添加的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            //添加的套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,cart.getSetmealId());
        }

        //查看菜品或套餐是否已经添加过，如果是，就将number+1
        ShoppingCart cart1 = shoppingCartService.getOne(lambdaQueryWrapper);


        if(cart1!=null){
            Integer number = cart1.getNumber();
            cart1.setCreateTime(LocalDateTime.now());
            cart1.setNumber(number+1);
            shoppingCartService.updateById(cart1);
        }else {
            cart.setNumber(1);
            shoppingCartService.save(cart);
            cart1=cart;
        }

        return R.success(cart1);
    }
    @PostMapping("/sub")
    public R<String> sub(ShoppingCart cart){
        Long dishId = cart.getDishId();
        Long setmealId = cart.getSetmealId();

        Long currentId = BaseContext.getCurrentId();

        //通过传过来的菜品或套餐id查出购物车信息
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        //查出来的就是某一条记录
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        lambdaQueryWrapper.eq(dishId!=null,ShoppingCart::getDishId,dishId);
        lambdaQueryWrapper.eq(setmealId!=null,ShoppingCart::getSetmealId,setmealId);

        ShoppingCart shoppingCart = shoppingCartService.getOne(lambdaQueryWrapper);

        if(shoppingCart.getNumber()==1){
            shoppingCartService.removeById(shoppingCart);
        }
        Integer number = shoppingCart.getNumber();
        shoppingCart.setNumber(number-1);
        shoppingCartService.updateById(shoppingCart);
        return R.success("删除成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper=new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }

}
