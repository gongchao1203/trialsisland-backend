package com.trialsisland.entity;

import com.trialsisland.enums.CoinTransactionType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 金币交易记录实体
 */
@Data
public class CoinTransaction implements Serializable {

    /**
     * 交易ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易类型
     */
    private CoinTransactionType transactionType;

    /**
     * 变动金额（正数为收入，负数为支出）
     */
    private Long amount;

    /**
     * 交易前余额
     */
    private Long balanceBefore;

    /**
     * 交易后余额
     */
    private Long balanceAfter;

    /**
     * 关联业务ID（如题目ID、PK赛ID等）
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