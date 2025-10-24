package com.trialsisland.service;

import com.trialsisland.entity.CoinAccount;
import com.trialsisland.entity.CoinTransaction;
import com.trialsisland.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 金币服务类
 */
@Service
@Slf4j
public class CoinService {

    // 使用ConcurrentHashMap保证并发安全
    private final Map<Long, CoinAccount> coinAccounts = new ConcurrentHashMap<>();
    private final Map<Long, List<CoinTransaction>> transactions = new ConcurrentHashMap<>();
    private final AtomicLong accountIdGenerator = new AtomicLong(1);
    private final AtomicLong transactionIdGenerator = new AtomicLong(1);

    // 金币兑换比例：100金币 = 1元
    private static final int COINS_PER_YUAN = 100;

    /**
     * 初始化用户金币账户
     */
    public CoinAccount createAccount(Long userId) {
        CoinAccount account = coinAccounts.get(userId);
        if (account == null) {
            account = new CoinAccount();
            account.setId(accountIdGenerator.getAndIncrement());
            account.setUserId(userId);
            account.setBalance(0);
            account.setTotalRecharge(0);
            account.setTotalSpend(0);
            account.setTotalReward(0);
            account.setCreateTime(LocalDateTime.now());
            account.setUpdateTime(LocalDateTime.now());
            coinAccounts.put(userId, account);
            transactions.put(userId, new ArrayList<>());
            log.info("创建用户金币账户，用户ID: {}", userId);
        }
        return account;
    }

    /**
     * 充值金币
     */
    public synchronized CoinAccount recharge(Long userId, Integer amount, String description) {
        if (amount <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        CoinAccount account = getOrCreateAccount(userId);
        Integer beforeBalance = account.getBalance();
        Integer afterBalance = beforeBalance + amount;

        account.setBalance(afterBalance);
        account.setTotalRecharge(account.getTotalRecharge() + amount);
        account.setUpdateTime(LocalDateTime.now());

        // 记录交易
        addTransaction(userId, 1, amount, beforeBalance, afterBalance, null, description);
        log.info("用户 {} 充值 {} 金币，余额: {} -> {}", userId, amount, beforeBalance, afterBalance);
        return account;
    }

    /**
     * 消费金币
     */
    public synchronized void spend(Long userId, Integer amount, Long relatedId, String description) {
        if (amount <= 0) {
            throw new BusinessException("消费金额必须大于0");
        }

        CoinAccount account = getOrCreateAccount(userId);
        if (account.getBalance() < amount) {
            throw new BusinessException("金币余额不足，当前余额: " + account.getBalance());
        }

        Integer beforeBalance = account.getBalance();
        Integer afterBalance = beforeBalance - amount;

        account.setBalance(afterBalance);
        account.setTotalSpend(account.getTotalSpend() + amount);
        account.setUpdateTime(LocalDateTime.now());

        // 记录交易（消费用负数）
        addTransaction(userId, 2, -amount, beforeBalance, afterBalance, relatedId, description);
        log.info("用户 {} 消费 {} 金币，余额: {} -> {}", userId, amount, beforeBalance, afterBalance);
    }

    /**
     * 发放奖励金币
     */
    public synchronized void reward(Long userId, Integer amount, Long relatedId, String description) {
        if (amount <= 0) {
            throw new BusinessException("奖励金额必须大于0");
        }

        CoinAccount account = getOrCreateAccount(userId);
        Integer beforeBalance = account.getBalance();
        Integer afterBalance = beforeBalance + amount;

        account.setBalance(afterBalance);
        account.setTotalReward(account.getTotalReward() + amount);
        account.setUpdateTime(LocalDateTime.now());

        // 记录交易
        addTransaction(userId, 3, amount, beforeBalance, afterBalance, relatedId, description);
        log.info("用户 {} 获得奖励 {} 金币，余额: {} -> {}", userId, amount, beforeBalance, afterBalance);
    }

    /**
     * 提现（金币兑换现金）
     */
    public synchronized CoinAccount withdraw(Long userId, Integer coinAmount) {
        if (coinAmount <= 0 || coinAmount % COINS_PER_YUAN != 0) {
            throw new BusinessException("提现金币数必须是" + COINS_PER_YUAN + "的整数倍");
        }

        CoinAccount account = getOrCreateAccount(userId);
        if (account.getBalance() < coinAmount) {
            throw new BusinessException("金币余额不足，当前余额: " + account.getBalance());
        }

        Integer beforeBalance = account.getBalance();
        Integer afterBalance = beforeBalance - coinAmount;
        Double cashAmount = coinAmount / (double) COINS_PER_YUAN;

        account.setBalance(afterBalance);
        account.setUpdateTime(LocalDateTime.now());

        // 记录交易
        String description = String.format("提现%.2f元", cashAmount);
        addTransaction(userId, 4, -coinAmount, beforeBalance, afterBalance, null, description);
        log.info("用户 {} 提现 {} 金币({}元)，余额: {} -> {}", userId, coinAmount, cashAmount, beforeBalance, afterBalance);

        return account;
    }

    /**
     * 获取用户金币账户
     */
    public CoinAccount getAccount(Long userId) {
        return coinAccounts.get(userId);
    }

    /**
     * 获取或创建账户
     */
    private CoinAccount getOrCreateAccount(Long userId) {
        CoinAccount account = coinAccounts.get(userId);
        if (account == null) {
            account = createAccount(userId);
        }
        return account;
    }

    /**
     * 获取用户交易记录
     */
    public List<CoinTransaction> getTransactions(Long userId, Integer limit) {
        List<CoinTransaction> userTransactions = transactions.get(userId);
        if (userTransactions == null) {
            return new ArrayList<>();
        }

        return userTransactions.stream()
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .limit(limit != null ? limit : userTransactions.size())
                .collect(Collectors.toList());
    }

    /**
     * 添加交易记录
     */
    private void addTransaction(Long userId, Integer type, Integer amount,
                                Integer beforeBalance, Integer afterBalance,
                                Long relatedId, String description) {
        CoinTransaction transaction = new CoinTransaction();
        transaction.setId(transactionIdGenerator.getAndIncrement());
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setBeforeBalance(beforeBalance);
        transaction.setAfterBalance(afterBalance);
        transaction.setRelatedId(relatedId);
        transaction.setDescription(description);
        transaction.setCreateTime(LocalDateTime.now());

        transactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(transaction);
    }

    /**
     * 获取金币余额
     */
    public Integer getBalance(Long userId) {
        CoinAccount account = getAccount(userId);
        return account != null ? account.getBalance() : 0;
    }
}