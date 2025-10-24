package com.trialsisland.service;

import com.trialsisland.entity.VipMembership;
import com.trialsisland.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * VIP会员服务类
 */
@Service
@Slf4j
public class VipService {

    // 使用ConcurrentHashMap保证并发安全
    private final Map<Long, VipMembership> vipMemberships = new ConcurrentHashMap<>();
    private final AtomicLong vipIdGenerator = new AtomicLong(1);

    /**
     * 购买VIP会员
     * @param userId 用户ID
     * @param level 会员等级：1-月卡(30天,送1000金币)，2-季卡(90天,送3500金币)，3-年卡(365天,送15000金币)
     * @return 赠送的金币数量
     */
    public Integer purchaseVip(Long userId, Integer level) {
        if (level < 1 || level > 3) {
            throw new BusinessException("无效的会员等级");
        }

        VipMembership existing = getUserVipMembership(userId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now;
        LocalDateTime expireTime;
        Integer giftCoins;

        // 根据等级计算到期时间和赠送金币
        switch (level) {
            case 1: // 月卡
                expireTime = now.plusDays(30);
                giftCoins = 1000;
                break;
            case 2: // 季卡
                expireTime = now.plusDays(90);
                giftCoins = 3500;
                break;
            case 3: // 年卡
                expireTime = now.plusDays(365);
                giftCoins = 15000;
                break;
            default:
                throw new BusinessException("无效的会员等级");
        }

        if (existing != null && existing.getStatus() == 1) {
            // 如果已有有效会员，续期
            if (existing.getExpireTime().isAfter(now)) {
                startTime = existing.getExpireTime();
                expireTime = startTime.plusDays(level == 1 ? 30 : (level == 2 ? 90 : 365));
            }
            existing.setLevel(level);
            existing.setStartTime(startTime);
            existing.setExpireTime(expireTime);
            existing.setUpdateTime(now);
            log.info("用户 {} 续费VIP，等级: {}，到期时间: {}", userId, level, expireTime);
        } else {
            // 创建新会员
            VipMembership vip = new VipMembership();
            vip.setId(vipIdGenerator.getAndIncrement());
            vip.setUserId(userId);
            vip.setLevel(level);
            vip.setStatus(1);
            vip.setStartTime(startTime);
            vip.setExpireTime(expireTime);
            vip.setCreateTime(now);
            vip.setUpdateTime(now);
            vipMemberships.put(userId, vip);
            log.info("用户 {} 购买VIP，等级: {}，到期时间: {}", userId, level, expireTime);
        }

        return giftCoins;
    }

    /**
     * 检查用户是否是有效VIP
     */
    public boolean isValidVip(Long userId) {
        VipMembership vip = vipMemberships.get(userId);
        if (vip == null || vip.getStatus() != 1) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (vip.getExpireTime().isBefore(now)) {
            // VIP已过期
            vip.setStatus(0);
            vip.setUpdateTime(now);
            return false;
        }

        return true;
    }

    /**
     * 获取用户VIP信息
     */
    public VipMembership getUserVipMembership(Long userId) {
        VipMembership vip = vipMemberships.get(userId);
        if (vip != null) {
            // 检查是否过期
            if (vip.getStatus() == 1 && vip.getExpireTime().isBefore(LocalDateTime.now())) {
                vip.setStatus(0);
                vip.setUpdateTime(LocalDateTime.now());
            }
        }
        return vip;
    }

    /**
     * 获取VIP价格信息
     */
    public Map<String, Object> getVipPrices() {
        return Map.of(
            "monthCard", Map.of(
                "level", 1,
                "name", "月卡",
                "days", 30,
                "price", 30.0,
                "giftCoins", 1000,
                "description", "购买月卡会员，赠送1000金币"
            ),
            "seasonCard", Map.of(
                "level", 2,
                "name", "季卡",
                "days", 90,
                "price", 80.0,
                "giftCoins", 3500,
                "description", "购买季卡会员，赠送3500金币"
            ),
            "yearCard", Map.of(
                "level", 3,
                "name", "年卡",
                "days", 365,
                "price", 298.0,
                "giftCoins", 15000,
                "description", "购买年卡会员，赠送15000金币"
            )
        );
    }
}