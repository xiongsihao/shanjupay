package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;

/**
 * @author : xsh
 * @create : 2020-08-17 - 3:07
 * @describe:
 */
public interface SmsService {

    /**
     * 获取短信验证码
     *
     * @param phone
     * @return
     */
    String sendMsg(String phone);


    /**
     * 校验验证码，抛出异常则校验无效
     * @param verifiyKey 验证码key
     * @param verifiyCode 验证码
     */
    void checkVerifiyCode(String verifiyKey, String verifiyCode) throws BusinessException;
}
