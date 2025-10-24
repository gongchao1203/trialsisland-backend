package com.trialsisland.service;

import com.trialsisland.entity.CoinTransaction;
import com.trialsisland.entity.User;
import com.trialsisland.enums.CoinTransactionType;
import com.trialsisland.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 金币服务类
 */
@Slf4j
@Service
public class CoinService {

    @Autowired
    private UserService userService;
}
