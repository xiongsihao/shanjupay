package com.shanjupay.transaction.sdk;

import java.io.InputStream;

/**
 * @author : xsh
 * @create : 2020-11-30 - 1:43
 * @describe:
 */
public class MyWXPayConfig extends WXPayConfig{

    public String getAppID() {
        return "wx632c8f211f8122c6";
    }

    String getMchID() {
        return "1497984412";
    }

    String getKey() {
        return "sbNCm1JnevqI36LrEaxFwcaT0hkGxFnC";
    }

    InputStream getCertStream() {
        return null;
    }

    IWXPayDomain getWXPayDomain() {
        MyWXPayDomain wxDomain = new MyWXPayDomain();
        return wxDomain;
    }
}
