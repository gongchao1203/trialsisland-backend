package com.trialsisland.service;

import com.trialsisland.dto.QuestionResponse;
import com.trialsisland.entity.Contest;
import com.trialsisland.entity.ContestParticipation;
import com.trialsisland.entity.Question;
import com.trialsisland.entity.UserAnswer;
import com.trialsisland.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 比赛服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContestService {

    private final VipService vipService;
    private final CoinService coinService;
    private final QuestionService questionService;

    // 使用ConcurrentHashMap保证并发安全
    private final Map<Long, Contest> contests = new ConcurrentHashMap<>();
    private final Map<Long, List<ContestParticipation>> participations = new ConcurrentHashMap<>();
    private final Map<String, ContestParticipation> userContestMap = new ConcurrentHashMap<>();
    private final AtomicLong contestIdGenerator = new AtomicLong(1);
    private final AtomicLong participationIdGenerator = new AtomicLong(1);

    // 报名费用
    private static final int ENTRY_FEE = 100;
    // 目标奖池金币
    private static final int TARGET_PRIZE_POOL = 5000;
    // 平台抽成比例（20%）
    private static final double PLATFORM_COMMISSION = 0.2;

    /**
     * 初始化默认比赛
     */
    @PostConstruct
    private void initDefaultContests() {
        createContest("编程基础挑战赛", "测试你的编程基础知识", Arrays.asList(1L, 2L, 3L, 4L, 5L), 30);
        log.info("初始化默认比赛完成");
    }

    /**
     * 创建比赛
     */
    public Contest createContest(String name, String description, List<Long> questionIds, Integer duration) {
        Contest contest = new Contest();
        contest.setId(contestIdGenerator.getAndIncrement());
        contest.setName(name);
        contest.setDescription(description);
        contest.setEntryFee(ENTRY_FEE);
        contest.setPrizePool(0);
        contest.setFirstPrize(0);
        contest.setSecondPrize(0);
        contest.setThirdPrize(0);
        contest.setQuestionIds(questionIds);
        contest.setDuration(duration);
        contest.setStatus(1); // 报名中
        contest.setMaxParticipants(1000);
        contest.setCurrentParticipants(0);
        
        LocalDateTime now = LocalDateTime.now();
        contest.setRegistrationStartTime(now);
        contest.setRegistrationEndTime(now.plusDays(7));
        contest.setStartTime(now);
        contest.setEndTime(now.plusDays(7));
        contest.setCreateTime(now);
        contest.setUpdateTime(now);

        contests.put(contest.getId(), contest);
        participations.put(contest.getId(), new ArrayList<>());
        log.info("创建比赛: {}, ID: {}", name, contest.getId());
        return contest;
    }

    /**
     * 报名参加比赛
     */
    public synchronized ContestParticipation joinContest(Long contestId, Long userId, String username) {
        // 验证VIP身份
        if (!vipService.isValidVip(userId)) {
            throw new BusinessException("只有VIP会员才能参加比赛，请先购买VIP会员");
        }

        Contest contest = contests.get(contestId);
        if (contest == null) {
            throw new BusinessException("比赛不存在");
        }

        if (contest.getStatus() != 1) {
            throw new BusinessException("比赛未在报名期间");
        }

        // 检查是否已报名
        String key = userId + "_" + contestId;
        if (userContestMap.containsKey(key)) {
            throw new BusinessException("您已经报名过此比赛");
        }

        // 检查人数限制
        if (contest.getCurrentParticipants() >= contest.getMaxParticipants()) {
            throw new BusinessException("比赛人数已满");
        }

        // 扣除报名费
        coinService.spend(userId, ENTRY_FEE, contestId, "报名比赛: " + contest.getName());

        // 更新奖池
        int platformFee = (int) (ENTRY_FEE * PLATFORM_COMMISSION);
        int prizeIncrease = ENTRY_FEE - platformFee;
        contest.setPrizePool(contest.getPrizePool() + prizeIncrease);
        contest.setCurrentParticipants(contest.getCurrentParticipants() + 1);
        contest.setUpdateTime(LocalDateTime.now());

        // 创建参赛记录
        ContestParticipation participation = new ContestParticipation();
        participation.setId(participationIdGenerator.getAndIncrement());
        participation.setContestId(contestId);
        participation.setUserId(userId);
        participation.setUsername(username);
        participation.setTotalScore(0);
        participation.setCorrectCount(0);
        participation.setTotalQuestions(contest.getQuestionIds().size());
        participation.setTotalDuration(0);
        participation.setRank(0);
        participation.setRewardCoins(0);
        participation.setStatus(0); // 已报名
        participation.setRegistrationTime(LocalDateTime.now());
        participation.setCreateTime(LocalDateTime.now());
        participation.setUpdateTime(LocalDateTime.now());

        participations.get(contestId).add(participation);
        userContestMap.put(key, participation);

        // 更新奖励金额
        updatePrizes(contest);

        log.info("用户 {} 报名比赛 {}, 当前奖池: {} 金币", userId, contest.getName(), contest.getPrizePool());
        return participation;
    }

    /**
     * 更新奖励金额（动态调整）
     */
    private void updatePrizes(Contest contest) {
        int prizePool = contest.getPrizePool();
        
        // 如果奖池达到目标，按比例分配
        if (prizePool >= TARGET_PRIZE_POOL) {
            contest.setFirstPrize((int) (prizePool * 0.5));  // 50%给第一名
            contest.setSecondPrize((int) (prizePool * 0.3)); // 30%给第二名
            contest.setThirdPrize((int) (prizePool * 0.2));  // 20%给第三名
        } else {
            // 奖池不足目标时，降低奖励但确保有奖
            double ratio = prizePool / (double) TARGET_PRIZE_POOL;
            contest.setFirstPrize(Math.max((int) (TARGET_PRIZE_POOL * 0.5 * ratio), prizePool / 2));
            contest.setSecondPrize(Math.max((int) (TARGET_PRIZE_POOL * 0.3 * ratio), prizePool / 3));
            contest.setThirdPrize(Math.max((int) (TARGET_PRIZE_POOL * 0.2 * ratio), prizePool / 6));
        }
    }

    /**
     * 提交比赛答案
     */
    public synchronized boolean submitAnswer(Long contestId, Long userId, Long questionId, 
                                              String answer, Long duration) {
        String key = userId + "_" + contestId;
        ContestParticipation participation = userContestMap.get(key);
        
        if (participation == null) {
            throw new BusinessException("您未报名此比赛");
        }

        if (participation.getStatus() == 2) {
            throw new BusinessException("您已经完成了此比赛");
        }

        // 标记为进行中
        if (participation.getStatus() == 0) {
            participation.setStatus(1);
            participation.setStartTime(LocalDateTime.now());
        }

        // 验证答案
        Question question = questionService.getQuestionEntity(questionId);
        if (question == null) {
            throw new BusinessException("题目不存在");
        }

        boolean isCorrect = question.getAnswer().trim().equalsIgnoreCase(answer.trim());
        
        if (isCorrect) {
            participation.setCorrectCount(participation.getCorrectCount() + 1);
            participation.setTotalScore(participation.getTotalScore() + question.getScore());
        }

        participation.setTotalDuration(participation.getTotalDuration() + (duration != null ? duration.intValue() : 0));
        participation.setUpdateTime(LocalDateTime.now());

        log.info("用户 {} 在比赛 {} 中回答题目 {}: {}", userId, contestId, questionId, isCorrect ? "正确" : "错误");
        return isCorrect;
    }

    /**
     * 完成比赛
     */
    public synchronized ContestParticipation finishContest(Long contestId, Long userId) {
        String key = userId + "_" + contestId;
        ContestParticipation participation = userContestMap.get(key);
        
        if (participation == null) {
            throw new BusinessException("您未报名此比赛");
        }

        if (participation.getStatus() == 2) {
            throw new BusinessException("您已经完成了此比赛");
        }

        participation.setStatus(2); // 已完成
        participation.setFinishTime(LocalDateTime.now());
        participation.setUpdateTime(LocalDateTime.now());

        // 计算排名
        calculateRankings(contestId);

        log.info("用户 {} 完成比赛 {}, 得分: {}, 排名: {}", 
                userId, contestId, participation.getTotalScore(), participation.getRank());
        return participation;
    }

    /**
     * 计算排名并发放奖励
     */
    private void calculateRankings(Long contestId) {
        List<ContestParticipation> finishedList = participations.get(contestId).stream()
                .filter(p -> p.getStatus() == 2)
                .sorted((a, b) -> {
                    // 先按得分降序
                    int scoreCompare = b.getTotalScore().compareTo(a.getTotalScore());
                    if (scoreCompare != 0) return scoreCompare;
                    // 得分相同按用时升序
                    return a.getTotalDuration().compareTo(b.getTotalDuration());
                })
                .collect(Collectors.toList());

        Contest contest = contests.get(contestId);
        
        for (int i = 0; i < finishedList.size(); i++) {
            ContestParticipation p = finishedList.get(i);
            p.setRank(i + 1);

            // 发放奖励
            if (i == 0 && contest.getFirstPrize() > 0) {
                p.setRewardCoins(contest.getFirstPrize());
                coinService.reward(p.getUserId(), contest.getFirstPrize(), contestId, 
                        "比赛第1名奖励: " + contest.getName());
            } else if (i == 1 && contest.getSecondPrize() > 0) {
                p.setRewardCoins(contest.getSecondPrize());
                coinService.reward(p.getUserId(), contest.getSecondPrize(), contestId, 
                        "比赛第2名奖励: " + contest.getName());
            } else if (i == 2 && contest.getThirdPrize() > 0) {
                p.setRewardCoins(contest.getThirdPrize());
                coinService.reward(p.getUserId(), contest.getThirdPrize(), contestId, 
                        "比赛第3名奖励: " + contest.getName());
            }
        }
    }

    /**
     * 获取比赛列表
     */
    public List<Contest> getAllContests() {
        return new ArrayList<>(contests.values());
    }

    /**
     * 获取比赛详情
     */
    public Contest getContest(Long contestId) {
        return contests.get(contestId);
    }

    /**
     * 开始比赛
     */
    public void startContest(Long contestId, Long userId) {
        String key = userId + "_" + contestId;
        ContestParticipation participation = userContestMap.get(key);
        
        if (participation == null) {
            throw new BusinessException("您未报名此比赛");
        }

        if (participation.getStatus() == 2) {
            throw new BusinessException("您已经完成了此比赛");
        }

        // 标记为进行中
        participation.setStatus(1);
        participation.setStartTime(LocalDateTime.now());
        participation.setUpdateTime(LocalDateTime.now());
        
        log.info("用户 {} 开始比赛 {}", userId, contestId);
    }

    /**
     * 获取比赛题目列表
     */
    public List<QuestionResponse> getContestQuestions(Long contestId) {
        Contest contest = contests.get(contestId);
        if (contest == null) {
            throw new BusinessException("比赛不存在");
        }
        
        List<QuestionResponse> questions = new ArrayList<>();
        for (Long questionId : contest.getQuestionIds()) {
            QuestionResponse question = questionService.getQuestion(questionId);
            if (question != null) {
                questions.add(question);
            }
        }
        return questions;
    }

    /**
     * 获取比赛排行榜
     */
    public List<ContestParticipation> getContestRanking(Long contestId) {
        return participations.get(contestId).stream()
                .filter(p -> p.getStatus() == 2)
                .sorted((a, b) -> {
                    int scoreCompare = b.getTotalScore().compareTo(a.getTotalScore());
                    if (scoreCompare != 0) return scoreCompare;
                    return a.getTotalDuration().compareTo(b.getTotalDuration());
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取用户参赛记录
     */
    public ContestParticipation getUserParticipation(Long contestId, Long userId) {
        String key = userId + "_" + contestId;
        return userContestMap.get(key);
    }

    /**
     * 获取用户所有参赛记录
     */
    public List<ContestParticipation> getUserAllParticipations(Long userId) {
        return userContestMap.values().stream()
                .filter(p -> p.getUserId().equals(userId))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
}