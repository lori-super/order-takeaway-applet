package com.sky.controller.admin;

import com.sky.constant.RedisConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@Api(tags = "管理端shop接口")
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PutMapping("{status}")
    @ApiOperation("设置营业状态")
    public Result updateStatus(@PathVariable Integer status){
        log.info("设置店铺的营业状态为：{}",status == 1 ? "营业中" : "打烊中");
        String key = RedisConstant.SHOP_STATUS;
        stringRedisTemplate.opsForValue().set(key, status.toString());
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result queryStatus(){
        String key = RedisConstant.SHOP_STATUS;
        String currentStatus = stringRedisTemplate.opsForValue().get(key);
        Integer integer = Integer.valueOf(currentStatus);
        log.info("获取店铺的营业状态为：{}",integer == 1 ? "营业中" : "打烊中");
        return Result.success(integer);
    }
}
