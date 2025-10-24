package com.trialsisland.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 提交答案响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否正确
     */
    private Boolean isCorrect;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 解析说明（可选）
     */
    private String explanation;
}