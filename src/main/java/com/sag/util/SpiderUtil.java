package com.sag.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫工具类
 */

public class SpiderUtil {

	public static String httpPost(String songId, int offset) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String first_param = "{rid:\"\", offset:\"" + offset + "\", total:\"true\", limit:\"" + Constants.ONE_PAGE + "\", csrf_token:\"\"}";
		try {
			// 参数加密，16位随机字符串，直接FFF
			String secKey = "FFFFFFFFFFFFFFFF";
			// 两遍ASE加密
			String encText = aesEncrypt(aesEncrypt(first_param, "0CoJUm6Qyw8W8jud"), secKey);
			String encSecKey = rsaEncrypt();
			HttpPost httpPost = new HttpPost("http://music.163.com/weapi/v1/resource/comments/R_SO_4_" + songId + "/?csrf_token=");
			httpPost.addHeader("Referer", Constants.BASE_URL);
			List<NameValuePair> ls = new ArrayList<NameValuePair>();
			ls.add(new BasicNameValuePair("params", encText));
			ls.add(new BasicNameValuePair("encSecKey", encSecKey));
			UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(ls, "utf-8");
			httpPost.setEntity(paramEntity);
			//设置代理
			HttpHost proxy = new HttpHost(Constants.PROXY_HOST,Constants.PROXY_PORT);
   			RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
			httpPost.setConfig(requestConfig);

			httpPost.setHeader("Proxy-Authorization", authHeader());
			httpPost.setHeader("userAgent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * ASE-128-CBC加密模式可以需要16位
	 */
	private static String aesEncrypt(String src, String key) throws Exception {
		String encodingFormat = "UTF-8";
		String iv = "0102030405060708";
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
		// 使用CBC模式，需要一个向量vi，增加加密算法强度
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] encrypted = cipher.doFinal(src.getBytes(encodingFormat));
		return new BASE64Encoder().encode(encrypted);
	}

	/**
	 * http://www.xdaili.cn/usercenter/order
	 * 讯代理买了10￥的动态转发100000次
	 */
	public static String authHeader(){
		int timestamp = (int)(System.currentTimeMillis()/1000);
		//拼装签名字符串
		String planText = String.format("orderno=%s,secret=%s,timestamp=%d", Constants.PROXY_ORDER_NO, Constants.PROXY_SECRET, timestamp);
		//计算签名
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(planText).toUpperCase();
		//拼装请求头Proxy-Authorization的值
		return String.format("sign=%s&orderno=%s&timestamp=%d", sign, Constants.PROXY_ORDER_NO, timestamp);
	}

	private static String rsaEncrypt() {
		return "257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffca5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";
	}

	/**
	 * 运行时间时间格式化
	 */
	public static String parseMillisecond(long millisecond) {
		String time = null;
		try {
			long yushu_day = millisecond % (1000 * 60 * 60 * 24);
			long yushu_hour = (millisecond % (1000 * 60 * 60 * 24))
					% (1000 * 60 * 60);
			long yushu_minute = millisecond % (1000 * 60 * 60 * 24)
					% (1000 * 60 * 60) % (1000 * 60);
			@SuppressWarnings("unused")
			long yushu_second = millisecond % (1000 * 60 * 60 * 24)
					% (1000 * 60 * 60) % (1000 * 60) % 1000;
			if (yushu_day == 0) {
				return (millisecond / (1000 * 60 * 60 * 24)) + "天";
			} else {
				if (yushu_hour == 0) {
					return (millisecond / (1000 * 60 * 60 * 24)) + "天"
							+ (yushu_day / (1000 * 60 * 60)) + "时";
				} else {
					if (yushu_minute == 0) {
						return (millisecond / (1000 * 60 * 60 * 24)) + "天"
								+ (yushu_day / (1000 * 60 * 60)) + "时"
								+ (yushu_hour / (1000 * 60)) + "分";
					} else {
						return (millisecond / (1000 * 60 * 60 * 24)) + "天"
								+ (yushu_day / (1000 * 60 * 60)) + "时"
								+ (yushu_hour / (1000 * 60)) + "分"
								+ (yushu_minute / 1000) + "秒";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 消除类型转换warning
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	/**
	 * 将时间戳转换为时间
     */
	public static String stampToDate(long timeLong) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeLong);
	}

	/**
	 * 将emoji表情替换成*
	 */
	public static String filterEmoji(String source) {
		if (StringUtils.isNotBlank(source)) {
			return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
		} else {
			return source;
		}
	}

}
