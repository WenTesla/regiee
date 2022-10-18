package com.bowen.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bowen.reggie.common.R;
import com.bowen.reggie.entity.Category;
import com.bowen.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐分类
     * @param category json格式
     * @return
     */

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category.toString());
        categoryService.save(category);
        return R.success("新增分类成功");
    }


    /**
     * 分页
     * @param page 页数
     * @param pageSize 每页页数
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageinfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件,根据sort排序
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }

    /**
     * 根据id删除分类
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long ids){
        log.info("删除id为{}",ids);
        categoryService.remove(ids);
        return R.success("分类删除成功!");


    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息:{}",category);

        categoryService.updateById(category);

        return R.success("修改分类信息成功！");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() !=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //查询

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);

    }


}
