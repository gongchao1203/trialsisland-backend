package com.trialsisland.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.io.Serializable;

/**
 * 购买VIP请求DTO
 */
@Data
public class PurchaseVipRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会员等级：1-月卡，2-季卡，3-年卡
     */
    @NotNull(message = "会员等级不能为空")
    @Min(value = 1, message = "会员等级必须在1-3之间")
    @Max(value = 3, message = "会员等级必须在1-3之间")
    private Integer level;
}