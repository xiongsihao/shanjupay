package com.shanjupay.merchant.service;

/**
 * @author : xsh
 * @create : 2020-08-17 - 3:07
 * @describe:
 */
public interface SmsService {

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    String sendMsg(String phone);
}
