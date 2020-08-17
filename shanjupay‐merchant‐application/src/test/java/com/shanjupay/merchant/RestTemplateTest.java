package com.shanjupay.merchant;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : xsh
 * @create : 2020-08-17 - 2:44
 * @describe:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RestTemplateTest {

    @Autowired
    private RestTemplate restTemplate;

    //测试使用restTemplate作为http的客户端向http服务发起请求
    @Test
    public void getHtml() {
        String url = "http://www.baidu.com/";
        //向url发起请求，得到响应结果
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        String body = forEntity.getBody();
        System.out.println(body);
    }

    //获取验证码测试方法
    @Test
    public void testGetSmsCode() {
        String url = "http://localhost:56085/sailing/generate?name=sms&effectiveTime=600";//验证码过 期时间为600秒
        String phone = "13434343434";
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
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("调用短信微服务发送验证码: 返回值:{}", JSON.toJSONString(exchange));
            //获取响应
            responseMap = exchange.getBody();
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }

        //取出body中的result数据
        if (responseMap != null || responseMap.get("result") != null) {
            Map resultMap = (Map) responseMap.get("result");
            String value = resultMap.get("key").toString();
            System.out.println(value);
        }
    }
}
