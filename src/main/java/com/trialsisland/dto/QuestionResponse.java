package com.trialsisland.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 题目响应DTO（不包含答案）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目内容
     */
    private String question;

    /**
     * 题目图片URL（可选）
     */
    private String imageUrl;

    /**
     * 选项列表
     */
    private List<String> options;

    /**
     * 题目类型
     */
    private String type;

    /**
     * 题目分值
     */
    private Integer score;
}