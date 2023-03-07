package com.kong.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kong.common.R;
import com.kong.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    //退出
    void logout();

    //会员信息分页查询
    R<Page> pageQuery(Page pageInfo,String phone);
}
