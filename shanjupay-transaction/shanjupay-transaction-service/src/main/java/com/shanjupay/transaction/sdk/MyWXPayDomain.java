package com.shanjupay.transaction.sdk;

/**
 * @author : xsh
 * @create : 2020-11-30 - 1:49
 * @describe:
 */
public class MyWXPayDomain implements IWXPayDomain{
    public void report(String domain, long elapsedTimeMillis, Exception ex) {

    }

    public DomainInfo getDomain(WXPayConfig config) {
        DomainInfo domainInfo=new DomainInfo("api.mch.weixin.qq.com",true);
        return domainInfo;
    }
}
