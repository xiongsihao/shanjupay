package com.shanjupay.merchant.api;

import com.shanjupay.merchant.api.dto.MerchantDTO;


public interface MerchantService {

    //根据 id查询商户
    public MerchantDTO queryMerchantById(Long id);
}
