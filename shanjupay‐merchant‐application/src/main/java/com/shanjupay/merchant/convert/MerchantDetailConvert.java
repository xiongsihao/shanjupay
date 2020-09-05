package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * 将商户资质vo和dto进行转换
 */
@Mapper
public interface MerchantDetailConvert {

    MerchantDetailConvert INSTANCE = Mappers.getMapper(MerchantDetailConvert.class);

    //将dto转成vo
    MerchantDetailVO dto2vo(MerchantDTO merchantDTO);
    //将vo转成dto
    MerchantDTO vo2dto(MerchantDetailVO merchantDetailVO);

}
