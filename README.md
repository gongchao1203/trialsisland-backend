# Trials Island Backend

基于 Spring Boot 3.2.0 和 Maven 的后端项目脚手架。

## 技术栈

- Spring Boot 3.2.0
- Java 17
- Maven
- Spring Data JPA
- MySQL
- Lombok

## 项目结构

```
trialsisland-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── trialsisland/
│   │   │           ├── TrialsIslandApplication.java    # 主应用类
│   │   │           ├── common/                         # 公共类
│   │   │           │   └── Result.java                 # 统一响应结果
│   │   │           ├── config/                         # 配置类
│   │   │           │   └── CorsConfig.java            # 跨域配置
│   │   │           ├── controller/                     # 控制器
│   │   │           │   └── HelloController.java       # 示例控制器
│   │   │           └── exception/                      # 异常处理
│   │   │               ├── BusinessException.java     # 业务异常
│   │   │               └── GlobalExceptionHandler.java # 全局异常处理器
│   │   └── resources/
│   │       └── application.yml                         # 应用配置文件
│   └── test/
│       └── java/
│           └── com/
│               └── trialsisland/
│                   └── TrialsIslandApplicationTests.java
├── .gitignore
├── pom.xml
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 配置数据库

1. 创建数据库：
```sql
CREATE DATABASE trialsisland DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/trialsisland
    username: your_username
    password: your_password
```

### 运行项目

1. 安装依赖：
```bash
mvn clean install
```

2. 启动项目：
```bash
mvn spring-boot:run
```

3. 访问测试接口：
```
http://localhost:8080/api/hello
```

## 主要功能

- ✅ 统一响应结果封装
- ✅ 全局异常处理
- ✅ 跨域配置
- ✅ JPA 集成
- ✅ 参数校验
- ✅ 热部署支持

## 开发说明

### 添加新的控制器

在 `com.trialsisland.controller` 包下创建新的控制器类，使用 `@RestController` 注解。

### 添加新的实体类

在 `com.trialsisland.entity` 包下创建实体类，使用 JPA 注解。

### 添加新的服务

在 `com.trialsisland.service` 包下创建服务接口和实现类。

## License

MIT