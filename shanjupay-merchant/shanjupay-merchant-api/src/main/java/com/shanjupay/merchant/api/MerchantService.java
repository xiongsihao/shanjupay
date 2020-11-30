package com.shanjupay.merchant.api;


import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;

public interface MerchantService {

    //根据 id查询商户
    public MerchantDTO queryMerchantById(Long id);

    //注册商户,接收账号 密码 手机号
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 资质申请接口
     *
     * @param merchantId  商户id
     * @param merchantDTO 资质申请的信息
     * @throws BusinessException
     */
    void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 商户下新增门店
     *
     * @param storeDTO 门店信息
     * @return 新增成功的门店信息
     * @throws BusinessException
     */
    StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;


    /**
     * 商户新增员工
     * @param staffDTO 员工信息
     * @return 新增成功的员工信息
     * @throws BusinessException
     */
    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;

    //将员工和门店进行绑定
    void bindStaffToStore(Long storeId, Long staffId) throws BusinessException;


    /**
     * 根据租户id查询商户信息
     * @param tenantId
     * @return
     */
    public MerchantDTO queryMerchantByTenantId(Long tenantId);

    /**
     *  分页条件查询商户下门店
     *  @param storeDTO 查询条件，必要参数：商户id
     *  @param pageNo 页码
     *  @param pageSize 每页记录数
     *  @return
     */
    PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize);
}
