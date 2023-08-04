package com.sky.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.utilentity.StatiscsEntity;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;



    @Override
    public Result turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> timeList = timeList(begin, end);
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate time : timeList) {
            LocalDateTime beginTime = LocalDateTime.of(time, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(time, LocalTime.MAX);
            StatiscsEntity build = StatiscsEntity.builder().status(Orders.COMPLETED).beginTime(beginTime).endTime(endTime).build();
            Double turnOver =  reportMapper.queryByTimeAndStatus(build);
            turnOver = turnOver == null ? 0.0 : turnOver;
            turnoverList.add(turnOver);
        }

        TurnoverReportVO build = TurnoverReportVO.builder().dateList(StrUtil.join(",", timeList))
                .turnoverList(StrUtil.join(",", turnoverList))
                .build();

        return Result.success(build);
    }

    @Override
    public Result userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> localDates = timeList(begin, end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate localDate : localDates) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //当天新增用户
          Integer sumNewUser =  reportMapper.queryByTimeUser(beginTime, endTime);
          newUserList.add(sumNewUser);
          //当天之前的所有用户
          Integer sumAllUser = reportMapper.queryByTimeUser(null, endTime);
          totalUserList.add(sumAllUser);
        }
        return Result.success(UserReportVO.builder().newUserList(StrUtil.join(",", newUserList))
                .totalUserList(StrUtil.join(",", totalUserList))
                .dateList(StrUtil.join(",", localDates)).build());
    }

    @Override
    public Result ordersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = timeList(begin, end);
        //每日新增订单数
        List<Integer> newOrderrList = new ArrayList<>();
        //每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate localDate : dates) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //当天订单
            Integer sumNewOrder =  reportMapper.queryByTimeOrder(beginTime, endTime, null);
            newOrderrList.add(sumNewOrder);
            //当天有效订单数
            Integer sumAllUser = reportMapper.queryByTimeOrder(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(sumAllUser);
        }
        //全部订单
        Integer totalOrderCount = reportMapper.queryAllOrder();
        //全部有效订单
        Integer validOrderCount = reportMapper.queryAllValidOrder(Orders.COMPLETED);

        return Result.success(OrderReportVO.builder().dateList(StrUtil.join(",", dates))
                .orderCountList(StrUtil.join(",", newOrderrList))
                .validOrderCountList(StrUtil.join(",", validOrderCountList))
                .totalOrderCount(totalOrderCount).validOrderCount(validOrderCount)
                .orderCompletionRate(Double.valueOf(validOrderCount) / Double.valueOf(totalOrderCount)).build());
    }

    @Override
    public Result orderTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOS = reportMapper.queryTop10(beginTime, endTime);
        String nameList = goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.joining(","));
        String numberList = goodsSalesDTOS.stream().map(GoodsSalesDTO::getNumber)
                .map(Object::toString).collect(Collectors.joining(","));


        return Result.success(SalesTop10ReportVO.builder().nameList(nameList).numberList(numberList).build());
    }

    /**
     * 获取天数的列表
     * @param begin
     * @param end
     * @return 每一个元素代表一天
     */
    private List<LocalDate> timeList(LocalDate begin, LocalDate end){
        List<LocalDate> timeList = new ArrayList<>();
        timeList.add(begin);
        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            timeList.add(begin);
        }
        return timeList;
    }
}
