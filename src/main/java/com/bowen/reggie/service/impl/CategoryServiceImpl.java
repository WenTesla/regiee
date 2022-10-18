package com.bowen.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bowen.reggie.common.CustomException;
import com.bowen.reggie.entity.Category;
import com.bowen.reggie.entity.Dish;
import com.bowen.reggie.entity.Setmeal;
import com.bowen.reggie.mapper.CategoryMapper;
import com.bowen.reggie.service.CategoryService;
import com.bowen.reggie.service.DishService;
import com.bowen.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     *
     * @param ids
     * @return
     */

    @Override
    public boolean remove(Long ids) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count1 = dishService.count(dishLambdaQueryWrapper);
//查询当前分类受否关联了菜品，如果已经关联，删除失败
        if (count1 > 0) {
//            已经关联，删除失败
            throw new CustomException("当前的分类项目关联了菜品，删除失败！");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2=setMealService.count();
        if (count2 > 0){
//            已经关联，抛出一个业务异常
            throw new CustomException("当前的分类关联了套餐，删除失败！");

        }
        super.removeById(ids);
        return false;
    }
}
