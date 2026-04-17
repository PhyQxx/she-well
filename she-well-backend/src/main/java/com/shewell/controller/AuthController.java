package com.shewell.controller;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shewell.entity.User;
import com.shewell.service.SmsService;
import com.shewell.service.UserService;
import com.shewell.util.JwtUtil;
import com.shewell.util.Result;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final long SMS_CODE_TTL_MINUTES = 5;

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/sendCode")
    public Result<Void> sendCode(@RequestBody SendCodeDTO dto) {
        if (dto.getPhone() == null || dto.getPhone().length() != 11) {
            return Result.fail("手机号格式不正确");
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(SMS_CODE_PREFIX + dto.getPhone(), code, SMS_CODE_TTL_MINUTES, TimeUnit.MINUTES);
        smsService.sendCode(dto.getPhone(), code);
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        if (dto.getPhone() == null || dto.getPhone().isEmpty()) {
            return Result.fail("手机号不能为空");
        }

        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone()));

        // 验证码验证（新用户也需要）
        if (dto.getCode() != null && !dto.getCode().isEmpty()) {
            String cachedCode = redisTemplate.opsForValue().get(SMS_CODE_PREFIX + dto.getPhone());
            if (cachedCode == null || !cachedCode.equals(dto.getCode())) {
                return Result.fail("验证码错误或已过期");
            }
            redisTemplate.delete(SMS_CODE_PREFIX + dto.getPhone());
        } else if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            // 密码登录需要已有用户
            if (user == null) return Result.fail("用户不存在");
            if (user.getPassword() == null || !BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
                return Result.fail("密码错误");
            }
        } else {
            return Result.fail("请输入验证码或密码");
        }

        // 自动注册：验证码登录时用户不存在则自动创建
        if (user == null) {
            user = new User();
            user.setPhone(dto.getPhone());
            user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
            user.setNickname("用户" + dto.getPhone().substring(7));
            user.setStatus(1);
            user.setCurrentMode("period");
            userService.save(user);
        } else {
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.fail("账号已被禁用");
            }
        }

        String token = JwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return Result.ok(vo);
    }

    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO dto) {
        if (dto.getPhone() == null || dto.getPhone().isEmpty()) {
            return Result.fail("手机号不能为空");
        }
        if (dto.getCode() == null || dto.getCode().isEmpty()) {
            return Result.fail("验证码不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            return Result.fail("密码不能少于6位");
        }

        // 验证短信验证码
        String cachedCode = redisTemplate.opsForValue().get(SMS_CODE_PREFIX + dto.getPhone());
        if (cachedCode == null || !cachedCode.equals(dto.getCode())) {
            return Result.fail("验证码错误或已过期");
        }

        // 检查手机号是否已注册
        long count = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone()));
        if (count > 0) {
            return Result.fail("该手机号已注册");
        }

        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setNickname("用户" + dto.getPhone().substring(7));
        user.setStatus(1);
        userService.save(user);

        redisTemplate.delete(SMS_CODE_PREFIX + dto.getPhone());

        String token = JwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        return Result.ok(vo);
    }

    @PostMapping("/admin-login")
    public Result<LoginVO> adminLogin(@RequestBody AdminLoginDTO dto) {
        if (dto.getUsername() == null || dto.getPassword() == null) {
            return Result.fail("用户名和密码不能为空");
        }
        // 管理员账号验证 - 从数据库 system_config 表或硬编码
        // TODO: 后续迁移到数据库管理
        if ("admin".equals(dto.getUsername()) && "admin123".equals(dto.getPassword())) {
            String token = JwtUtil.generateToken(0L); // admin 用 userId=0
            LoginVO vo = new LoginVO();
            vo.setToken(token);
            vo.setUserId(0L);
            vo.setNickname("管理员");
            return Result.ok(vo);
        }
        return Result.fail("用户名或密码错误");
    }

    @GetMapping("/me")
    public Result<User> me(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    @Data
    public static class SendCodeDTO { private String phone; }
    @Data
    public static class LoginDTO { private String phone; private String code; private String password; }
    @Data
    public static class RegisterDTO { private String phone; private String code; private String password; }
    @Data
    public static class AdminLoginDTO { private String username; private String password; }
    @Data
    public static class LoginVO {
        private String token;
        private Long userId;
        private String nickname;
        private String avatar;
    }
}
