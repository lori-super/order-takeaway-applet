package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.Result;

import java.util.List;

public interface SetmealService {
    Result insert(SetmealDTO setmealDTO);

    Result queryPage(SetmealPageQueryDTO setmealPageQueryDTO);

    Result updateStatus(Integer status, Long id);

    Result queryById(Long id);

    Result update(SetmealDTO setmealDTO);

    Result delete(List<Long> ids);
}
