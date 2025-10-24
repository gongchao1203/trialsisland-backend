package com.trialsisland.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 报名比赛请求DTO
 */
@Data
public class JoinContestRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 比赛ID
     */
    @NotNull(message = "比赛ID不能为空")
    private Long contestId;
}