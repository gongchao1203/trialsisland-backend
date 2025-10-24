package com.trialsisland.service;

import com.trialsisland.dto.LoginRequest;
import com.trialsisland.dto.LoginResponse;
import com.trialsisland.entity.User;
import com.trialsisland.exception.BusinessException;
import com.trialsisland.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务类
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private JwtUtil jwtUtil;

    // 模拟用户数据库（实际项目中应该使用数据库）
    private static final Map<String, User> USER_DB = new HashMap<>();

    static {
        // 初始化一些测试用户
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin");
        user1.setPassword("123456");
        user1.setNickname("管理员");
        user1.setEmail("admin@example.com");
        user1.setStatus(1);
        user1.setCreateTime(LocalDateTime.now());
        USER_DB.put("admin", user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user");
        user2.setPassword("123456");
        user2.setNickname("普通用户");
        user2.setEmail("user@example.com");
        user2.setStatus(1);
        user2.setCreateTime(LocalDateTime.now());
        USER_DB.put("user", user2);
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应（包含token）
     */
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 查询用户
        User user = USER_DB.get(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码（实际项目中应该使用加密后的密码比对）
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId());
        log.info("用户[{}]登录成功，生成Token", username);

        // 返回登录响应
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getNickname());
    }

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUserById(Long userId) {
        return USER_DB.values().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}