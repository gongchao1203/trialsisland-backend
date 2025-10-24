package com.trialsisland.controller;

import com.trialsisland.common.Result;
import com.trialsisland.dto.LoginRequest;
import com.trialsisland.dto.LoginResponse;
import com.trialsisland.entity.User;
import com.trialsisland.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应（包含token）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求：{}", loginRequest.getUsername());
        LoginResponse response = userService.login(loginRequest);
        return Result.success(response);
    }

    /**
     * 获取当前登录用户信息（需要JWT认证）
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        // 从request中获取用户ID（由JWT拦截器设置）
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取用户信息：userId={}", userId);
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 清除密码信息
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 测试受保护的接口
     * @param request HTTP请求
     * @return 测试消息
     */
    @GetMapping("/protected")
    public Result<String> protectedEndpoint(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success("您好，用户ID: " + userId + "，这是一个受保护的接口");
    }
}