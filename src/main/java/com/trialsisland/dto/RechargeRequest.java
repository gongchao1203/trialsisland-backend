package com.trialsisland.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * 充值请求DTO
 */
@Data
public class RechargeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 充值金币数量
     */
    @NotNull(message = "充值金额不能为空")
    @Min(value = 100, message = "最少充值100金币")
    private Integer amount;

    /**
     * 充值描述（可选）
     */
    private String description;
}