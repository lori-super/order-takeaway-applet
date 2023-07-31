package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    @AutoFill(OperationType.INSERT)
    Boolean insert(Dish dish);

    Page<DishVO> queryPage(DishPageQueryDTO dish);

    @Select("select * from dish where id = #{id}")
    Dish queryById(Long id);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    @AutoFill(OperationType.UPDATE)
    Boolean update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> queryByCategoryId(Long categoryId);
}
