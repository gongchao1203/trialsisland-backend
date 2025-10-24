package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * PK赛参赛记录实体
 */
@Data
public class PKParticipant implements Serializable {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * PK赛ID
     */
    private Long pkMatchId;

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
     * 参赛时间
     */
    private LocalDateTime joinTime;
}