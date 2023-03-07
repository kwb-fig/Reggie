package com.kong.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.UserMapper;
import com.kong.Service.UserService;
import com.kong.common.BaseContext;
import com.kong.common.R;
import com.kong.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    HttpServletRequest request;
    @Override
    public void logout() {
        request.getSession().removeAttribute("user");
    }

    //会员分页查询
    @Override
    public R<Page> pageQuery(Page pageInfo,String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(phone),User::getPhone,phone);
        queryWrapper.orderByDesc(User::getCreateTime);
        this.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
}
