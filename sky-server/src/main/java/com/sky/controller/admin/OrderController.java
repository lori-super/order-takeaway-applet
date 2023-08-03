package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrderController")
@Api(tags = "订单接口类")
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索：{}", ordersPageQueryDTO);
        return orderService.conditionSearch(ordersPageQueryDTO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("根据订单ID查询")
    public Result<OrderVO> queryById(@PathVariable Long id){
        log.info("客户端 根据订单ID查询：{}", id);
        return orderService.queryById(id);
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<OrderVO> completeOrder(@PathVariable Long id){
        log.info("完成订单：{}", id);
        return orderService.completeOrder(id);
    }

    @PutMapping("/confirm")
    @ApiOperation("完成订单")
    public Result<OrderVO> confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单：{}", ordersConfirmDTO);
        return orderService.confirmOrder(ordersConfirmDTO);
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("客户端取消订单：{}", ordersCancelDTO);
        return orderService.cancelAdminOrder(ordersCancelDTO);
    }

    @PutMapping("/rejection")
    @ApiOperation("取消订单")
    public Result rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("客户端拒绝订单：{}", ordersRejectionDTO);
        return orderService.rejectionOrder(ordersRejectionDTO);
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id){
        log.info("客户端派送订单：{}", id);
        return orderService.deliveryOrder(id);
    }

    @GetMapping ("/statistics")
    @ApiOperation("统计订单")
    public Result<OrderStatisticsVO> statisticsOrder(){
        log.info("客户端统计订单");
        return orderService.statisticsOrder();
    }

}
