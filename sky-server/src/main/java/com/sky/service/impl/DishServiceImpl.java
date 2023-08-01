package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.RedisConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.UpdateFailException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @Override
    @Transactional
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

        String key = RedisConstant.DISH_INFO + dishDTO.getCategoryId().toString();
        cleanCache(key);
        return Result.success();
    }

    @Override
    public Result queryPage(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        Page<DishVO> disheVos = dishMapper.queryPage(dto);

        return Result.success(new PageResult(disheVos.getTotal(), disheVos.getResult()));
    }

    @Override
    public Result deleteDish(List<Long> ids) {
        // 查询每个菜品的status，是否在售，在售不可删除

        for (Long id : ids) {
            Dish dish = dishMapper.queryById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw  new DeletionNotAllowedException(dish.getName() + "," + MessageConstant.DISH_ON_SALE);
            }
        }

        // 查询菜品关联的套餐
        List<Long> setMealIds = setMealDishMapper.queryByIds(ids);
        if (setMealIds != null && setMealIds.size() > 0) {
            throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteById(id);
        }

        String key = RedisConstant.DISH_INFO + "*";
        cleanCache(key);

        return Result.success();
    }

    /**
     * 菜品起售 停售
     * @param status
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Result updateStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setStatus(status);
        dish.setId(id);
        Boolean success = dishMapper.update(dish);
        if (!success) {
            return Result.error("修改失败！");
        }
        String key = RedisConstant.DISH_INFO + "*";
        cleanCache(key);
        return Result.success();
    }

    /**
     * 修改菜品信息
     * @param id
     * @return
     */
    @Override
    public Result<DishVO> queryById(Long id) {
        Dish dish = dishMapper.queryById(id);
        DishVO dishVO = BeanUtil.copyProperties(dish, DishVO.class);
        return Result.success(dishVO);
    }

    @Override
    @Transactional
    public Result update(DishDTO dishDTO) {
        Dish dish = BeanUtil.copyProperties(dishDTO, Dish.class);
        Boolean success = dishMapper.update(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        Boolean successFlavor = true;
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());

            });
            successFlavor = dishFlavorMapper.insertList(flavors);
        }

        if (!success || !successFlavor) {
            return Result.error( "更新菜品 or 口味失败！");
        }
        String key = RedisConstant.DISH_INFO + dishDTO.getCategoryId().toString();
        cleanCache(key);
        return Result.success();
    }

    @Override
    public Result queryCategoryId(Long categoryId) {
        List<Dish> dishs = dishMapper.queryByCategoryId(categoryId);
        return Result.success(dishs);
    }

    /**
     * C端 根据分类ID查询菜品
     * @param dish
     * @return
     */
    @Override
    @Cacheable(cacheNames = RedisConstant.DISH_INFO, key = "#dish.categoryId")
    public List<DishVO> listWithFlavor(Dish dish) {
//        String key = RedisConstant.DISH_INFO + dish.getCategoryId().toString();
//        String json = stringRedisTemplate.opsForValue().get(key);
//
//        if (json != null) {
//            return JSONUtil.toList(json, DishVO.class);
//        }
        List<Dish> dishes = dishMapper.queryListFlavor(dish);
        List<DishVO> dishVOS = new ArrayList<>();
        for (Dish dish1 : dishes) {
            DishVO dishVO = BeanUtil.copyProperties(dish1, DishVO.class);
            List<DishFlavor> dishFlavors = dishFlavorMapper.queryByDishId(dish1.getId());
            dishVO.setFlavors(dishFlavors);
            dishVOS.add(dishVO);
        }
        //stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(dishVOS));
        return dishVOS;
    }

    public void cleanCache(String pattern){
        Set<String> keys = stringRedisTemplate.keys(pattern);
        stringRedisTemplate.delete(keys);
    }

}
