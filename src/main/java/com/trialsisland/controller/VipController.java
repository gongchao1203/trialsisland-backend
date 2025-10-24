package com.trialsisland.controller;

import com.trialsisland.common.Result;
import com.trialsisland.dto.PurchaseVipRequest;
import com.trialsisland.entity.VipMembership;
import com.trialsisland.service.VipService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * VIP会员控制器
 */
@RestController
@RequestMapping("/api/vip")
public class VipController {

    @Autowired
    private VipService vipService;

    /**
     * 购买/续费VIP
     */
    @PostMapping("/purchase")
    public Result<VipMembership> purchaseVip(@Valid @RequestBody PurchaseVipRequest request,
                                               HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer giftCoins = vipService.purchaseVip(userId, request.getLevel());
        VipMembership vip = vipService.getUserVipMembership(userId);
        return Result.success(vip);
    }

    /**
     * 获取VIP信息
     */
    @GetMapping("/info")
    public Result<VipMembership> getVipInfo(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        VipMembership vip = vipService.getUserVipMembership(userId);
        if (vip == null) {
            return Result.error(404, "未开通VIP会员");
        }
        return Result.success(vip);
    }

    /**
     * 检查VIP状态
     */
    @GetMapping("/check")
    public Result<Boolean> checkVipStatus(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        boolean isValid = vipService.isValidVip(userId);
        return Result.success(isValid);
    }
}