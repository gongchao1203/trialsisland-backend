package com.trialsisland.enums;

/**
 * 金币交易类型枚举
 */
public enum CoinTransactionType {
    /**
     * 注册奖励
     */
    REGISTER_REWARD("注册奖励"),
    
    /**
     * 刷题奖励
     */
    PROBLEM_SOLVE_REWARD("刷题奖励"),
    
    /**
     * 提交时间奖励
     */
    SUBMIT_TIME_REWARD("提交时间奖励"),
    
    /**
     * PK赛报名费
     */
    PK_ENTRY_FEE("PK赛报名费"),
    
    /**
     * PK赛奖励
     */
    PK_REWARD("PK赛奖励"),
    
    /**
     * 试卷排名奖励
     */
    EXAM_RANK_REWARD("试卷排名奖励"),
    
    /**
     * 管理员调整
     */
    ADMIN_ADJUST("管理员调整");

    private final String description;

    CoinTransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}