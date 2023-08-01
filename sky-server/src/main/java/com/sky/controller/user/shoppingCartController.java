package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "购物车接口类")
@Slf4j
public class shoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车：{}", shoppingCartDTO);
        return shoppingCartService.add(shoppingCartDTO);
    }

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result query(){
        log.info("查看购物车");
        return shoppingCartService.query();
    }

    @PostMapping("/sub")
    @ApiOperation("减少购物车中菜品数量")
    public Result subCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除一个商品: {}", shoppingCartDTO);
        return shoppingCartService.subOne(shoppingCartDTO);
    }

    @DeleteMapping("/clean")
    @ApiOperation("根据用户ID清空购物车")
    public Result deleteByUserId(){
        log.info("清空购物车");
        return shoppingCartService.deleteByUserId();
    }
}
