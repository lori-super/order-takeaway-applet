package com.sky.service;

import com.sky.result.Result;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkSpaceService {
    Result businessData();

    OrderOverViewVO getOrderOverView();

    DishOverViewVO getDishOverView();


    SetmealOverViewVO getSetmealOverView();
}
