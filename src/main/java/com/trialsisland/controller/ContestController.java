package com.trialsisland.controller;

import com.trialsisland.common.Result;
import com.trialsisland.dto.ContestAnswerRequest;
import com.trialsisland.dto.JoinContestRequest;
import com.trialsisland.dto.QuestionResponse;
import com.trialsisland.entity.Contest;
import com.trialsisland.entity.ContestParticipation;
import com.trialsisland.service.ContestService;
import com.trialsisland.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 比赛控制器
 */
@RestController
@RequestMapping("/api/contest")
public class ContestController {

    @Autowired
    private ContestService contestService;

    @Autowired
    private UserService userService;

    /**
     * 获取所有比赛列表
     */
    @GetMapping("/list")
    public Result<List<Contest>> getContestList() {
        List<Contest> contests = contestService.getAllContests();
        return Result.success(contests);
    }

    /**
     * 获取比赛详情
     */
    @GetMapping("/{contestId}")
    public Result<Contest> getContest(@PathVariable Long contestId) {
        Contest contest = contestService.getContest(contestId);
        if (contest == null) {
            return Result.error(404, "比赛不存在");
        }
        return Result.success(contest);
    }

    /**
     * 报名参加比赛
     */
    @PostMapping("/join")
    public Result<ContestParticipation> joinContest(@Valid @RequestBody JoinContestRequest request,
                                                      HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String username = userService.getUserById(userId).getUsername();
        ContestParticipation participation = contestService.joinContest(request.getContestId(), userId, username);
        return Result.success(participation);
    }

    /**
     * 开始比赛
     */
    @PostMapping("/{contestId}/start")
    public Result<Map<String, Object>> startContest(@PathVariable Long contestId,
                                                      HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        contestService.startContest(contestId, userId);
        
        // 获取比赛题目
        List<QuestionResponse> questions = contestService.getContestQuestions(contestId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("questions", questions);
        result.put("message", "比赛已开始，祝你好运！");
        
        return Result.success(result);
    }

    /**
     * 提交答案
     */
    @PostMapping("/answer")
    public Result<Map<String, Object>> submitAnswer(@Valid @RequestBody ContestAnswerRequest request,
                                                      HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        boolean isCorrect = contestService.submitAnswer(
            request.getContestId(), 
            userId, 
            request.getQuestionId(), 
            request.getAnswer(), 
            request.getDuration()
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("isCorrect", isCorrect);
        result.put("message", isCorrect ? "回答正确！" : "回答错误！");
        
        return Result.success(result);
    }

    /**
     * 完成比赛
     */
    @PostMapping("/{contestId}/finish")
    public Result<ContestParticipation> finishContest(@PathVariable Long contestId,
                                                        HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ContestParticipation participation = contestService.finishContest(contestId, userId);
        return Result.success(participation);
    }

    /**
     * 获取比赛排行榜
     */
    @GetMapping("/{contestId}/ranking")
    public Result<List<ContestParticipation>> getRanking(@PathVariable Long contestId) {
        List<ContestParticipation> ranking = contestService.getContestRanking(contestId);
        return Result.success(ranking);
    }

    /**
     * 获取用户参赛记录
     */
    @GetMapping("/my-participations")
    public Result<List<ContestParticipation>> getMyParticipations(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<ContestParticipation> participations = contestService.getUserAllParticipations(userId);
        return Result.success(participations);
    }

    /**
     * 获取用户在某场比赛的参赛记录
     */
    @GetMapping("/{contestId}/my-record")
    public Result<ContestParticipation> getMyRecord(@PathVariable Long contestId,
                                                      HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ContestParticipation participation = contestService.getUserParticipation(contestId, userId);
        if (participation == null) {
            return Result.error(404, "未找到参赛记录");
        }
        return Result.success(participation);
    }
}