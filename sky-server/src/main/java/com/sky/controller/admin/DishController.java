package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api("菜品管理接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品接口")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品，{}", dishDTO);
        return dishService.save(dishDTO);
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result queryPage(DishPageQueryDTO dto){
        log.info("菜品分页查询，{}", dto);
        return dishService.queryPage(dto);
    }

    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("菜品批量删除，{}", ids);
        return dishService.deleteDish(ids);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("更改菜品状态")
    public Result updateStatus(@PathVariable("status") Integer status, @RequestParam Long id){
        log.info("修改员工状态，员工号:{},状态:{}", id, status);
        return dishService.updateStatus(status, id);
    }

    @GetMapping("{id}")
    @ApiOperation("根据菜品ID查询菜品信息")
    public Result<DishVO> queryById(@PathVariable Long id){
        log.info("查询菜品信息, 菜品ID{}", id);
        return dishService.queryById(id);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息, 修改信息：{}", dishDTO);
        return dishService.update(dishDTO);
    }


    @GetMapping("/list")
    @ApiOperation("套餐中，添加菜品接口")
    public Result queryCategoryId(Long categoryId){
        log.info("根据分类ID查询菜品， categoryId{}", categoryId);
        return dishService.queryCategoryId(categoryId);
    }
}
