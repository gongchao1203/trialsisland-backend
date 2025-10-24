package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 金币交易记录实体类
 */
@Data
public class CoinTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 交易ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易类型：1-充值，2-报名比赛，3-比赛奖励，4-提现
     */
    private Integer type;

    /**
     * 交易金额（正数为增加，负数为减少）
     */
    private Integer amount;

    /**
     * 交易前余额
     */
    private Integer beforeBalance;

    /**
     * 交易后余额
     */
    private Integer afterBalance;

    /**
     * 关联业务ID（如比赛ID）
     */
    private Long relatedId;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}