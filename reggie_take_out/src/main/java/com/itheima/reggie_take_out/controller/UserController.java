package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie_take_out.common.Result;
import com.itheima.reggie_take_out.entity.User;
import com.itheima.reggie_take_out.service.UserService;
import com.itheima.reggie_take_out.utils.SMSUtils;
import com.itheima.reggie_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 移动端用户登录
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param session
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(HttpSession session, @RequestBody User user) {
        // 获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code= {}", code);

//            // 调用阿里云短信服务API完成发送短信
//            SMSUtils.sendMessage("", "", phone, code);

//            // 需要将生成的验证码保存到Session
//            session.setAttribute(phone, code);

            // 将生成的验证码缓存到Redis中, 并且设置有效期5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return Result.success("手机验证码短信发送成功");
        }
        return Result.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param session
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(HttpSession session, @RequestBody Map map) {
        log.info(map.toString());

        // 获取手机号
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = map.get("code").toString();

//        // 从Session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        // 从Redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)) {
            // 如果能够比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if(user == null) {
                // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            // 如果用户登录成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);
            return Result.success(user);
        }

        return Result.error("登录失败");
    }
}
