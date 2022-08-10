package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味 需要同时操作两种表 dish dish_flavor
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品的信息和口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息 同时更新菜品的口味信息
    void updateWithFlavor(DishDto dishDto);

    //删除菜品信息 同时删除菜品的口味信息
    R<String> deleteByIdWithFlavor(List<Long> ids);

    void deleteByIds(List<Long> ids);
}
