# 在线做题系统API使用指南

## 功能概述

本系统实现了一个支持并发的在线做题系统，主要功能包括：

1. 获取题目（支持获取单个、随机、批量题目）
2. 提交答案并自动判题
3. 查看答题历史和得分统计
4. 使用线程安全的数据结构保证并发场景下的数据一致性

## 项目结构

```
src/main/java/com/trialsisland/
├── controller/
│   └── QuestionController.java        # 题目控制器
├── dto/
│   ├── QuestionResponse.java          # 题目响应DTO
│   ├── SubmitAnswerRequest.java       # 提交答案请求DTO
│   └── SubmitAnswerResponse.java      # 提交答案响应DTO
├── entity/
│   ├── Question.java                  # 题目实体类
│   └── UserAnswer.java                # 用户答题记录
└── service/
    └── QuestionService.java           # 题目服务类
```

## 并发安全设计

### 1. 使用ConcurrentHashMap

系统使用 `ConcurrentHashMap` 存储题库和用户答题记录，保证在高并发场景下的线程安全：

```java
private final Map<Long, Question> questionBank = new ConcurrentHashMap<>();
private final Map<Long, UserAnswer> userAnswers = new ConcurrentHashMap<>();
```

### 2. 使用AtomicLong生成唯一ID

使用 `AtomicLong` 生成答题记录的唯一ID，保证并发场景下ID不重复：

```java
private final AtomicLong answerIdGenerator = new AtomicLong(1);
```

### 3. 无状态服务设计

所有接口都是无状态的，每次请求独立处理，不依赖会话状态，适合水平扩展。

## API接口说明

### 1. 获取单个题目

**接口**: `GET /api/questions/{questionId}`

**请求头**:
```
Authorization: Bearer <token>
```

**路径参数**:
- `questionId`: 题目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "question": "下列各项中，不属于我国古代四大发明的是（  ）",
    "imageUrl": null,
    "options": [
      "A.造纸术",
      "B.针灸术",
      "C.火药",
      "D.活字印刷术"
    ],
    "type": "choice",
    "score": 10
  }
}
```

### 2. 随机获取一道题目

**接口**: `GET /api/questions/random`

**请求头**:
```
Authorization: Bearer <token>
```

**响应示例**: 同上

### 3. 获取多道题目（批量获取）

**接口**: `GET /api/questions/list?count=5`

**请求头**:
```
Authorization: Bearer <token>
```

**查询参数**:
- `count`: 获取题目数量，默认5道，可选参数

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "question": "下列各项中，不属于我国古代四大发明的是（  ）",
      "imageUrl": null,
      "options": ["A.造纸术", "B.针灸术", "C.火药", "D.活字印刷术"],
      "type": "choice",
      "score": 10
    },
    {
      "id": 2,
      "question": "甲、乙两人在周长为400米的环形跑道上同时同地同向而行，甲的速度是每秒6米，乙的速度是每秒4米，那么甲第一次追上乙需要多少秒？（  ）",
      "imageUrl": null,
      "options": ["A.100", "B.150", "C.200", "D.250"],
      "type": "choice",
      "score": 15
    }
  ]
}
```

### 4. 提交答案

**接口**: `POST /api/questions/submit`

**请求头**:
```
Authorization: Bearer <token>
```

**请求参数**:
```json
{
  "questionId": 1,
  "answer": "B",
  "duration": 30
}
```

**参数说明**:
- `questionId`: 题目ID（必填）
- `answer`: 用户答案，选择题为A/B/C/D，其他类型为文本内容（必填）
- `duration`: 答题时长（秒），可选

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isCorrect": true,
    "score": 10,
    "correctAnswer": "B",
    "explanation": null
  }
}
```

### 5. 获取用户答题历史

**接口**: `GET /api/questions/history`

**请求头**:
```
Authorization: Bearer <token>
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "questionId": 1,
      "userAnswer": "B",
      "isCorrect": true,
      "score": 10,
      "duration": 30,
      "submitTime": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "userId": 1,
      "questionId": 2,
      "userAnswer": "A",
      "isCorrect": false,
      "score": 0,
      "duration": 45,
      "submitTime": "2024-01-15T10:31:00"
    }
  ]
}
```

### 6. 获取用户总得分

**接口**: `GET /api/questions/score`

**请求头**:
```
Authorization: Bearer <token>
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 65
}
```

## 内置题目

系统内置了5道测试题目：

1. **题目1**（ID: 1）- 四大发明，难度2，分值10
2. **题目2**（ID: 2）- 追及问题，难度3，分值15
3. **题目3**（ID: 3）- 语言理解，难度4，分值20
4. **题目4**（ID: 4）- 数学计算，难度3，分值15
5. **题目5**（ID: 5）- 逻辑推理，难度5，分值25

## 使用示例

### 使用curl测试

1. **获取题目列表**:
```bash
curl -X GET "http://localhost:8080/api/questions/list?count=3" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

2. **提交答案**:
```bash
curl -X POST http://localhost:8080/api/questions/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "questionId": 1,
    "answer": "B",
    "duration": 30
  }'
```

3. **查看答题历史**:
```bash
curl -X GET http://localhost:8080/api/questions/history \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

4. **查看总得分**:
```bash
curl -X GET http://localhost:8080/api/questions/score \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 使用Postman测试

#### 1. 获取题目列表
- Method: GET
- URL: `http://localhost:8080/api/questions/list?count=5`
- Headers:
  - Key: `Authorization`
  - Value: `Bearer <token>`

#### 2. 提交答案
- Method: POST
- URL: `http://localhost:8080/api/questions/submit`
- Headers:
  - Key: `Authorization`
  - Value: `Bearer <token>`
  - Key: `Content-Type`
  - Value: `application/json`
- Body (JSON):
```json
{
  "questionId": 1,
  "answer": "B",
  "duration": 30
}
```

## 并发测试建议

### 1. 使用JMeter进行压力测试

创建线程组，模拟多个用户并发获取题目和提交答案：

- 线程数：100
- Ramp-Up时间：10秒
- 循环次数：10次

### 2. 使用Apache Bench测试

```bash
# 测试获取题目接口
ab -n 1000 -c 100 -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/questions/list?count=5

# 测试提交答案接口
ab -n 1000 -c 100 -p answer.json -T application/json \
  -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/questions/submit
```

其中 `answer.json` 内容：
```json
{"questionId": 1, "answer": "B", "duration": 30}
```

## 错误处理

### 1. 题目不存在

**响应**:
```json
{
  "code": 500,
  "message": "题目不存在",
  "data": null
}
```

### 2. 参数校验失败

**响应**:
```json
{
  "code": 500,
  "message": "题目ID不能为空",
  "data": null
}
```

### 3. 未登录或Token无效

**响应**:
```json
{
  "code": 401,
  "message": "用户不存在，请先登录",
  "data": null
}
```

## 性能优化建议

### 1. 数据库优化
- 实际项目应使用数据库存储题目和答题记录
- 为题目ID、用户ID添加索引
- 使用数据库连接池（如HikariCP）

### 2. 缓存优化
- 使用Redis缓存热门题目
- 缓存用户的答题统计信息
- 设置合理的缓存过期时间

### 3. 分布式部署
- 使用负载均衡器（如Nginx）
- 多实例部署，水平扩展
- 使用分布式锁（Redis）处理关键业务

### 4. 异步处理
- 答题记录可以异步写入数据库
- 使用消息队列（如RabbitMQ）处理统计任务

## 扩展功能建议

1. **题目分类管理**：按科目、难度、类型分类
2. **题目搜索**：支持关键词搜索题目
3. **考试模式**：创建考试试卷，限时答题
4. **错题本**：自动收集用户答错的题目
5. **排行榜**：展示用户得分排名
6. **题目收藏**：用户可以收藏题目
7. **题目评论**：用户可以讨论题目
8. **智能推荐**：根据用户水平推荐合适题目
9. **答题统计**：展示答题时长、正确率等数据
10. **题目导入导出**：支持批量导入题目

## 注意事项

1. **认证要求**：所有题目接口都需要JWT认证，必须先登录获取Token
2. **答案格式**：选择题答案统一使用大写字母（A/B/C/D），系统会自动转换
3. **并发安全**：使用ConcurrentHashMap保证线程安全，支持高并发访问
4. **数据持久化**：当前使用内存存储，重启后数据会丢失，生产环境应使用数据库
5. **题目随机性**：获取题目列表时会随机打乱顺序，每