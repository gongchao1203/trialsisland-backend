package com.trialsisland.controller;

import com.trialsisland.common.Result;
import com.trialsisland.dto.RechargeRequest;
import com.trialsisland.dto.WithdrawRequest;
import com.trialsisland.entity.CoinAccount;
import com.trialsisland.entity.CoinTransaction;
import com.trialsisland.service.CoinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 金币控制器
 */
@RestController
@RequestMapping("/api/coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    /**
     * 充值金币
     */
    @PostMapping("/recharge")
    public Result<CoinAccount> recharge(@Valid @RequestBody RechargeRequest request,
                                         HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String description = request.getDescription() != null ? request.getDescription() : "用户充值";
        CoinAccount account = coinService.recharge(userId, request.getAmount(), description);
        return Result.success(account);
    }

    /**
     * 提现
     */
    @PostMapping("/withdraw")
    public Result<Map<String, Object>> withdraw(@Valid @RequestBody WithdrawRequest request,
                                                  HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        CoinAccount account = coinService.withdraw(userId, request.getCoinAmount());
        
        Map<String, Object> result = new HashMap<>();
        result.put("account", account);
        result.put("cashAmount", request.getCoinAmount() / 100.0);
        result.put("message", "提现成功，预计1-3个工作日到账");
        
        return Result.success(result);
    }

    /**
     * 获取金币账户信息
     */
    @GetMapping("/account")
    public Result<CoinAccount> getAccount(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        CoinAccount account = coinService.getAccount(userId);
        return Result.success(account);
    }

    /**
     * 获取交易记录
     */
    @GetMapping("/transactions")
    public Result<List<CoinTransaction>> getTransactions(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        List<CoinTransaction> transactions = coinService.getTransactions(userId, 10);
        return Result.success(transactions);
    }
}