package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 试卷实体
 */
@Data
public class Exam implements Serializable {

    /**
     * 试卷ID
     */
    private Long id;

    /**
     * 试卷标题
     */
    private String title;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 奖池金币总额
     */
    private Long prizePool;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态（0-未开始，1-进行中，2-已结束）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}