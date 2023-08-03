package com.sky.controller.user;

import cn.hutool.db.sql.Order;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("UserOrderController")
@Api(tags = "订单接口类")
@Slf4j
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("submit")
    @ApiOperation("订单提交")
    public Result orderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("提交订单, {}", ordersSubmitDTO);
        return orderService.submitOrder(ordersSubmitDTO);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result queryHistory(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("查询历史订单：{}", ordersPageQueryDTO);
        return orderService.queryHistory(ordersPageQueryDTO);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("根据订单ID查询订单详情")
    public Result queryOrderDetail(@PathVariable Long id){
        log.info("查询订单详情：{}", id);
        return orderService.queryOrderDetail(id);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelOrder(@PathVariable Long id){
        log.info("取消订单：{}", id);
        return orderService.cancelOrder(id);
    }
}
