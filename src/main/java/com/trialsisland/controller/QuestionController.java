package com.trialsisland.controller;

import com.trialsisland.common.Result;
import com.trialsisland.dto.QuestionResponse;
import com.trialsisland.dto.SubmitAnswerRequest;
import com.trialsisland.dto.SubmitAnswerResponse;
import com.trialsisland.entity.UserAnswer;
import com.trialsisland.service.QuestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目控制器
 */
@RestController
@RequestMapping("/api/questions")
@Slf4j
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 获取单个题目
     */
    @GetMapping("/{questionId}")
    public Result<QuestionResponse> getQuestion(@PathVariable Long questionId) {
        log.info("获取题目，题目ID: {}", questionId);
        QuestionResponse question = questionService.getQuestion(questionId);
        return Result.success(question);
    }

    /**
     * 随机获取一道题目
     */
    @GetMapping("/random")
    public Result<QuestionResponse> getRandomQuestion() {
        log.info("随机获取一道题目");
        QuestionResponse question = questionService.getRandomQuestion();
        return Result.success(question);
    }

    /**
     * 获取多道题目（用于做题）
     * 支持并发访问
     */
    @GetMapping("/list")
    public Result<List<QuestionResponse>> getQuestions(
            @RequestParam(required = false, defaultValue = "5") Integer count) {
        log.info("获取题目列表，数量: {}", count);
        List<QuestionResponse> questions = questionService.getQuestions(count);
        return Result.success(questions);
    }

    /**
     * 提交答案
     * 支持并发提交
     */
    @PostMapping("/submit")
    public Result<SubmitAnswerResponse> submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request,
            HttpServletRequest httpRequest) {
        // 从request attribute中获取用户ID（由JWT拦截器设置）
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        log.info("用户 {} 提交答案，题目ID: {}, 答案: {}", 
                userId, request.getQuestionId(), request.getAnswer());
        
        SubmitAnswerResponse response = questionService.submitAnswer(userId, request);
        return Result.success(response);
    }

    /**
     * 获取用户答题历史
     */
    @GetMapping("/history")
    public Result<List<UserAnswer>> getUserAnswerHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取用户答题历史，用户ID: {}", userId);
        
        List<UserAnswer> history = questionService.getUserAnswerHistory(userId);
        return Result.success(history);
    }

    /**
     * 获取用户总得分
     */
    @GetMapping("/score")
    public Result<Integer> getUserTotalScore(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取用户总得分，用户ID: {}", userId);
        
        Integer totalScore = questionService.getUserTotalScore(userId);
        return Result.success(totalScore);
    }
}