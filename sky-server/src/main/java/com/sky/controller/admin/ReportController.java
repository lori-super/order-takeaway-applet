package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "统计相关接口")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")
                                         LocalDate begin,
                                     @DateTimeFormat(pattern = "yyyy-MM-dd")
                                     LocalDate end){
        log.info("统计营业额, 开始：{}，结束：{}",begin, end);
        return reportService.turnoverStatistics(begin, end);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计接口")
    public Result userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")
                                     LocalDate begin,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd")
                                     LocalDate end){
        log.info("统计用户数, 开始：{}，结束：{}",begin, end);
        return reportService.userStatistics(begin, end);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计接口")
    public Result ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")
                                 LocalDate begin,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd")
                                 LocalDate end){
        log.info("统计订单, 开始：{}，结束：{}",begin, end);
        return reportService.ordersStatistics(begin, end);
    }

    @GetMapping("/top10")
    @ApiOperation("查询销量排名TOP10")
    public Result orderTop10(@DateTimeFormat(pattern = "yyyy-MM-dd")
                                   LocalDate begin,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd")
                                   LocalDate end){
        log.info("销量Top10, 开始：{}，结束：{}",begin, end);
        return reportService.orderTop10(begin, end);
    }
}
