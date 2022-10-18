package com.bowen.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bowen.reggie.dto.DishDto;
import com.bowen.reggie.entity.Dish;
import com.bowen.reggie.entity.DishFlavor;
import com.bowen.reggie.mapper.DishMapper;
import com.bowen.reggie.service.DishFlavorService;
import com.bowen.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本信息到基本表
        this.save(dishDto);
        //菜品的id
        Long dishId = dishDto.getId();
        //保存菜品口味数据到菜品口味表dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        //重新赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        //创建dishDto对象
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);
        //清理当前菜品对应的口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        //使用流
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //更新口味表
        dishFlavorService.saveBatch(flavors);


    }

    /**
     * 单批量修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @Override
    public void updateStatusByIds(int status, List<Long> ids) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Dish::getId,ids);
        List<Dish> list = this.list(wrapper);
        for (Dish dish :
            list) {
            if (dish != null) {
                dish.setStatus(status);
            }
        }
        this.updateBatchById(list);
        return;
    }


}
