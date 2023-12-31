package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User queryByOpenId(String openid);


    Boolean insert(User user);

    @Select("select * from user where id = #{userId}")
    User queryByUserId(Long userId);
}
