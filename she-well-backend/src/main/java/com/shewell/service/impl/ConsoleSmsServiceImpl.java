package com.shewell.service.impl;

import com.shewell.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 控制台短信服务实现（开发环境使用）
 * 生产环境请替换为阿里云/腾讯云 SMS 实现
 */
@Slf4j
@Service
public class ConsoleSmsServiceImpl implements SmsService {

    @Override
    public void sendCode(String phone, String code) {
        log.info("【SMS】向 {} 发送验证码: {}", phone, code);
        System.out.println("========================================");
        System.out.println("【SheWell 短信验证码】");
        System.out.println("手机号: " + phone);
        System.out.println("验证码: " + code);
        System.out.println("========================================");
    }
}
