package com.trialsisland.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 提交答案请求DTO
 */
@Data
public class SubmitAnswerRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /**
     * 用户答案（选择题为A/B/C/D，其他类型为文本内容）
     */
    @NotBlank(message = "答案不能为空")
    private String answer;

    /**
     * 答题时长（秒，可选）
     */
    private Integer duration;
}