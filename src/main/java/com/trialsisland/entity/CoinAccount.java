package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 金币账户实体类
 */
@Data
public class CoinAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 账户ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 金币余额
     */
    private Integer balance;

    /**
     * 累计充值金币
     */
    private Integer totalRecharge;

    /**
     * 累计消费金币
     */
    private Integer totalSpend;

    /**
     * 累计获得奖励金币
     */
    private Integer totalReward;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}