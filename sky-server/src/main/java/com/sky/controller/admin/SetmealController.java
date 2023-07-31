package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Api("套餐管理类")
public class SetmealController {
    @Autowired
    private SetmealService setMealService;

    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐， {}", setmealDTO);
        return setMealService.insert(setmealDTO);
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result queryPage(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询， {}", setmealPageQueryDTO);
        return setMealService.queryPage(setmealPageQueryDTO);
    }

    /**
     * 更改套餐状态
     * @param status
     * @param id 套餐id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("更改套餐状态")
    public Result updateStatus(@PathVariable Integer status,@RequestParam Long id){
        log.info("修改套餐状态，id = {}, status = {}", id, status);
        return setMealService.updateStatus(status, id);
    }

    @GetMapping("{id}")
    @ApiOperation("根据套餐ID查询")
    public Result queryById(@PathVariable Long id){
        log.info("查询套餐，id = {}", id);
        return setMealService.queryById(id);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐，{}", setmealDTO);
        return setMealService.update(setmealDTO);
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除套餐，{}", ids);
        return setMealService.delete(ids);
    }
}
