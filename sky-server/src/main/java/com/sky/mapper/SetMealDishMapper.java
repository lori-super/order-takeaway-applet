package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    List<Long> queryByIds(List<Long> ids);

    Boolean insert(SetmealDish setmealDishe);

    Integer queryByIdForDish(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteById(Long id);

    @Select("select * from setmeal_dish where setmeal_id = #{id} ")
    List<DishItemVO> queryById(Long id);
}
