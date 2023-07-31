package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     * @return
     */
    @AutoFill(OperationType.INSERT)
    Boolean insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmeal
     * @return
     */
    Page<SetmealVO> queryPage(Setmeal setmeal);

    @AutoFill(OperationType.UPDATE)
    Boolean update(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    SetmealVO queryById(Long id);

    @Delete("delete from setmeal where id = #{id}")
    void deleteById(Long id);
}
