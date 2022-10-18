package com.bowen.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bowen.reggie.dto.SetmealDto;
import com.bowen.reggie.entity.Setmeal;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    /**
     * 新增套餐同时保存与菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐同时删除与套餐的关联数据
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);

    /**
     * 套餐状态批量改变
     * @param status
     * @param ids
     */
    public void updateSetmealStatusById(Integer status,List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealDto getDtoById(Long id);

    /**
     * 修改setmeal套餐和setmeal_dish
     * @param setmealDto
     * @return
     */
    public boolean updateWithsetmeal_Dish(SetmealDto setmealDto);

}
