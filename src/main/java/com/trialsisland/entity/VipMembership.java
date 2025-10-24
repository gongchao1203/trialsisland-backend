package com.trialsisland.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * VIP会员实体类
 */
@Data
public class VipMembership implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 会员等级：1-月卡，2-季卡，3-年卡
     */
    private Integer level;

    /**
     * 会员状态：0-已过期，1-有效
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}