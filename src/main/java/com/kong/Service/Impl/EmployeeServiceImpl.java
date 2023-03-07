package com.kong.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.EmployeeMapper;
import com.kong.Service.EmployeeService;
import com.kong.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
