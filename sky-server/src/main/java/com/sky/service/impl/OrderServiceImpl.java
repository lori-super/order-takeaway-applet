package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    @Transactional
    public Result submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //异常情况的处理（收货地址为空、超出配送范围、购物车为空）
        AddressBook addressBook = addressBookMapper.queryByAddressId(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //查询当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.query(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //构造订单数据
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,order);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(userId);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());

        //向订单表插入1条数据
        orderMapper.insert(order);

        //订单明细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }

        //向明细表插入n条数据
        orderDetailMapper.insertBatch(orderDetailList);

        //清理购物车中的数据
        shoppingCartMapper.deleteByUserId(userId);

        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(order.getId())
                .orderNumber(order.getNumber())
                .orderAmount(order.getAmount())
                .orderTime(order.getOrderTime())
                .build();

        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     * @throws Exception
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.queryByUserId(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        // vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    @Override
    public Result queryHistory(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<OrderVO> ordersList = orderMapper.queryPage(ordersPageQueryDTO);
        for (OrderVO orderVO : ordersList.getResult()) {
            List<OrderDetail> orderDetailList = orderDetailMapper.queryByOrderId(orderVO.getId());
            orderVO.setOrderDetailList(orderDetailList);
        }
        return Result.success(new PageResult(ordersList.getTotal(), ordersList));
    }

    @Override
    public Result queryOrderDetail(Long id) {

        Orders orders = orderMapper.queryById(id);
        OrderVO orderVO = BeanUtil.copyProperties(orders, OrderVO.class);
        List<OrderDetail> orderDetailList = orderDetailMapper.queryByOrderId(orderVO.getId());
        orderVO.setOrderDetailList(orderDetailList);
        return Result.success(orderVO);
    }

    @Override
    @Transactional
    public Result cancelOrder(Long id) {
        Orders order = orderMapper.queryById(id);
        if (order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 2){
            throw new OrderBusinessException((MessageConstant.ORDER_STATUS_ERROR));
        }
        Orders orders = new Orders();
        orders.setId(order.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        Boolean success = orderMapper.update(orders);

        return success ? Result.success() : Result.error("取消失败！");
    }

    /**
     * 客户端 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderVO> orderVOS = orderMapper.queryPage(ordersPageQueryDTO);
        for (OrderVO orderVO : orderVOS.getResult()) {
            List<OrderDetail> orderDetailList = orderDetailMapper.queryByOrderId(orderVO.getId());
            orderVO.setOrderDishes(concatStr(orderDetailList));
        }
        return Result.success(new PageResult(orderVOS.getTotal(), orderVOS.getResult()));
    }

    @Override
    public Result<OrderVO> queryById(Long id) {
        Orders orders = orderMapper.queryById(id);
        OrderVO orderVO = BeanUtil.copyProperties(orders, OrderVO.class);
        List<OrderDetail> orderDetailList = orderDetailMapper.queryByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return Result.success(orderVO);
    }

    @Override
    @Transactional
    public Result<OrderVO> completeOrder(Long id) {
        Boolean success = orderMapper.update(Orders.builder().id(id).status(Orders.COMPLETED).build());
        return success ? Result.success() : Result.error("更新失败！");
    }

    @Override
    public Result<OrderVO> confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Boolean success = orderMapper.update(Orders.builder().id(ordersConfirmDTO.getId()).status(Orders.CONFIRMED).build());
        return success ? Result.success() : Result.error("接单失败！");
    }

    @Override
    public Result cancelAdminOrder(OrdersCancelDTO ordersCancelDTO) {
        Orders order = orderMapper.queryById(ordersCancelDTO.getId());
        if (order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 4){
            throw new OrderBusinessException((MessageConstant.ORDER_STATUS_ERROR));
        }
        Orders orders = new Orders();
        orders.setId(order.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        Boolean success = orderMapper.update(orders);

        return success ? Result.success() : Result.error("取消失败！");
    }

    @Override
    public Result rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders order = orderMapper.queryById(ordersRejectionDTO.getId());
        if (order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 3){
            throw new OrderBusinessException((MessageConstant.ORDER_STATUS_ERROR));
        }
        Orders orders = new Orders();
        orders.setId(order.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        Boolean success = orderMapper.update(orders);

        return success ? Result.success() : Result.error("取消失败！");
    }

    @Override
    public Result deliveryOrder(Long id) {
        Orders orders = orderMapper.queryById(id);
        Orders order = new Orders();

        order.setDeliveryStatus(1);
        order.setStatus(Orders.DELIVERY_IN_PROGRESS);
        order.setId(id);
        Boolean success = orderMapper.update(order);
        return success ? Result.success() : Result.error("派送失败！");
    }

    @Override
    public Result<OrderStatisticsVO> statisticsOrder() {
        Integer toBeConfirmed = orderMapper.count(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.count(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.count(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return Result.success(orderStatisticsVO);
    }

    public String concatStr(List<OrderDetail> orderDetailList){
        List<String> collect = orderDetailList.stream().map(orderDetail -> {
            return orderDetail.getName() + "*" + orderDetail.getNumber() + ";";
        }).collect(Collectors.toList());

        return String.join("", collect);
    }
}
