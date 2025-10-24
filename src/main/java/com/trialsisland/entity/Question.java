package com.trialsisland.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目ID
     */
    private Long id;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目图片URL（可选）
     */
    private String imageUrl;

    /**
     * 题目类型（choice: 选择题，填空等其他类型可扩展）
     */
    private String type;

    /**
     * 选项列表（选择题）
     */
    private List<String> options;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 题目难度（1-5）
     */
    private Integer difficulty;

    /**
     * 题目分值
     */
    private Integer score;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}