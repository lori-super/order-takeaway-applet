package com.sky.service;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.enumeration.OperationType;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface DishService {
    /**
     * 菜品添加
     * @param dishDTO
     * @return
     */
    Result save(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    Result queryPage(DishPageQueryDTO dto);

    Result deleteDish(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    Result updateStatus(Integer status, Long id);

    Result<DishVO> queryById(Long id);

    Result update(DishDTO dishDTO);
}
