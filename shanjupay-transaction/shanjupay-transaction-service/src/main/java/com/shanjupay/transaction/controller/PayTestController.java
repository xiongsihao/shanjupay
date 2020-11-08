package com.shanjupay.transaction.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : xsh
 * @create : 2020-10-15 - 0:31
 * @describe: 支付宝接口对接测试类
 */
@Slf4j
@Controller
public class PayTestController {

    String APP_ID = "2016101900723922";
    String APP_PRIVATE_KEY="xxx+PWcWPSiNnRwcs56NI/TAfnMFn6grHKlNCNPIfP8Xh4P1Y09b2xzrIcsIYIpTr0AV/MI+5RYYFrjQpWq2dmPlkLbNZOUBtYx1YfUBFssaMGagToHocUim9XbzvOhDAWn0Z7OZq5pT/i/p7JYcC+W4F4r0ogMRn29A4ZVuFlLqthJxRAOOm81MRvFzDY9tKAMPBCS/lbvLI9V0Rxoj7KFygZP45rh0pgd/vUsfRzGBEXAei29JHXN1tnjjLu6j8oQ2NbgavtNqQzxVHlDlxo4yCKA3CdEQAHEP44ESHaUummEO1iIeKe6/6DxEyrFAgMBAAECggEAM1CYh0GH833PNynfRJPlsfuzsajUptC+mY4RP++EMLsLuIsOrrb5yh+k9Qn8U/7svmV1XhxtOMAASB1nqCNuC3epzEOVvELLAKlN6T1XcvNMbpNQ26cB0AMJNO2m52+Bu1Ebg3MaHG2L0qlzY6n/PwGvO7RQm5Y5+jZSlwet2PhfFkv8t8GKKuELVlio8Ec8c8Zpd8wdzd5QjDTjB5YRE6BHk66iX/ytxAlOnNafq39IW8mGETYHC43Rx6klAGF+s0PtedZJ+8Mwj8IRsVvj/pDVhgDKE304fyycPrO4/wIQ/I8xlsCdwpS3umd2IvR+GcMLYLE7CWuSl98rD2RMDQKBgQD9HtpVjvjRNM7moizpMmZukkZrK9VvwaVnbzdDE/t/hi7Iap4uJp2dfN9CGLjOAlw2COGQnPys99OeRu4yefXlmut1z4Hyo0Sr38cbazJCcbSjZLlM3LoFuAP364B5YCMCcELM1veOH2gY3xVMyuPwJQoLLoxjczmSL3NqBsdBswKBgQC7M8VYt7PFZ55HNZv3rkW/OnjabHcQcO64lKB8Glx5/lyrLZjlVMpfy9ereFb8NgAIKRMZE0o7SasmdMZw3qae7hQjV8mGd6eCT8CQ+1bcnLjFNu7NCUt72yYj10oepo5QeOycm3Ko6GQO1nA7W15lLjJnC8Xtge12Azsgvm31pwKBgCm7TXnvsXwkfbwunsLvyU2PlkaTGoRrY87S2kSX5c4XnFz0nxGy0iel79Ug6E8munc6LZ4/E7WcwmoE/b6atvzc9tccmCsd2fOQWG5f1RB5WLPbpmVSuuS4ewcD79GTFRKQ2aKWCoiHCw2WshdQGBZ+tNI1IXZACAze/+NKLM3bAoGAJxlRBo6XxYs2y7iusrR7bM1RoI028QAlW/fu7Py0g+ZUhIwpevySqB9zFCU2RcchipLJolsdDfT9AjMxnzFhq6AeJKOZO7SSD+2IUr+sDLfO/6HRjpF6aowDE0lL+crfvn8DLFJrSEwBWff/yOX0eT2E2XDkDg8tAZD7MIOVg9ECgYEA6EE/EmsmITNxAcjnlMB49dRPuyflNdb8uFFsaXPOR97R73o128g2uGCB26yB27QT/teZHlRu6ksTp9qjAGj2ijCO5E1NE4zx8wrjU++3NzEdv+TDng2OG7h1POJX3qgt+lcQNeDBaEW0/IBZPI6H5VcpSdZi3HMA0nXbLNQt9Qs=";
    String ALIPAY_PUBLIC_KEY ="xxx+cIyqK+UPJatBH/4br6cvbMVc4f3sganvkT1IK07A6Sz0tLLjeNIlcSUyeO8m5gtmsEjXd4wiMw+H2VaVHiu8KT9yGzrtiZr7BQqVrJwpBHXh5awpcukhGnB9roww2qx0A2HRwWvuuB3Y9srONcuINx+9+PJ+ztAY42zpidffSbOQKwKQBFZNXe4I8tcRA1y58r7bFtov7W2wtNErkVaTebT9Osd58FQt9Ovw19vTWwn4wHgwIDAQAB";
    String CHARSET = "utf‐8";
    String serverUrl = "https://openapi.alipaydev.com/gateway.do";//正 式"https://openapi.alipay.com/gateway.do"


    @GetMapping("/alipaytest")
    public void alipaytest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        //构造AliPay sdk的客户端对象
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl,
                APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        //alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        //alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\"20150320010101002\"," +
                " \"total_amount\":\"88.88\"," +
                " \"subject\":\"Iphone6 16G\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");//填充业务参数
        String form = "";
        try {
            //请求支付宝的下单接口
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
}
