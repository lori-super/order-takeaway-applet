package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.UpdateFailException;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Override
    @Transactional
    public Result insert(SetmealDTO setmealDTO) {

        Setmeal setmeal = BeanUtil.copyProperties(setmealDTO, Setmeal.class);
        setMealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmeal.getId());
            setMealDishMapper.insert(setmealDish);
        });

        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public Result queryPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Setmeal setmeal = BeanUtil.copyProperties(setmealPageQueryDTO, Setmeal.class);
        Page<SetmealVO> pages = setMealMapper.queryPage(setmeal);

        return Result.success(new PageResult(pages.getTotal(), pages.getResult()));
    }

    @Override
    @Transactional
    public Result updateStatus(Integer status, Long id) {
        if (status == StatusConstant.ENABLE){
            Integer count = setMealDishMapper.queryByIdForDish(id);
            if (count > 0) {
                throw new UpdateFailException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        Boolean update = setMealMapper.update(setmeal);
        if (!update) {
            return Result.error("更新状态失败！");
        }
        return Result.success();
    }

    @Override
    public Result queryById(Long id) {
        SetmealVO setmealVO = setMealMapper.queryById(id);
        return Result.success(setmealVO);
    }

    @Override
    @Transactional
    public Result update(SetmealDTO setmealDTO) {
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
            setMealDishMapper.insert(setmealDish);
        });

        Boolean update = setMealMapper.update(BeanUtil.copyProperties(setmealDTO, Setmeal.class));
        if (!update) {
            return Result.error("修改失败！");
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result delete(List<Long> ids) {
        //查询每个套餐状态，是否可以删除
        for (Long id : ids) {
            SetmealVO setmealVO = setMealMapper.queryById(id);
            Integer status = setmealVO.getStatus();
            if(status == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //删除
        for (Long id : ids) {
            setMealMapper.deleteById(id);
            setMealDishMapper.deleteById(id);
        }

        return Result.success();
    }


}
