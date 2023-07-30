package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.Result;
import com.sky.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @Override
    public Result save(DishDTO dishDTO) {
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        Boolean success = dishMapper.insert(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Boolean successFlavor = true;
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());

            });
            successFlavor = dishFlavorMapper.insertList(flavors);
        }

        if (!success || !successFlavor) {
            return Result.error( "新增菜品 or 口味失败！");
        }
        return Result.success();
    }
}
