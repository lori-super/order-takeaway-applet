package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    Result submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    Result queryHistory(OrdersPageQueryDTO ordersPageQueryDTO);

    Result queryOrderDetail(Long id);

    Result cancelOrder(Long id);

    Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    Result<OrderVO> queryById(Long id);

    Result<OrderVO> completeOrder(Long id);

    Result<OrderVO> confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    Result cancelAdminOrder(OrdersCancelDTO ordersCancelDTO);

    Result rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    Result deliveryOrder(Long id);

    Result<OrderStatisticsVO> statisticsOrder();
}
