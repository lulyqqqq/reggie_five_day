package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: DishServiceImpl
 * @author: mafangnian
 * @date: 2022/8/8 11:30
 * @Blog: null
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private SetmealService setmealService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品,同时保存口味数据
     *
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品信息到dish
        this.save(dishDto);

        Long disId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //使用stream流处理,将菜品口味对应到新建立的菜品id上
        flavors = flavors.stream().map((item) -> {
            item.setDishId(disId);
            return item;
        }).collect(Collectors.toList());

        //保存口味信息到dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品的信息和口味信息
     *
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息,在dish表中查询
        Dish dish = this.getById(id);

        //拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        //查询当前菜品的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息 同时更新菜品的口味信息
     *
     * @param dishDto
     */
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        //更新dish表信息,菜品基本信息更新
        this.updateById(dishDto);

        //更新菜品口味信息
        //先清理原来菜品的口味信息---dishFlavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交的口味信息---dishFlavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //使用stream流处理,将菜品口味对应到新建立的菜品id上
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 逻辑删除
     * 关联到套餐
     * 如果套餐在售卖且含有菜品,则菜品不能够删除
     * @param ids
     * @return
     */
    public R<String> deleteByIdWithFlavor(List<Long> ids) {

        //根据菜品id在stemeal_dish表中查出哪些套餐包含该菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        List<SetmealDish> SetmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //如果菜品没有关联套餐，直接删除就行  其实下面这个逻辑可以抽离出来，这里我就不抽离了
        if (SetmealDishList.size() == 0){
            //这个deleteByIds中已经做了菜品起售不能删除的判断力
            dishService.deleteByIds(ids);
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DishFlavor::getDishId,ids);
            dishFlavorService.remove(queryWrapper);
            return R.success("菜品删除成功");
        }

        //如果菜品有关联套餐，并且该套餐正在售卖，那么不能删除
        //得到与删除菜品关联的套餐id
        ArrayList<Long> Setmeal_idList = new ArrayList<>();
        for (SetmealDish setmealDish : SetmealDishList) {
            Long setmealId = setmealDish.getSetmealId();
            Setmeal_idList.add(setmealId);
        }

        //查询出与删除菜品相关联的套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,Setmeal_idList);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);
        //对拿到的所有套餐进行遍历，然后拿到套餐的售卖状态，如果有套餐正在售卖那么删除失败
        for (Setmeal setmeal : setmealList) {
            Integer status = setmeal.getStatus();
            if (status == 1){
                return R.error("删除的菜品中有关联在售套餐,删除失败！");
            }
        }


        //要删除的菜品关联的套餐没有在售，可以删除
        //这下面的代码并不一定会执行,因为如果前面的for循环中出现status == 1,那么下面的代码就不会再执行
        dishService.deleteByIds(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);


        //删除菜品口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);

        return R.success("菜品删除成功");
    }

    /**
     * 查询菜品有没有在售卖的状态
     *
     * @param ids
     */
    public void deleteByIds(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids != null, Dish::getId, ids);
        List<Dish> list = this.list(queryWrapper);
        for (Dish dish : list) {
            Integer status = dish.getStatus();
            //如果不是在售卖,则可以删除
            if (status == 0) {
                this.removeById(dish.getId());
            } else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }

    }

}
