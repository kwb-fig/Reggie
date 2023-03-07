package com.kong.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kong.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
