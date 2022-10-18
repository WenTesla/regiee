package com.bowen.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bowen.reggie.dto.DishDto;
import com.bowen.reggie.entity.Dish;
import org.springframework.stereotype.Service;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品同时插入菜品对应的口味数据，操作两表dish和dish_flavor

    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息

    public DishDto getByIdWithFlavor(Long id);

    //修改菜品信息，同时修改口味信息
    public void updateWithFlavor(DishDto dishDto);
    
    //单批量修改菜品状态
    public void updateStatusByIds(int status, List<Long> ids);
    
}
