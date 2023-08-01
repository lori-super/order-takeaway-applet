package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.exception.UpdateFailException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes()).toUpperCase();
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public Result save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        //状态字段设置
        employee.setStatus(StatusConstant.ENABLE);
        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()).toUpperCase());
        //插入数据
        Boolean success = employeeMapper.insert(employee);
        if (success) {
            return Result.success();
        }
        return Result.error("增加员工失败！");
    }

    /**
     * 分页查询员工
     * @param pageQueryDTO
     * @return
     */
    @Override
    public Result queryPageList(EmployeePageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<Employee> employees = employeeMapper.queryPage(pageQueryDTO.getName());
        return Result.success(new PageResult(employees.getTotal(), employees.getResult()));
    }

    /**
     * 更新员工状态
     * @param status    需更新的状态值
     * @param id    员工id
     * @return
     */
    @Override
    public Result updateStatus(Integer status, Long id) {
        Long currentId = BaseContext.getCurrentId();
        if (currentId != 1){
            //throw new UpdateFailException(MessageConstant.ADMIN_QUANLI);
            return Result.error("非管理人员不可更改");
        }
        Employee.EmployeeBuilder employeeBuilder = Employee.builder().status(status)
                .id(id).updateTime(LocalDateTime.now()).updateUser(currentId);
        Boolean success = employeeMapper.update(employeeBuilder.build());
        if (!success){
            return Result.error("修改失败！");
        }
        return Result.success();
    }

    /**
     * 更具员工id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Result<EmployeeDTO> queryById(Long id) {
        Employee employee = employeeMapper.queryById(id);
        if (employee == null) {
            return Result.error("未找到该员工！");
        }
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return Result.success(employeeDTO);
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     * @return
     */
    @Override
    public Result updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        Boolean success = employeeMapper.update(employee);
        if (!success) {
            return Result.error("更新失败！");
        }
        return Result.success();
    }

}
