package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


@org.apache.dubbo.config.annotation.Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchant.getId());
        merchantDTO.setMerchantName(merchant.getMerchantName());
        //....
        return merchantDTO;
    }

    @Override
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException {

        //校验参数的合法性
        if (merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        if (StringUtils.isBlank(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        Boolean matches = PhoneUtil.isMatches(merchantDTO.getMobile());
        System.out.println(matches);
        //手机号格式校验
        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //校验手机号唯一性，根据手机号查询商户表，如果存在记录则说明已存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if(count>0){
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //Merchant merchant = new Merchant();
        //设置手机号
        //merchant.setMobile(merchantDTO.getMobile());
        //...在merchant写入其它DTO的属性
        //使用MapStruct进行对象转换
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);

        //设置审核状态0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝
        merchant.setAuditStatus("0");
        //保存商户
        merchantMapper.insert(merchant);

        //将DTO中写入新增的商户的id
        //merchantDTO.setId(merchant.getId());

        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    @Override
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        if(merchantId == null || merchantDTO == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }

        //校验商户id merchantId是否在商户表存在，如果不存在则认为非法
        Merchant merchant = merchantMapper.selectById(merchantId);
        if(merchant == null){
            throw new BusinessException((CommonErrorCode.E_200002));
        }

        //将dto转成entity
        Merchant entity = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //更新之前，要将必要的参数设置到entity
        entity.setId(merchant.getId());
        entity.setMobile(merchant.getMobile());//因为资质申请的时候手机号比较重要，不让修改，所以取出数据库原有的手机号再更新
        entity.setAuditStatus("1");//审核状态为1-已申请待审核
        entity.setTenantId(merchant.getTenantId());
        //调用mapper更新商户表
        merchantMapper.updateById(entity);
    }
}
