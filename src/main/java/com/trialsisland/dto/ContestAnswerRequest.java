package com.trialsisland.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 比赛答题请求DTO
 */
@Data
public class ContestAnswerRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 比赛ID
     */
    @NotNull(message = "比赛ID不能为空")
    private Long contestId;

    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /**
     * 用户答案
     */
    @NotBlank(message = "答案不能为空")
    private String answer;

    /**
     * 答题耗时（毫秒）
     */
    private Long duration;
}