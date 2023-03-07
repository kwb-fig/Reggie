package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kong.Service.EmployeeService;
import com.kong.common.R;
import com.kong.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    //@RequestBody 用于从前端获取对象
    @PostMapping("/login")
    public R<Employee> login(HttpSession session , @RequestBody Employee employee){
        //1.对密码MD5加密；
        String password = employee.getPassword();
        String MD5password = DigestUtils.md5DigestAsHex(password.getBytes());

        //    2.根据页面提交的username查询数据库
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //    3.如果没查到登陆失败
        if(emp==null){
            return R.error("用户名不存在");
        }

        //4.如果密码错误则则失败
        if(!MD5password.equals(emp.getPassword())){
            return R.error("密码错误");
        }

        //5.查看员工状态，被禁用则失败
        if(emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        session.invalidate();
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpSession session){
        log.info("新增员工信息,{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //employee.setCreateUser((Long) session.getAttribute("employee"));
        //employee.setUpdateUser((Long) session.getAttribute("employee"));

        employeeService.save(employee);
        return R.success("添加成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    //编辑禁用启用员工信息
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpSession session){
        //Long id = (Long) session.getAttribute("employee");

        //employee.setUpdateUser(id);
        //employee.setUpdateTime(LocalDateTime.now());


        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    //编辑员工信息时先回显员工信息;
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查到对应员工信息");
    }

}
