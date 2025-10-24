package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目实体
 */
@Data
public class Problem implements Serializable {

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 难度等级（1-简单，2-中等，3-困难）
     */
    private Integer difficulty;

    /**
     * 完成奖励金币
     */
    private Long rewardCoins;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}