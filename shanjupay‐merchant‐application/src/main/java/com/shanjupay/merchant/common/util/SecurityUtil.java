package com.shanjupay.merchant.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjupay.common.util.EncryptUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/***
 * 获取当前登录用户信息
 * 前端配置token，后续每次请求并通过Header方式发送至后端
 */
public class SecurityUtil {

	//测试使用
	public static Long getMerchantId() {
		HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
				.getRequest();
		String jsonToken = request.getHeader("authorization");
		if (StringUtils.isEmpty(jsonToken) || !jsonToken.startsWith("Bearer ")) {
			throw new RuntimeException("token is not as expected");
		}
		jsonToken = jsonToken.substring(7);
		jsonToken = EncryptUtil.decodeUTF8StringBase64(jsonToken);
		JSONObject jsonObject = JSON.parseObject(jsonToken);
		return jsonObject.getLong("merchantId");
	}

	/**
	 * 根据租户ID查询商户ID
	 * @param tenantId
	 * @return
	 */
/*	public static Long getMerchantId(Long tenantId){
		MerchantService merchantService = ApplicationContextHelper.getBean(MerchantService.class);
		MerchantDTO merchant = merchantService.queryMerchantByTenantId(tenantId);
		Long merchantId = null;
		if(merchant!=null){
			merchantId = merchant.getId();
		}
		return merchantId;
	}*/

	/**
	 * 获取当前登录用户信息
	 * @return
	 */
	public static LoginUser getUser() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();

		if (servletRequestAttributes != null) {
			HttpServletRequest request = servletRequestAttributes.getRequest();

			Object jwt = request.getAttribute("jsonToken");
			if (jwt instanceof LoginUser) {
				return (LoginUser) jwt;
			}
		}
		return new LoginUser();
	}

	/*	token 明文格式
		{
			"mobile": "",
				"payload": "",
				"client_id": "merchant-platform",
				"user_name": "",
				"merchantId":"1196392632578809858"
		}*/
	public static void main(String[] args) {
		String jsonToken = "Bearer ewoJIm1vYmlsZSI6ICIiLAoJInBheWxvYWQiOiAiIiwKCSJjbGllbnRfaWQiOiAibWVyY2hhbnQtcGxhdGZvcm0iLAoJInVzZXJfbmFtZSI6ICIiLAoJIm1lcmNoYW50SWQiOiIxMTk2MzkyNjMyNTc4ODA5ODU4Igp9";
		if (StringUtils.isEmpty(jsonToken) || !jsonToken.startsWith("Bearer ")) {
			throw new RuntimeException("token is not as expected");
		}

		jsonToken = jsonToken.substring(7);
		jsonToken = EncryptUtil.decodeUTF8StringBase64(jsonToken);
		JSONObject jsonObject = JSON.parseObject(jsonToken);
		System.out.println(jsonObject.getString("merchantId"));
	}

}
