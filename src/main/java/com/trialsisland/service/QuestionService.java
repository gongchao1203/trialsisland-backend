package com.trialsisland.service;

import com.trialsisland.dto.QuestionResponse;
import com.trialsisland.dto.SubmitAnswerRequest;
import com.trialsisland.dto.SubmitAnswerResponse;
import com.trialsisland.entity.Question;
import com.trialsisland.entity.UserAnswer;
import com.trialsisland.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 题目服务类
 */
@Service
@Slf4j
public class QuestionService {

    // 使用ConcurrentHashMap保证并发安全
    private final Map<Long, Question> questionBank = new ConcurrentHashMap<>();
    
    // 用户答题记录，使用ConcurrentHashMap保证并发安全
    private final Map<Long, UserAnswer> userAnswers = new ConcurrentHashMap<>();
    
    // 答题记录ID生成器
    private final AtomicLong answerIdGenerator = new AtomicLong(1);

    public QuestionService() {
        // 初始化题库（实际项目应该从数据库加载）
        initQuestionBank();
    }

    /**
     * 初始化题库
     */
    private void initQuestionBank() {
        // 题目1
        Question q1 = new Question();
        q1.setId(1L);
        q1.setContent("下列各项中，不属于我国古代四大发明的是（  ）");
        q1.setType("choice");
        q1.setOptions(Arrays.asList("A.造纸术", "B.针灸术", "C.火药", "D.活字印刷术"));
        q1.setAnswer("B");
        q1.setDifficulty(2);
        q1.setScore(10);
        q1.setCreateTime(LocalDateTime.now());
        questionBank.put(q1.getId(), q1);

        // 题目2
        Question q2 = new Question();
        q2.setId(2L);
        q2.setContent("甲、乙两人在周长为400米的环形跑道上同时同地同向而行，甲的速度是每秒6米，乙的速度是每秒4米，那么甲第一次追上乙需要多少秒？（  ）");
        q2.setType("choice");
        q2.setOptions(Arrays.asList("A.100", "B.150", "C.200", "D.250"));
        q2.setAnswer("C");
        q2.setDifficulty(3);
        q2.setScore(15);
        q2.setCreateTime(LocalDateTime.now());
        questionBank.put(q2.getId(), q2);

        // 题目3
        Question q3 = new Question();
        q3.setId(3L);
        q3.setContent("在如今浅阅读盛行的时代，一边是快餐式、碎片式阅读的轻松，一边是慢读、细读的沉重，经典自然免不了有点______的味道。就阅读是一种学习、一种对自我的提升而言，浅阅读并不是真正的阅读，长久沉浸在浅阅读的习惯之中，也只会让人离那些最好的书籍愈来愈远。然而，只是______对经典的珍重，却不如对于经典真实的理解更为重要。依次填入划横线部分最恰当的一项是（  ）");
        q3.setType("choice");
        q3.setOptions(Arrays.asList("A.阳春白雪 提倡", "B.曲高和寡 呼唤", "C.孤芳自赏 强调", "D.凄风苦雨 苛求"));
        q3.setAnswer("B");
        q3.setDifficulty(4);
        q3.setScore(20);
        q3.setCreateTime(LocalDateTime.now());
        questionBank.put(q3.getId(), q3);

        // 题目4
        Question q4 = new Question();
        q4.setId(4L);
        q4.setContent("某工厂有甲、乙、丙三个车间，甲车间的人数是乙车间的\\(\\frac{2}{3}\\)，乙车间的人数是丙车间的\\(\\frac{3}{4}\\)，已知丙车间有80人，那么甲车间有多少人？（  ）");
        q4.setType("choice");
        q4.setOptions(Arrays.asList("A.40", "B.50", "C.60", "D.70"));
        q4.setAnswer("A");
        q4.setDifficulty(3);
        q4.setScore(15);
        q4.setCreateTime(LocalDateTime.now());
        questionBank.put(q4.getId(), q4);

        // 题目5
        Question q5 = new Question();
        q5.setId(5L);
        q5.setContent("所有的聪明人都是近视眼，我近视得很厉害，所以我很聪明。以下哪项与上述推理的逻辑结构一致？（  ）");
        q5.setType("choice");
        q5.setOptions(Arrays.asList(
            "A.我是个笨人，因为所有的聪明人都是近视眼，而我的视力那么好",
            "B.所有的猪都有四条腿，但这种动物有八条腿，所以它不是猪",
            "C.小陈十分高兴，所以小陈一定长得很胖；因为高兴的人都能长胖",
            "D.所有的鸡都是尖嘴，这种总在树上呆着的鸟是尖嘴，所以它是鸡"
        ));
        q5.setAnswer("D");
        q5.setDifficulty(5);
        q5.setScore(25);
        q5.setCreateTime(LocalDateTime.now());
        questionBank.put(q5.getId(), q5);

        log.info("题库初始化完成，共加载 {} 道题目", questionBank.size());
    }

    /**
     * 获取单个题目（用于做题）
     * 不返回答案
     */
    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionBank.get(questionId);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }
        
        return convertToResponse(question);
    }

    /**
     * 随机获取一道题目
     */
    public QuestionResponse getRandomQuestion() {
        if (questionBank.isEmpty()) {
            throw new BusinessException("题库为空");
        }
        
        List<Long> questionIds = new ArrayList<>(questionBank.keySet());
        Collections.shuffle(questionIds);
        Long randomId = questionIds.get(0);
        
        return getQuestion(randomId);
    }

    /**
     * 获取多道题目
     */
    public List<QuestionResponse> getQuestions(Integer count) {
        if (count == null || count <= 0) {
            count = 5;
        }
        
        if (questionBank.isEmpty()) {
            throw new BusinessException("题库为空");
        }
        
        List<Long> questionIds = new ArrayList<>(questionBank.keySet());
        Collections.shuffle(questionIds);
        
        // 取前count个题目，如果题库数量不足则全部返回
        int actualCount = Math.min(count, questionIds.size());
        
        return questionIds.stream()
                .limit(actualCount)
                .map(this::getQuestion)
                .collect(Collectors.toList());
    }

    /**
     * 提交答案
     */
    public SubmitAnswerResponse submitAnswer(Long userId, SubmitAnswerRequest request) {
        // 验证题目是否存在
        Question question = questionBank.get(request.getQuestionId());
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        // 标准化答案格式（统一转换为大写，去除空格）
        String userAnswer = request.getAnswer().trim().toUpperCase();
        String correctAnswer = question.getAnswer().trim().toUpperCase();
        
        // 判断答案是否正确
        boolean isCorrect = userAnswer.equals(correctAnswer);
        int score = isCorrect ? question.getScore() : 0;

        // 保存答题记录（使用ConcurrentHashMap保证并发安全）
        UserAnswer userAnswerRecord = new UserAnswer();
        userAnswerRecord.setId(answerIdGenerator.getAndIncrement());
        userAnswerRecord.setUserId(userId);
        userAnswerRecord.setQuestionId(request.getQuestionId());
        userAnswerRecord.setUserAnswer(userAnswer);
        userAnswerRecord.setIsCorrect(isCorrect);
        userAnswerRecord.setScore(score);
        userAnswerRecord.setDuration(request.getDuration());
        userAnswerRecord.setSubmitTime(LocalDateTime.now());
        
        userAnswers.put(userAnswerRecord.getId(), userAnswerRecord);
        
        log.info("用户 {} 提交答案，题目ID: {}, 答案: {}, 正确: {}, 得分: {}",
                userId, request.getQuestionId(), userAnswer, isCorrect, score);

        // 返回结果
        return new SubmitAnswerResponse(
                isCorrect,
                score,
                question.getAnswer(),
                null // 解析说明可以后续添加
        );
    }

    /**
     * 获取用户的答题历史
     */
    public List<UserAnswer> getUserAnswerHistory(Long userId) {
        return userAnswers.values().stream()
                .filter(answer -> answer.getUserId().equals(userId))
                .sorted(Comparator.comparing(UserAnswer::getSubmitTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的总得分
     */
    public Integer getUserTotalScore(Long userId) {
        return userAnswers.values().stream()
                .filter(answer -> answer.getUserId().equals(userId))
                .mapToInt(UserAnswer::getScore)
                .sum();
    }

    /**
     * 将Question转换为QuestionResponse（不包含答案）
     */
    private QuestionResponse convertToResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setQuestion(question.getContent());
        response.setImageUrl(question.getImageUrl());
        response.setOptions(question.getOptions());
        response.setType(question.getType());
        response.setScore(question.getScore());
        return response;
    }
}