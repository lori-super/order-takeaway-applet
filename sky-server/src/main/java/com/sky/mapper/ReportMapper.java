package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.utilentity.StatiscsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {

    Double queryByTimeAndStatus(StatiscsEntity build);

    Integer queryByTimeUser(LocalDateTime beginTime, LocalDateTime endTime);


    Integer queryByTimeOrder(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    @Select("select count(*) from orders")
    Integer queryAllOrder();

    @Select("select count(*) from orders where status = #{status}")
    Integer queryAllValidOrder(Integer status);

    List<GoodsSalesDTO> queryTop10(LocalDateTime begin, LocalDateTime end);
}
