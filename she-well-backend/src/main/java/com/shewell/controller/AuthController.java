package com.shewell.controller;

import com.shewell.entity.User;
import com.shewell.service.UserService;
import com.shewell.util.JwtUtil;
import com.shewell.util.Result;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendCode")
    public Result<Void> sendCode(@RequestBody SendCodeDTO dto) {
        // 开发环境：打印验证码到控制台
        System.out.println("【SheWell】验证码: 123456 -> " + dto.getPhone());
        return Result.ok();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        // 演示模式：直接创建/返回用户
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setNickname("用户" + dto.getPhone().substring(7));
        user.setStatus(1);
        user.setPassword("NOLOGIN"); // 演示模式不设密码
        userService.save(user);
        String token = JwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        return Result.ok(vo);
    }

    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO dto) {
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setNickname("新用户");
        user.setStatus(1);
        userService.save(user);
        String token = JwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        return Result.ok(vo);
    }

    @GetMapping("/me")
    public Result<User> me(Long userId) {
        User user = userService.getById(userId);
        return Result.ok(user);
    }

    @Data
    public static class SendCodeDTO { private String phone; }
    @Data
    public static class LoginDTO { private String phone; private String code; private String password; }
    @Data
    public static class RegisterDTO { private String phone; private String code; private String password; }
    @Data
    public static class LoginVO { private String token; private Long userId; private String nickname; }
}
