package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.WorkSpaceMapper;
import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private WorkSpaceMapper workSpaceMapper;

    @Override
    public Result businessData() {
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        //今日营业额
        Double turnover = workSpaceMapper.querySales(begin, end);
        //有效订单数
        Integer validOrderCount = workSpaceMapper.queryOrder(begin,end, Orders.COMPLETED);
        //总订单
        Integer allOrder = workSpaceMapper.queryOrder(begin,end, null);
        //新增用户数
        Integer newUsers = workSpaceMapper.countNewUsers(begin, end);

        return Result.success(BusinessDataVO.builder().newUsers(newUsers).unitPrice(turnover / validOrderCount)
                .turnover(turnover).orderCompletionRate(Double.valueOf(validOrderCount) / Double.valueOf(allOrder))
                .validOrderCount(validOrderCount).build());
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        map.put("beginTime", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);

        //待接单
        Integer waitingOrders = workSpaceMapper.countByMap(map);

        //待派送
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = workSpaceMapper.countByMap(map);

        //已完成
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = workSpaceMapper.countByMap(map);

        //已取消
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = workSpaceMapper.countByMap(map);

        //全部订单
        map.put("status", null);
        Integer allOrders = workSpaceMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public DishOverViewVO getDishOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = workSpaceMapper.countByDishMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = workSpaceMapper.countByDishMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = workSpaceMapper.countBySetMealMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = workSpaceMapper.countBySetMealMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
