package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;

public interface ShoppingCartService {
    Result add(ShoppingCartDTO shoppingCartDTO);

    Result query();

    Result subOne(ShoppingCartDTO shoppingCartDTO);

    Result deleteByUserId();
}
