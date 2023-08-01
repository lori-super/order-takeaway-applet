package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    @Transactional
    public Result add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.query(shoppingCart);
        if (shoppingCartList != null && shoppingCartList.size() > 0){
            ShoppingCart shoppingCart1 = shoppingCartList.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            Boolean success = shoppingCartMapper.updateNumberById(shoppingCart1);
            return success ? Result.success() : Result.error("增加数量失败！");
        }

        if (dishId == null){
            SetmealVO setmealVO = setmealMapper.queryById(setmealId);
            shoppingCart.setName(setmealVO.getName());
            shoppingCart.setAmount(setmealVO.getPrice());
            shoppingCart.setImage(setmealVO.getImage());
        }else{
            Dish dish = dishMapper.queryById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setAmount(dish.getPrice());
            shoppingCart.setImage(dish.getImage());
        }
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setNumber(1);
        Boolean success = shoppingCartMapper.insert(shoppingCart);
        return success ? Result.success() : Result.error("增加数量失败！");
    }

    @Override
    public Result<List<ShoppingCart>> query() {
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryAll();
        return Result.success(shoppingCartList);
    }

    @Override
    @Transactional
    public Result subOne(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = BeanUtil.copyProperties(shoppingCartDTO, ShoppingCart.class);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.query(shoppingCart);
        ShoppingCart cart = shoppingCartList.get(0);
        if (cart.getNumber() > 1){
            cart.setNumber(cart.getNumber() - 1);
            Boolean success = shoppingCartMapper.updateNumberById(cart);
            return success ? Result.success() : Result.error("sub-one修改数量出错！");
        }
        Boolean success = shoppingCartMapper.deleteById(cart);
        return success ? Result.success() : Result.error("sub-one删除出错！");
    }

    @Override
    @Transactional
    public Result deleteByUserId() {
        Boolean success = shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
        return success ? Result.success() : Result.error("清空购物车出错！");
    }
}
