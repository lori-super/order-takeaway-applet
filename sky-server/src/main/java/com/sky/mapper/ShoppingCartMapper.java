package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    Boolean insert(ShoppingCart shoppingCart);

    List<ShoppingCart> query(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    Boolean updateNumberById(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart")
    List<ShoppingCart> queryAll();

    @Delete("delete from shopping_cart  where id = #{id}")
    Boolean deleteById(ShoppingCart cart);

    @Delete("delete from shopping_cart where user_id = #{currentId}")
    Boolean deleteByUserId(Long currentId);
}
