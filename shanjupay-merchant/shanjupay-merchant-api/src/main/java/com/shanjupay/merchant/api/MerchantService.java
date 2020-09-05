package com.shanjupay.merchant.api;


import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.MerchantDTO;

public interface MerchantService {

    //根据 id查询商户
    public MerchantDTO queryMerchantById(Long id);

    //注册商户,接收账号 密码 手机号
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 资质申请接口
     * @param merchantId 商户id
     * @param merchantDTO 资质申请的信息
     * @throws BusinessException
     */
    void applyMerchant(Long merchantId,MerchantDTO merchantDTO) throws BusinessException;
}
