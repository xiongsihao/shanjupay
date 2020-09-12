package com.shanjupay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.entity.PlatformChannel;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 */
@Repository
public interface PlatformChannelMapper extends BaseMapper<PlatformChannel> {

    //根据平台服务类型获取原始支付渠道
    @Select("SELECT\n" +
            "\tpc.* \n" +
            "FROM\n" +
            "\tplatform_pay_channel ppc,\n" +
            "\tpay_channel pc,\n" +
            "\tplatform_channel pla\n" +
            "WHERE\n" +
            "\tppc.pay_channel = pc.CHANNEL_CODE and ppc.PLATFORM_CHANNEL=pla.CHANNEL_CODE\n" +
            "\tAND pla.CHANNEL_CODE = #{platformChannelCode}")
    public List<PayChannelDTO> selectPayChannelByPlatformChannel(String platformChannelCode) ;
}
