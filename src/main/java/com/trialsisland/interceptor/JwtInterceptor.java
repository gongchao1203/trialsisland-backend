package com.trialsisland.interceptor;

import com.trialsisland.exception.AuthenticationException;
import com.trialsisland.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        
        // 如果token为空，抛出认证异常
        if (token == null || token.isEmpty()) {
            log.warn("请求头中未找到Token");
            throw new AuthenticationException("用户不存在，请先登录");
        }

        // 如果token以"Bearer "开头，去掉前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 解析token获取用户ID
        Long userId = jwtUtil.parseToken(token);
        
        // 如果userId为null或0，说明token无效或已过期
        if (userId == null || userId == 0) {
            log.warn("Token无效或已过期");
            throw new AuthenticationException("用户不存在，请先登录");
        }

        // 将用户ID存储到request中，供后续使用
        request.setAttribute("userId", userId);
        log.info("用户[{}]通过JWT认证", userId);
        
        return true;
    }
}