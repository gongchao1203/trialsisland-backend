# JWT登录系统使用指南

## 功能概述

本项目实现了基于JWT的用户登录认证系统，主要功能包括：

1. 用户登录，生成JWT Token
2. 请求拦截器验证Token
3. Token解析获取用户ID
4. 全局异常处理

## 项目结构

```
src/main/java/com/trialsisland/
├── config/
│   └── WebConfig.java                 # Web配置，注册JWT拦截器
├── controller/
│   └── AuthController.java            # 认证控制器，提供登录接口
├── dto/
│   ├── LoginRequest.java              # 登录请求DTO
│   └── LoginResponse.java             # 登录响应DTO
├── entity/
│   └── User.java                      # 用户实体类
├── exception/
│   ├── AuthenticationException.java   # 认证异常
│   └── GlobalExceptionHandler.java    # 全局异常处理器
├── interceptor/
│   └── JwtInterceptor.java            # JWT拦截器
├── service/
│   └── UserService.java               # 用户服务类
└── utils/
    └── JwtUtil.java                   # JWT工具类
```

## 核心功能说明

### 1. JWT工具类 (JwtUtil)

- **generateToken(Long userId)**: 根据用户ID生成JWT Token，有效期7天
- **parseToken(String token)**: 解析Token获取用户ID，如果Token无效或过期返回null
- **validateToken(String token)**: 验证Token是否有效

### 2. JWT拦截器 (JwtInterceptor)

拦截所有请求（除了登录、注册等公开接口），验证请求头中的Token：

- 从请求头的 `Authorization` 字段获取Token
- 如果Token为null或空，抛出 `AuthenticationException`，提示"用户不存在，请先登录"
- 解析Token获取用户ID
- 如果用户ID为null或0，抛出 `AuthenticationException`，提示"用户不存在，请先登录"
- 验证通过后，将用户ID存储到request的attribute中，供后续使用

### 3. 全局异常处理器 (GlobalExceptionHandler)

处理以下异常：

- **Exception**: 系统异常，返回500错误
- **BusinessException**: 业务异常，返回500错误和自定义消息
- **AuthenticationException**: 认证异常，返回401错误和自定义消息
- **BindException**: 参数校验异常，返回500错误

### 4. 用户服务 (UserService)

- **login(LoginRequest)**: 用户登录，验证用户名和密码，生成Token
- **getUserById(Long userId)**: 根据用户ID获取用户信息

## API接口说明

### 1. 用户登录

**接口**: `POST /api/auth/login`

**请求参数**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "nickname": "管理员"
  }
}
```

### 2. 获取当前用户信息

**接口**: `GET /api/auth/me`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@example.com",
    "status": 1,
    "createTime": "2024-01-01T00:00:00"
  }
}
```

### 3. 测试受保护的接口

**接口**: `GET /api/auth/protected`

**请求头**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "您好，用户ID: 1，这是一个受保护的接口"
}
```

## 测试用户

系统内置了两个测试用户：

1. **管理员**
   - 用户名: `admin`
   - 密码: `123456`
   - 用户ID: 1

2. **普通用户**
   - 用户名: `user`
   - 密码: `123456`
   - 用户ID: 2

## 使用示例

### 使用curl测试

1. **登录获取Token**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

2. **使用Token访问受保护接口**:
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 使用Postman测试

1. **登录**:
   - Method: POST
   - URL: `http://localhost:8080/api/auth/login`
   - Body (JSON):
     ```json
     {
       "username": "admin",
       "password": "123456"
     }
     ```

2. **访问受保护接口**:
   - Method: GET
   - URL: `http://localhost:8080/api/auth/me`
   - Headers:
     - Key: `Authorization`
     - Value: `Bearer <复制登录返回的token>`

## 错误处理

### 1. Token为空或null

**响应**:
```json
{
  "code": 401,
  "message": "用户不存在，请先登录",
  "data": null
}
```

### 2. Token无效或过期

**响应**:
```json
{
  "code": 401,
  "message": "用户不存在，请先登录",
  "data": null
}
```

### 3. 用户名或密码错误

**响应**:
```json
{
  "code": 500,
  "message": "用户名或密码错误",
  "data": null
}
```

## 注意事项

1. **JWT密钥**: 当前密钥硬编码在代码中，生产环境应该配置在配置文件中并使用环境变量
2. **用户数据**: 当前使用内存Map存储用户，实际项目应该使用数据库
3. **密码加密**: 当前密码明文存储和比对，实际项目应该使用BCrypt等加密算法
4. **Token刷新**: 当前未实现Token刷新机制，可根据需要添加
5. **跨域配置**: 项目已配置CORS，允许跨域访问

## 扩展功能建议

1. 集成Spring Security实现更完善的权限管理
2. 添加用户注册功能
3. 实现Token刷新机制
4. 添加登录日志记录
5. 实现用户权限和角色管理
6. 集成数据库（如MySQL）存储用户数据
7. 添加图形验证码或短信验证码
8. 实现单点登录（SSO）