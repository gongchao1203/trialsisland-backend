package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 试卷提交记录实体
 */
@Data
public class ExamSubmission implements Serializable {

    /**
     * 提交ID
     */
    private Long id;

    /**
     * 试卷ID
     */
    private Long examId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 排名
     */
    private Integer ranking;

    /**
     * 获得的奖励金币
     */
    private Long rewardCoins;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}