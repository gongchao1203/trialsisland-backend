package com.trialsisland.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * 提现请求DTO
 */
@Data
public class WithdrawRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 提现金币数量（必须是100的整数倍）
     */
    @NotNull(message = "提现金币数量不能为空")
    @Min(value = 100, message = "最少提现100金币")
    private Integer coinAmount;
}