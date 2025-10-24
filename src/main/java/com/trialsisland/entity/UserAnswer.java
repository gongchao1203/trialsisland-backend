package com.trialsisland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户答题记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 答题时长（秒）
     */
    private Integer duration;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}