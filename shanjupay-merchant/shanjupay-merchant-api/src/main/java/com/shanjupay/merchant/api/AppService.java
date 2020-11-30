package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.merchant.api.dto.AppDTO;

import java.util.List;

/**
 * @author : xsh
 * @create : 2020-09-06 - 4:35
 * @describe: 应用管理相关接口
 */
public interface AppService {

    /**
     * 创建应用
     * @param merchantId 商户id
     * @param app        应用的信息
     * @return 创建成功的应用信息
     * @throws BusinessException
     */
    AppDTO createApp(Long merchantId, AppDTO app) throws BusinessException;

    /**
     * 查询商户下的应用列表
     *
     * @param merchantId
     * @return
     * @throws BusinessException
     */
    List<AppDTO> queryAppByMerchant(Long merchantId) throws BusinessException;

    /**
     * 根据业务id查询应用
     *
     * @param id
     * @return
     * @throws BusinessException
     */
    AppDTO getAppById(String id) throws BusinessException;

}
