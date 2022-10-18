package com.bowen.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bowen.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    public boolean remove(Long ids);
}
