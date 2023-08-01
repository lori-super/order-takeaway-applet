package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入
     * @param flavors
     * @return
     */
    Boolean insertList(List<DishFlavor> flavors);

    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteById(Long id);

    /**
     * 按草品ID查询
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> queryByDishId(Long id);

}
