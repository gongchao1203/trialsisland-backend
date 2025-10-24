package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 题目提交记录实体
 */
@Data
public class ProblemSubmission implements Serializable {

    /**
     * 提交ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long problemId;

    /**
     * 提交代码
     */
    private String code;

    /**
     * 是否通过
     */
    private Boolean passed;

    /**
     * 获得的金币
     */
    private Long coinsEarned;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}