package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : xsh
 * @create : 2020-08-18 - 1:29
 * @describe:
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${sms.url}")
    private String url;
    @Value("${sms.effectiveTime}")
    private String effectiveTime;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取短信验证码
     *
     * @param phone
     * @return
     */
    @Override
    public String sendMsg(String phone) {
        String sms_url = url + "/generate?name=sms&effectiveTime=" + effectiveTime;//验证码过 期时间为600秒
        log.info("调用短信微服务发送验证码：url:{}", url);

        //请求体
        Map<String, Object> body = new HashMap();
        body.put("mobile", phone);
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置数据格式为json
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //封装请求参数
        HttpEntity entity = new HttpEntity(body, httpHeaders);

        Map responseMap = null;
        try {
            //post请求
            ResponseEntity<Map> exchange = restTemplate.exchange(sms_url, HttpMethod.POST, entity, Map.class);
            log.info("调用短信微服务发送验证码: 返回值:{}", JSON.toJSONString(exchange));
            //获取响应
            responseMap = exchange.getBody();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException("发送验证码出错");
        }

        //取出body中的result数据
        if (responseMap != null || responseMap.get("result") != null) {
            Map resultMap = (Map) responseMap.get("result");
            String key = resultMap.get("key").toString();
            System.out.println(key);
            return key;
        } else {
            throw new RuntimeException("发送验证码出错");
        }
    }

    @Override
    public void checkVerifiyCode(String verifiyKey, String verifiyCode) {
        //实现校验验证码的逻辑
        String check_url = url + "/verify?name=sms&verificationCode=" + verifiyCode + "&verificationKey=" + verifiyKey;
        Map responseMap = null;
        try {
            //请求校验验证码
            ResponseEntity<Map> exchange = restTemplate.exchange(check_url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            responseMap = exchange.getBody();
            log.info("校验验证码，响应内容：{}", JSON.toJSONString(responseMap));
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage(), e);
            throw new RuntimeException("验证码错误");
        }
        if (responseMap == null || responseMap.get("result") == null || !(Boolean) responseMap.get("result")) {
            throw new RuntimeException("验证码错误");
        }
    }

}
