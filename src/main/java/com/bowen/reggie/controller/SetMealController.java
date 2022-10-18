package com.bowen.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bowen.reggie.common.R;
import com.bowen.reggie.dto.SetmealDto;
import com.bowen.reggie.entity.Category;
import com.bowen.reggie.entity.Setmeal;
import com.bowen.reggie.entity.SetmealDish;
import com.bowen.reggie.service.CategoryService;
import com.bowen.reggie.service.SetMealDishService;
import com.bowen.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 1.批量删除 ✔️
 * 2.批量起售 ✔️
 * 3.批量停售 ✔️
 * 4.新建套餐 ✔️
 * 5.修改 ✔️
 * 6.起售（停售）✔️
 * 7.删除 ✔️
 * */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息:{}", setmealDto.toString());
        setMealService.saveWithDish(setmealDto);
        return R.success("新增菜品成功");
    }

    /**
     * 套餐管理（返回套餐信息）
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);

        //添加查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //查询
        setMealService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                //设置套餐名称
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);


    }

    /**
     * 删除套餐（批量删除，单个删除）
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        log.info("删除的ids{}", ids);
        setMealService.deleteWithDish(ids);
        return R.success("套餐删除成功！");

    }

//    /**
//     * 改变单个套餐售卖状态
//     * @param isSelling
//     * @param ids
//     * @return
//     */
//    @PostMapping("/status/{isSelling}")
//    public R<String> changeStatus(@PathVariable int isSelling, Long ids) {
//        log.info("修改套餐状态:{},{}", ids, isSelling);
//        UpdateWrapper<Setmeal> setmealUpdateWrapper = new UpdateWrapper<>();
//        setmealUpdateWrapper.eq("id", ids).set("status", isSelling);
//        setMealService.update(null, setmealUpdateWrapper);
//        if (isSelling == 1)
//            return R.success("套餐开始售卖!");
//        else
//            return R.success("套餐开始停售!");
//    }

    /**
     * 批量停售套餐售卖状态
     * @param isSelling
     * @param ids
     * @return
     */
    @PostMapping("/status/{isSelling}")
    public R<String> changeStatus(@PathVariable int isSelling,@RequestParam List<Long> ids) {
        log.info("ids:{}",ids);
        setMealService.updateSetmealStatusById(isSelling,ids);
//        if (ids.size() == 1){
//            if (isSelling == 1)
//                return R.success("该套餐起售!");
//            else
//                return R.success("该套餐停售!");
//        }
//        else {
//            if (isSelling == 1)
//                return R.success("套餐全部起售!");
//            else
//                return R.success("套餐全部停售!");
//        }
        return R.success("套餐状态修改成功");
    }

    /**
     * 修改套餐返回信息
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> updateById(@PathVariable Long id){
        log.info("id为:{}",id);
        SetmealDto setmealDto = setMealService.getDtoById(id);
        return R.success(setmealDto);


    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:{}",setmealDto.toString());
        boolean isUpdated = setMealService.updateWithsetmeal_Dish(setmealDto);
        if (isUpdated){
            return R.success("修改菜品成功");
        }
        else {
            return R.error("修改菜品失败");
        }
    }

}
