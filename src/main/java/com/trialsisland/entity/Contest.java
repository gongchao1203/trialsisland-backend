package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 比赛实体类
 */
@Data
public class Contest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 比赛ID
     */
    private Long id;

    /**
     * 比赛名称
     */
    private String name;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 报名费用（金币）
     */
    private Integer entryFee;

    /**
     * 奖池金币总数
     */
    private Integer prizePool;

    /**
     * 第一名奖励金币
     */
    private Integer firstPrize;

    /**
     * 第二名奖励金币
     */
    private Integer secondPrize;

    /**
     * 第三名奖励金币
     */
    private Integer thirdPrize;

    /**
     * 题目ID列表（JSON格式）
     */
    private List<Long> questionIds;

    /**
     * 比赛时长（分钟）
     */
    private Integer duration;

    /**
     * 比赛状态：0-未开始，1-报名中，2-进行中，3-已结束
     */
    private Integer status;

    /**
     * 最大参赛人数
     */
    private Integer maxParticipants;

    /**
     * 当前参赛人数
     */
    private Integer currentParticipants;

    /**
     * 报名开始时间
     */
    private LocalDateTime registrationStartTime;

    /**
     * 报名结束时间
     */
    private LocalDateTime registrationEndTime;

    /**
     * 比赛开始时间
     */
    private LocalDateTime startTime;

    /**
     * 比赛结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}