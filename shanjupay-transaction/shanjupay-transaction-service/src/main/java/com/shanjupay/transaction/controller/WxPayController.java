package com.shanjupay.transaction.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.shanjupay.transaction.sdk.MyWXPayConfig;
import com.shanjupay.transaction.sdk.WXPay;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("wxpay")
public class WxPayController {

    @RequestMapping("dopay")
    public void dopay(HttpServletResponse response){

        //用于生成订单编号的随机数
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHssmm");
        String orderIdPrefix = sdf.format(date);//创建订单编号的前缀
        String pid="HP";//商品id,目前固定一个
        String orderId=orderIdPrefix+pid;//最终订单id
        MyWXPayConfig config = new MyWXPayConfig();
        WXPay wxpay = null;
        try {
            wxpay = new WXPay(config);

            Map<String, String> data = new HashMap<String, String>();
            /*支付显示title*/
            data.put("body", "qwer");
            /*订单标号，唯一*/
            data.put("out_trade_no", orderId);
            /*设备信息*/
            data.put("device_info", "");
            /*货币单位，分*/
            data.put("fee_type", "CNY");
            /*总付费 单位分*/
            data.put("total_fee", "1");
            data.put("spbill_create_ip", "123.12.12.123");//ip
            /*自定义回调地址,获取此次微信支付信息*/
            data.put("notify_url", "http://xiongsihao.free.idcfengye.com/transaction/wxpay/notify_url");
            data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
            /*商品id*/
            data.put("product_id", "001");

            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);

            //将支付链接转换成二维码
            String code_url = resp.get("code_url");//二维码需要包含的文本内容
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 2);
            try {
                BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE, 200, 200, hints);
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", response.getOutputStream());
                System.out.println("创建二维码完成");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 微信回调地址
     * @return
     */
    @RequestMapping("notify_url")
    public void getNotifyURL(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取微信发送来的请求，从请求消息中获得数据
        ServletInputStream is = request.getInputStream();
        byte[] b=new byte[1024];
        int len=0;
        String str=null;
        while((len=is.read(b))!=-1){
            str=new String(b,0,len);
            System.out.println(str);
        }

        //返回一个标准格式的回信，告诉微信请求成功,付款成功
        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");

    }
}
