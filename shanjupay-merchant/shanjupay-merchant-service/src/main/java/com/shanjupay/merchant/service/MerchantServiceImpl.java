package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@org.apache.dubbo.config.annotation.Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private StoreStaffMapper storeStaffMapper;
    @Reference
    private TenantService tenantService;


    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);

        //MerchantDTO merchantDTO = new MerchantDTO();
        //merchantDTO.setId(merchant.getId());
        //merchantDTO.setMerchantName(merchant.getMerchantName());
        //....

        return MerchantConvert.INSTANCE.entity2dto(merchant);
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
        //校验手机号的合法性
        if (!PhoneUtil.isMatches(merchantDTO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //联系人非空校验
        if (StringUtils.isBlank(merchantDTO.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //密码非空校验
        if (StringUtils.isBlank(merchantDTO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        Boolean matches = PhoneUtil.isMatches(merchantDTO.getMobile());
        System.out.println(matches);

        //校验手机号唯一性，根据手机号查询商户表，如果存在记录则说明已存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if (count > 0) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //添加租户和账号 并绑定关系
        CreateTenantRequestDTO createTenantRequest = new CreateTenantRequestDTO();
        createTenantRequest.setMobile(merchantDTO.getMobile());
        //表示该租户类型是商户
        createTenantRequest.setTenantTypeCode("shanju‐merchant");
        //设置租户套餐为初始化套餐餐
        createTenantRequest.setBundleCode("shanju‐merchant");
        //租户的账号信息
        createTenantRequest.setUsername(merchantDTO.getUsername());
        createTenantRequest.setPassword(merchantDTO.getPassword());
        //新增租户并设置为管理员
        createTenantRequest.setName(merchantDTO.getUsername());
        log.info("商户中心调用统一账号服务，新增租户和账号");
        //调用SaaS接口
        TenantDTO tenantDTO = tenantService.createTenantAndAccount(createTenantRequest);
        if (tenantDTO == null || tenantDTO.getId() == null) {
            throw new BusinessException(CommonErrorCode.E_200012);
        }

        //判断租户下是否已经注册过商户
        Merchant m = merchantMapper.selectOne(new QueryWrapper<Merchant>().lambda().eq(Merchant::getTenantId, tenantDTO.getId()));
        if (m != null && m.getId() != null) {
            throw new BusinessException(CommonErrorCode.E_200017);
        }


        //Merchant merchant = new Merchant();
        //设置手机号
        //merchant.setMobile(merchantDTO.getMobile());
        //...在merchant写入其它DTO的属性
        //使用MapStruct进行对象转换
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //设置商户所属租户
        merchant.setTenantId(tenantDTO.getId());
        //设置审核状态0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝
        merchant.setAuditStatus("0");
        //保存商户
        log.info("保存商户注册信息");
        merchantMapper.insert(merchant);

        //新增门店，创建根门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchant.getId());
        storeDTO.setStoreStatus(true);//门店状态。启用
        storeDTO.setStoreName("根门店");
        storeDTO = createStore(storeDTO);
        log.info("门店信息：{}" + JSON.toJSONString(storeDTO));

        //新增员工，并设置归属门店
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMerchantId(merchant.getId());
        staffDTO.setMobile(merchantDTO.getMobile());
        staffDTO.setUsername(merchantDTO.getUsername());
        //为员工选择归属门店,此处为根门店
        staffDTO.setStoreId(storeDTO.getId());
        staffDTO.setStaffStatus(true);//员工状态，启用
        staffDTO = createStaff(staffDTO);

        //为门店设置管理员
        bindStaffToStore(storeDTO.getId(), staffDTO.getId());

        //将DTO中写入新增的商户的id
        //merchantDTO.setId(merchant.getId());

        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    @Override
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        if (merchantId == null || merchantDTO == null) {
            throw new BusinessException(CommonErrorCode.E_300009);
        }

        //校验商户id merchantId是否在商户表存在，如果不存在则认为非法
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
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

    /**
     * 商户下新增门店
     *
     * @param storeDTO 门店信息
     * @return 新增成功的门店信息
     * @throws BusinessException
     */
    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store store = StoreConvert.INSTANCE.dto2entity(storeDTO);
        System.out.println("商户下新增门店" + JSON.toJSONString(store));
        log.info("商户下新增门店" + JSON.toJSONString(store));
        storeMapper.insert(store);
        return StoreConvert.INSTANCE.entity2dto(store);
    }

    /**
     * 商户新增员工
     *
     * @param staffDTO 员工信息
     * @return 新增成功的员工信息
     * @throws BusinessException
     */
    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {

        //参数合法性校验
        //1.校验手机号格式及是否存在
        String mobile = staffDTO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //在同一个商户下员工的账号和手机号唯一
        //根据商户id和手机号校验唯一性
        if (isExistStaffByMobile(mobile, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        //2.校验用户名是否为空
        String username = staffDTO.getUsername();
        if (StringUtils.isBlank(username)) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //根据商户id和账号校验唯一性
        if (isExistStaffByUserName(username, staffDTO.getMerchantId())) {
            throw new BusinessException(CommonErrorCode.E_100114);
        }

        Staff entity = StaffConvert.INSTANCE.dto2entity(staffDTO);
        log.info("商户下新增员工");
        staffMapper.insert(entity);
        return StaffConvert.INSTANCE.entity2dto(entity);
    }

    //将员工和门店进行绑定
    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStoreId(storeId);
        storeStaff.setStaffId(staffId);
        storeStaffMapper.insert(storeStaff);
    }

    /**
     * 根据租户id查询商户信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public MerchantDTO queryMerchantByTenantId(Long tenantId) {
        Merchant merchant = merchantMapper.selectOne(new QueryWrapper<Merchant>().lambda().eq(Merchant::getTenantId, tenantId));
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 根据手机号判断员工是否已在指定商户存在
     *
     * @param mobile     手机号
     * @param merchantId 商户id
     * @return
     */
    private boolean isExistStaffByMobile(String mobile, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getMobile, mobile).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    /**
     * 根据账号判断员工是否已在指定商户存在
     *
     * @param userName   员工姓名
     * @param merchantId 商户id
     * @return
     */
    private boolean isExistStaffByUserName(String userName, Long merchantId) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<Staff>();
        lambdaQueryWrapper.eq(Staff::getUsername, userName).eq(Staff::getMerchantId, merchantId);
        int i = staffMapper.selectCount(lambdaQueryWrapper);
        return i > 0;
    }

    /**
     * 分页条件查询商户下门店
     *
     * @param storeDTO 查询条件，必要参数：商户id
     * @param pageNo   页码
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize) {
        // 创建分页
        Page<Store> page = new Page<>(pageNo, pageSize);
        // 构造查询条件
        QueryWrapper<Store> qw = new QueryWrapper();
        if (null != storeDTO && null != storeDTO.getMerchantId()) {
            qw.lambda().eq(Store::getMerchantId, storeDTO.getMerchantId());
        }
        // 执行查询
        IPage<Store> storeIPage = storeMapper.selectPage(page, qw);
        // entity List转DTO List
        List<StoreDTO> storeList = StoreConvert.INSTANCE.listentity2dto(storeIPage.getRecords());
        // 封装结果集
        return new PageVO<>(storeList, storeIPage.getTotal(), pageNo, pageSize);
    }
}
