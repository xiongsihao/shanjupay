package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.convert.AppCovert;
import com.shanjupay.merchant.entity.App;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.AppMapper;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author : xsh
 * @create : 2020-09-06 - 4:38
 * @describe:
 */
@org.apache.dubbo.config.annotation.Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppMapper appMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    /**
     * 1）校验商户是否通过资质审核 如果商户资质审核没有通过不允许创建应用。
     * 2）生成应用ID 应用Id使用UUID方式生成。
     * 3）保存商户应用信息 应用名称需要校验唯一性。
     * @param merchantId 商户id
     * @param appDTO 应用信息
     * @return 创建成功返回的应用信息
     * @throws BusinessException
     */
    @Override
    public AppDTO createApp(Long merchantId, AppDTO appDTO) throws BusinessException {


        if(merchantId == null || appDTO ==null || StringUtils.isBlank(appDTO.getAppName())){
            throw new BusinessException(CommonErrorCode.E_300009);
        }


        //校验商户是否通过资质审核
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        //校验商户是否通过资质申请，状态2为通过
        if (!"2".equals(merchant.getAuditStatus())) {
            throw new BusinessException(CommonErrorCode.E_200003);
        }
        if (isExistAppName(appDTO.getAppName())) {
            throw new BusinessException(CommonErrorCode.E_200004);
        }

        //生成应用id
        String appId = UUID.randomUUID().toString();
        App entity = AppCovert.INSTANCE.dto2entity(appDTO);
        entity.setAppId(appId);//应用id
        entity.setMerchantId(merchantId);//商户id
        //调用appMapper向app表插入数据
        appMapper.insert(entity);
        return AppCovert.INSTANCE.entity2dto(entity);
    }

    public Boolean isExistAppName(String appName) {
        Integer count = appMapper.selectCount(new QueryWrapper<App>().lambda().eq(App::getAppName, appName));
        return count.intValue() > 0;
    }
}
