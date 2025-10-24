package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 比赛参赛记录实体类
 */
@Data
public class ContestParticipation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 比赛ID
     */
    private Long contestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 答对题数
     */
    private Integer correctCount;

    /**
     * 总题数
     */
    private Integer totalQuestions;

    /**
     * 总用时（秒）
     */
    private Integer totalDuration;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 获得奖励金币
     */
    private Integer rewardCoins;

    /**
     * 参赛状态：0-已报名，1-进行中，2-已完成
     */
    private Integer status;

    /**
     * 报名时间
     */
    private LocalDateTime registrationTime;

    /**
     * 开始答题时间
     */
    private LocalDateTime startTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}