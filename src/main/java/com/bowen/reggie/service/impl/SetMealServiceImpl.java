package com.bowen.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bowen.reggie.common.CustomException;
import com.bowen.reggie.dto.SetmealDto;
import com.bowen.reggie.entity.Setmeal;
import com.bowen.reggie.entity.SetmealDish;
import com.bowen.reggie.mapper.SetMealMapper;
import com.bowen.reggie.service.SetMealDishService;
import com.bowen.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐同时保存与菜品的关联关系
     *
     * @param setmealDto
     */

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        //操作套餐和菜品的关联关系，操作setmeal_dish,执行insert操作
        setMealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐同时删除与套餐的关联数据
     *
     * @param ids
     */
    @Override
    public void deleteWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1

        //查询套餐状态，判断是否在售卖中
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(Setmeal::getId, ids);

        queryWrapper.eq(Setmeal::getStatus, 1);
        //select count(*) from
        int count = this.count(queryWrapper);


        //不能删除，抛出业务异常

        if (count > 0) {
            throw new CustomException("套餐正在售卖中，请先停售");
        }

        //如果可以删除，删除套餐表的数据--setmeal
        this.removeByIds(ids);

        //删除关系表的数据
        //delete from setmeal_dish where setmeal_id in ();
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据传入的ids查询
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setMealDishService.remove(lambdaQueryWrapper);


    }

    /**
     * 套餐状态批量改变
     *
     * @param status
     * @param ids
     */

    @Override
    public void updateSetmealStatusById(Integer status, List<Long> ids) {
        //查询套餐状态，判断是否在售卖中
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //批量查询id的SetMeal
        queryWrapper.in(Setmeal::getId, ids);
        //根据数据批量查询
        List<Setmeal> list = this.list(queryWrapper);
        for (Setmeal setmeal :
                list) {
            if (setmeal != null) {
                setmeal.setStatus(status);
            }
        }
        this.updateBatchById(list);
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */

    @Override
    public SetmealDto getDtoById(Long id) {
        //根据id查询setmeal
        Setmeal setmeal = this.getById(id);
        //创建Dto用于返回
        SetmealDto setmealDto = new SetmealDto();
        //创建查询类
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //查询setmealdish
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        if (setmeal != null) {
            //复制给setmealDto
            BeanUtils.copyProperties(setmeal, setmealDto);
            //根据传入的id获取setmeal的信息
            List<SetmealDish> setmealDishList = setMealDishService.list(queryWrapper);
            //将获取的信息赋给setmealdto
            setmealDto.setSetmealDishes(setmealDishList);
            return setmealDto;
        }
        return setmealDto;
    }

    /**
     * 修改setmeal套餐和setmeal_dish和数据
     *
     * @param setmealDto
     * @return
     */

    @Override
    public boolean updateWithsetmeal_Dish(SetmealDto setmealDto) {
        if (setmealDto == null) {
            return false;
        }
        //更改setmeal
        this.updateById(setmealDto);

//        //解包后获取
//        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//        //更新setmeal_dish
//        setMealDishService.saveOrUpdateBatch(setmealDishes);
//        这里无法获取id和setealid

//        先删除，再添加setmeal_dishs
        //获取setmeal id
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        //删除setmealdishes
//        setMealDishService.list(wrapper);
        setMealDishService.remove(wrapper);
        //添加setmeal_dishes
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //赋值给setmealId
        for (SetmealDish setmealDish :
                setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        //添加数据
        setMealDishService.saveBatch(setmealDishes);
        return true;
    }


}
