package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * PK赛实体
 */
@Data
public class PKMatch implements Serializable {

    /**
     * PK赛ID
     */
    private Long id;

    /**
     * PK赛名称
     */
    private String name;

    /**
     * 报名费（金币）
     */
    private Long entryFee;

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