package com.trialsisland.config;

import com.trialsisland.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除登录、注册等不需要认证的接口
                // 注意：由于配置了context-path=/api，拦截器看到的路径不包含/api前缀
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/auth/register",
                        "/hello",
                        "/error",
                        // 排除静态资源
                        "/*.html",
                        "/*.css",
                        "/*.js",
                        "/favicon.ico"
                );
    }
}