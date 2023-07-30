package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user,status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    @AutoFill(OperationType.INSERT)
    Boolean insert(Employee employee);



    Page<Employee> queryPage(String name);

    @Update("update employee set status = #{status} where id = #{id}")
    Boolean updateStatus(Integer status, Long id);

    @Select("select * from employee where id = #{id}")
    Employee queryById(Long id);

    @AutoFill(OperationType.UPDATE)
    Boolean update(Employee employee);
}
