package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface WorkSpaceMapper {
    Double querySales(LocalDateTime beginTime, LocalDateTime endTime);

    Integer queryOrder(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    Integer countNewUsers(LocalDateTime beginTime, LocalDateTime endTime);

    Integer countByMap(Map map);

    Integer countByDishMap(Map map);

    Integer countBySetMealMap(Map map);
}
