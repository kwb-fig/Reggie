package com.kong.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kong.Service.UserService;
import com.kong.Utils.SMSUtils;
import com.kong.Utils.ValidateCodeUtils;
import com.kong.common.R;
import com.kong.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成短信发送
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //session.setAttribute(phone,code);
            //将验证码存入redis中
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        //Object codeInSession = session.getAttribute(phone);

        //从redis中取出验证码
        Object codeInSession=redisTemplate.opsForValue().get(phone);

        if(codeInSession!=null && codeInSession.equals(code)){

            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();

            lambdaQueryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(lambdaQueryWrapper);

            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setCreateTime(LocalDateTime.now());
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //如果登录成功,就将验证码移除
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }
    @PostMapping("/loginout")
    public R<String> logout() {
        userService.logout();
        return R.success("退出成功");
    }

    @GetMapping("/getUserInfo/{phone}")
    public R<User> getUserInfo(@PathVariable String phone) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(phone != null, User::getPhone, phone);
        User byId = userService.getOne(queryWrapper);
        if (byId != null) {
            return R.success(byId);
        }
        return R.error("用户信息查询失败！");
    }

    //完善前台用户信息
    @PutMapping
    public R<String> addUser(@RequestBody User user) {
        userService.updateById(user);
        return R.success("修改成功！");
    }


    /**
     * 会员分页条件查询
     *
     * @param page
     * @param pageSize
     * @param phone
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String phone) {
        //构造分页构造器
        Page pageInfo = new Page<>(page, pageSize);
        return userService.pageQuery(pageInfo, phone);
    }
    /**
     * 根据ID修改会员信息
     *
     * @param user
     * @return
     */
    @PutMapping("/updateStatus")
    public R<String> update(@RequestBody User user) {
        userService.updateById(user);
        return R.success("会员信息修改成功！");
    }


    @GetMapping("/getUserCount")
    public R<Long> getUserCount() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        long count = userService.count(queryWrapper);
        return R.success(count);
    }
}
