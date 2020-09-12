package com.shanjupay.transaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import com.shanjupay.transaction.convert.PlatformChannelConvert;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import com.shanjupay.transaction.entity.PlatformChannel;
import com.shanjupay.transaction.mapper.AppPlatformChannelMapper;
import com.shanjupay.transaction.mapper.PlatformChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : xsh
 * @create : 2020-09-09 - 2:32
 * @describe:
 */
@org.apache.dubbo.config.annotation.Service
public class PayChannelServiceImpl implements PayChannelService {
    @Autowired
    private PlatformChannelMapper platformChannelMapper;

    @Autowired
    private AppPlatformChannelMapper appPlatformChannelMapper;

    /**
     * 查询平台全部的服务类型
     *
     * @return
     */
    @Override
    public List<PlatformChannelDTO> queryPlatformChannel() {
        //查询platform_channel表的全部记录
        List<PlatformChannel> platformChannels = platformChannelMapper.selectList(null);
        //将List<PlatformChannel>转成包含dto的List<PlatformChannelDTO>
        List<PlatformChannelDTO> platformChannelDTOS = PlatformChannelConvert.INSTANCE.listentity2listdto(platformChannels);
        return platformChannelDTOS;
    }

    @Override
    @Transactional
    public void bindPlatformChannelForApp(String appId, String platformChannelCodes) throws BusinessException {
        //根据appId和平台服务类型code查询app_platform_channel
        AppPlatformChannel appPlatformChannel =
                appPlatformChannelMapper.selectOne(
                        new LambdaQueryWrapper<AppPlatformChannel>()
                                .eq(AppPlatformChannel::getAppId, appId)
                                .eq(AppPlatformChannel::getPlatformChannel, platformChannelCodes));
        //如果没有绑定则绑定
        if (appPlatformChannel == null) {
            appPlatformChannel = new AppPlatformChannel();
            appPlatformChannel.setAppId(appId);
            appPlatformChannel.setPlatformChannel(platformChannelCodes);
            appPlatformChannelMapper.insert(appPlatformChannel);
        }
    }

    @Override
    public int queryAppBindPlatformChannel(String appId, String platformChannel) {
        int count = appPlatformChannelMapper.selectCount(new QueryWrapper<AppPlatformChannel>().lambda().eq(AppPlatformChannel::getAppId, appId).eq(AppPlatformChannel::getPlatformChannel, platformChannel));
        //已存在绑定关系返回1
        if (count > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}

