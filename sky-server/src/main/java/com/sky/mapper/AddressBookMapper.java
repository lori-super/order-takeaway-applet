package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    Boolean insert(AddressBook addressBook);

    @Select("select * from address_book")
    List<AddressBook> query();

    /**
     * 根据用户ID查询
     * @param currentId 用户ID
     * @return
     */
    @Select("select * from address_book where user_id = #{currentId}")
    List<AddressBook> queryById(Long currentId);

    Boolean update(AddressBook build);

    /**
     * 根据地址ID查询
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook queryByAddressId(Long id);
}
