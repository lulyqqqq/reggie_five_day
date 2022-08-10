package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: CategoryServiceImpl
 * @author: mafangnian
 * @date: 2022/8/8 11:30
 * @Blog: null
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    @Resource CategoryService categoryService;
    /**
     * 根据id删除分类,删除之前需要进行判断,当前分类有没有关联菜品或者套装
     * 如果有则不能删除
     * @param id
     */
    @Override
    public void remove(Long id) {
        //菜品匹配
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        //套餐匹配
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);

        //查询出来分类关联的菜品数
        long count = dishService.count(dishLambdaQueryWrapper);

        //查询出来分类关联的套餐数
        long count1 = setmealService.count(setmealLambdaQueryWrapper);
        //查询当前分类有没有关联菜品
        if (count>0){
            //已关联菜品,无法删除,抛出业务异常
            throw new CustomException("当前分类关联了菜品,不能删除");
        }

        //查询当前分类有没有关联套餐
        if (count1>0){
            //已关联套餐,无法删除,抛出业务异常
            throw new CustomException("当前分类关联了套餐,不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
