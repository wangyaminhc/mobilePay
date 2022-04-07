package com.sssoft_lib.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.text.TextUtils;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.json.sssoft.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class MobileUtil {
	public static String code2Channel(String barcode,HashMap<String,String> map){
		Iterator iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String,String> entry = (Map.Entry<String,String>)iter.next();
			if(Pattern.compile(entry.getValue()).matcher(barcode).matches()){
				return entry.getKey();
			}
		}
		return "";
	}



	public static void setLanguage(Context context,SharedPreferences preferences){
		 String localStr = context.getResources().getConfiguration().locale.getLanguage();
		 String language =  preferences.getString("language", "");
		 Configuration config = context.getResources().getConfiguration();
		 if(language.equals("")){
			 if(localStr.equals("zh")){
				 Locale.setDefault(Locale.CHINESE); 
		         config.locale = Locale.CHINESE;
			 }else{
				 Locale.setDefault(Locale.US); 
		         config.locale = Locale.US;
			 }
		 }else{
			 if(language.equals("us")){
				 Locale.setDefault(Locale.US); 
		         config.locale = Locale.US;
			 }else{
				 Locale.setDefault(Locale.CHINESE); 
		         config.locale = Locale.CHINESE;
			 } 
		 }
		 
        context.getResources().updateConfiguration(config
                    , context.getResources().getDisplayMetrics());
	}
	public static String getVoucher(){
		return (new Date()).getTime()+""+(int)((Math.random()*9+1)*100000);
	}
	private static final int BLACK = 0xff000000;
	public static Bitmap createQRCode(String content) throws Exception{
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");      
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();     
        int height = matrix.getHeight();     
        int[] pixels = new int[width * height];      
             
        for (int y = 0; y < height; y++) {     
            for (int x = 0; x < width; x++) {     
                if (matrix.get(x, y)) {     
                    pixels[y * width + x] = BLACK;     
                }     
            }     
        }     
        Bitmap bitmap = Bitmap.createBitmap(width, height,     
                Bitmap.Config.ARGB_8888);     
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);     
        return bitmap;     
	}


	//
	private static HashMap<String,String> barCodeFlagMap = new HashMap<String, String>();
	private static HashMap<String,String> txnTypeTcpMap = new HashMap<String, String>();
	private static HashMap<String,String> channelMap = new HashMap<String, String>();
	public static HashMap<String, String> getBarCodeFlagMap() {
		barCodeFlagMap.put("2", "^13[0-9]+$");
		barCodeFlagMap.put("6", "^(2[5-9]|30)[0-9]+$");
		//^28[0-9]+$
		//barCodeFlagMap.put("TENPAY", "^91[0-9]+$");
		return barCodeFlagMap;
	}
	public static HashMap<String, String> getTxnTypeTcpMap() {
		txnTypeTcpMap.put("101", "02");
		txnTypeTcpMap.put("102", "03");
		txnTypeTcpMap.put("103", "04");
		return txnTypeTcpMap;
	}
	/*public static HashMap<String, String> getChannelMap() {
		channelMap.put("1", "支付宝1.0");
		channelMap.put("2", "微信");
		channelMap.put("3", "环迅银行卡");
		channelMap.put("5", "大众点评");
		channelMap.put("6", "支付宝2.0");
		channelMap.put("10", "现金");
		channelMap.put("11", "银行卡");
		channelMap.put("12", "预付卡");
		return channelMap;
	}*/

	public static JSONObject doJsonMessage(String respStr){
		JSONObject  backJson = new JSONObject(respStr);
		if(backJson.has("UnfreezeAmt")){
			backJson.put("TxnAmt", backJson.getDouble("UnfreezeAmt"));
		}
		if(backJson.has("FreezeAmt")){
			backJson.put("TxnAmt", backJson.getDouble("FreezeAmt"));
		}
		return backJson;
	}
	//获取子渠道名称数组
	private static HashMap<String,String>  accTypeNameMap = new HashMap<String, String>();
	public static HashMap<String, String> getAccTypeNameMap() {
		accTypeNameMap.put(Constant.AccType.ALIPAY, 	"支付宝");
		accTypeNameMap.put(Constant.AccType.WECHAT, 		"微信");
		accTypeNameMap.put(Constant.AccType.QQ_PAY, 		"QQ钱包");

		accTypeNameMap.put(Constant.AccType.BANK, 	"银行卡");
		accTypeNameMap.put(Constant.AccType.GIFIT, 		"预付卡");
		accTypeNameMap.put(Constant.AccType.BAIDU_PAY, 		"百度钱包");

		accTypeNameMap.put(Constant.AccType.YI_PAY, 	"翼支付");
		accTypeNameMap.put(Constant.AccType.JD_PAY, 			"京东钱包");
		accTypeNameMap.put(Constant.AccType.YIFUBAO, 			"易付宝");

		accTypeNameMap.put(Constant.AccType.UNION, 		"银联二维码");
		accTypeNameMap.put(Constant.AccType.MEMBER_CARD, 		"会员卡");
		accTypeNameMap.put(Constant.AccType.ELEMA, 		"饿了么企餐");

		accTypeNameMap.put(Constant.AccType.DIGITAL, 		"数字货币");
		return accTypeNameMap;
	};



	//获取子渠道名称数组
	private static HashMap<String,String> brachChannelNameMap = new HashMap<String, String>();
	public static HashMap<String, String> getBrachChannelNameMap() {
		brachChannelNameMap.put("Bestpay", 	"RoyalPay-翼支付");
		brachChannelNameMap.put("Alipay", 		"RoyalPay-支付宝");
		brachChannelNameMap.put("Wechat", 		"RoyalPay-微信");

		brachChannelNameMap.put("umsunionpay", 	"银商互联网-银联二维码");
		brachChannelNameMap.put("umsalipay", 		"银商互联网-支付宝");
		brachChannelNameMap.put("umsweixin", 		"银商互联网-微信");

		brachChannelNameMap.put("EBUSMBNK", 	"农行互联网-掌银");
		brachChannelNameMap.put("WX", 			"农行互联网-微信");
		brachChannelNameMap.put("ZFB", 			"农行互联网-支付宝");

		brachChannelNameMap.put("ZFBA", 		"中行互联网-支付宝");
		brachChannelNameMap.put("WEIX", 		"中行互联网-微信");
		brachChannelNameMap.put("UPAY", 		"中行互联网-银联二维码");

		brachChannelNameMap.put("817001", 		"工行互联网-支付宝");
		brachChannelNameMap.put("817002", 		"工行互联网-微信");
		brachChannelNameMap.put("817003", 		"工行互联网-银联二维码");

		brachChannelNameMap.put("892001", 		"贵州农信-支付宝");
		brachChannelNameMap.put("892002", 		"贵州农信-微信");
		brachChannelNameMap.put("892003", 		"贵州农信-云闪付");
		brachChannelNameMap.put("892004", 		"贵州农信-黔农云");

		brachChannelNameMap.put("893001", 		"敏付-支付宝");
		brachChannelNameMap.put("893002", 		"敏付-微信");
		brachChannelNameMap.put("893003", 		"敏付-聚合扫码");
		return brachChannelNameMap;
	};
	/*//获取子渠道数组
	private static HashMap<String,String> brachChannelMap = new HashMap<String, String>();
	public static HashMap<String, String> getBrachChannelMap() {
		brachChannelMap.put("Bestpay", 	Constant.ChannelID.UNIONPAY);
		brachChannelMap.put("Alipay", 		Constant.ChannelID.ALIPAY);
		brachChannelMap.put("Wechat", 		Constant.ChannelID.WCPAY);

		brachChannelMap.put("umsunionpay", 	Constant.ChannelID.UNIONPAY);
		brachChannelMap.put("umsalipay", 		Constant.ChannelID.ALIPAY);
		brachChannelMap.put("umsweixin", 		Constant.ChannelID.WCPAY);

		brachChannelMap.put("EBUSMBNK", 	Constant.ChannelID.UNIONPAY);
		brachChannelMap.put("WX", 			Constant.ChannelID.WCPAY);
		brachChannelMap.put("ZFB", 			Constant.ChannelID.ALIPAY);

		brachChannelMap.put("ZFBA", 		Constant.ChannelID.ALIPAY);
		brachChannelMap.put("WEIX", 		Constant.ChannelID.WCPAY);
		brachChannelMap.put("UPAY", 		Constant.ChannelID.UNIONPAY);

		brachChannelMap.put("817001", 		Constant.ChannelID.ALIPAY);
		brachChannelMap.put("817002", 		Constant.ChannelID.WCPAY);
		brachChannelMap.put("817003", 		Constant.ChannelID.UNIONPAY);

		brachChannelMap.put("892001", 		Constant.ChannelID.ALIPAY);
		brachChannelMap.put("892002", 		Constant.ChannelID.WCPAY);
		brachChannelMap.put("892003", 		Constant.ChannelID.UNIONPAY);//贵州农信-银联二维码
		brachChannelMap.put("892004", 		Constant.ChannelID.UNIONPAY);//黔农云

		brachChannelMap.put("893001", 		Constant.ChannelID.ALIPAY);
		brachChannelMap.put("893002", 		Constant.ChannelID.WCPAY);
		brachChannelMap.put("893003", 		Constant.ChannelID.UNIONPAY);
		brachChannelMap.put("ALIPAY_SCAN", 	Constant.ChannelID.ALIPAY);
		brachChannelMap.put("WECHAT_SCAN", 	Constant.ChannelID.WCPAY);
		brachChannelMap.put("ALIPAY_USCAN", Constant.ChannelID.ALIPAY);
		brachChannelMap.put("WECHAT_USCAN", Constant.ChannelID.WCPAY);
		brachChannelMap.put("GATHER_USCAN", Constant.ChannelID.UNIONPAY);
		brachChannelMap.put("QUICKPAY", 	Constant.ChannelID.UNIONPAY);
		return brachChannelMap;
	};*/


	//获取渠道名称数组
	private static HashMap<String,String> channelNameMap = new HashMap<String, String>();
	public static HashMap<String, String> getChannelNameMap() {
		channelNameMap.put("1", 		"支付宝1.0-直连");
		channelNameMap.put("2", 		"微信-直连");
		channelNameMap.put("4", 		"淘点点-直连");
		channelNameMap.put("5", 		"大众点评-直连");
		channelNameMap.put("6", 		"支付宝2.0-直连");
		channelNameMap.put("7", 		"支付宝-国际");
		channelNameMap.put("12", 		"微信-Weepay");
		channelNameMap.put("13", 		"支付通-支付宝");
		channelNameMap.put("14", 		"中行福建-支付宝");
		channelNameMap.put("15", 		"中行福建-微信");
		channelNameMap.put("16", 		"QQ钱包-直连");
		channelNameMap.put("17", 		"工行互联网聚合");//TODO
		channelNameMap.put("18", 		"支付通-微信");
		channelNameMap.put("19", 		"FOMOPay-支付宝");
		channelNameMap.put("20", 		"FOMOPay-微信");
		channelNameMap.put("21", 		"FOMOPay-百度钱包");
		channelNameMap.put("24", 		"RoyalPay-聚合");//TODO
		channelNameMap.put("25", 		"AscanPay-支付宝");
		channelNameMap.put("26", 		"银商互联网聚合");//TODO
		channelNameMap.put("27", 		"中行江苏银行卡");
		channelNameMap.put("28", 		"中行江苏支付宝");
		channelNameMap.put("29", 		"中行江苏微信");
		channelNameMap.put("30", 		"中行江苏二维码");
		channelNameMap.put("31", 		"农行互联网聚合");//TODO
		channelNameMap.put("32", 		"美团支付宝");
		channelNameMap.put("33", 		"美团微信");
		channelNameMap.put("34", 		"DBS-BankCard");
		channelNameMap.put("35", 		"银联聚合支付");//TODO
		channelNameMap.put("36", 		"农商行江苏银行卡");
		channelNameMap.put("37", 		"农商行江苏支付宝");
		channelNameMap.put("38", 		"农商行江苏微信");
		channelNameMap.put("39", 		"农商行江苏二维码");
		channelNameMap.put("40", 		"KiplePay-Qrcode");
		channelNameMap.put("41", 		"微信-商户直连");
		channelNameMap.put("42", 		"交行POS银行卡");
		channelNameMap.put("43", 		"交行POS微信");
		channelNameMap.put("44", 		"交行POS支付宝");
		channelNameMap.put("45", 		"交行POS云闪付");
		channelNameMap.put("46", 		"交行POS-QQ");
		channelNameMap.put("47", 		"农行POS银行卡");
		channelNameMap.put("48", 		"农行POS微信");
		channelNameMap.put("49", 		"农行POS支付宝");
		channelNameMap.put("50", 		"农行POS云闪付");
		channelNameMap.put("51", 		"农行POS-QQ");
		channelNameMap.put("52", 		"建行POS银行卡");
		channelNameMap.put("53", 		"建行POS微信");
		channelNameMap.put("54", 		"建行POS支付宝");
		channelNameMap.put("55", 		"建行POS云闪付");
		channelNameMap.put("56", 		"建行POS-QQ");
		channelNameMap.put("61", 		"中行POS银行卡");
		channelNameMap.put("62", 		"中行POS支付宝");
		channelNameMap.put("63", 		"中行POS微信");
		channelNameMap.put("64", 		"中行POS云闪付");
		channelNameMap.put("65", 		"中行POS-QQ");
		channelNameMap.put("66", 		"工行POS银行卡");
		channelNameMap.put("67", 		"工行POS微信");
		channelNameMap.put("68", 		"工行POS支付宝");
		channelNameMap.put("69", 		"工行POS云闪付");
		channelNameMap.put("70", 		"工行POS-QQ");
		channelNameMap.put("71", 		"工行POS聚合");//TODO
		channelNameMap.put("72", 		"慧金宝");
		channelNameMap.put("73", 		"银联直连云闪付");
		channelNameMap.put("74", 		"建行POS龙支付");
		channelNameMap.put("75", 		"华润预付卡");
		channelNameMap.put("76", 		"杉徳银联二维码");
		channelNameMap.put("77", 		"富友汇丰银行卡");
		channelNameMap.put("78", 		"富友支付宝");
		channelNameMap.put("79", 		"富友微信");
		channelNameMap.put("80", 		"中行互联网聚合");//TODO
		channelNameMap.put("81", 		"富友服务商支付宝");
		channelNameMap.put("82", 		"富友服务商微信");
		channelNameMap.put("83", 		"富友服务商银联二维码");

		channelNameMap.put("92", 		"贵州农信");
		channelNameMap.put("93", 		"敏付");

		channelNameMap.put("BDPAY", 		"富基-直连-百度钱包");
		channelNameMap.put("BESTPAY", 		"富基-直连-翼支付-直连");
		channelNameMap.put("ICBC", 		"工行PatT银行卡");
		channelNameMap.put("IPS", 			"环迅银行卡");
		channelNameMap.put("JDPAY", 		"富基-直连-京东钱包-直连");
		channelNameMap.put("JSABC", 		"农行江苏银行卡");
		channelNameMap.put("MIAOJIE", 		"富基-直连-喵街-直连");
		channelNameMap.put("ogs1", 		"OGS点餐");
		channelNameMap.put("ogs2", 		"OGS充值");
		channelNameMap.put("SNPAY", 		"富基-直连-易付宝-直连");
		channelNameMap.put("TENPAY", 		"富基-直连-QQ钱包-直连");

		return channelNameMap;
	};
	/*//获取渠道名称数组
	public static HashMap<String, String> getChannelMap() {
		channelMap.put("1", 		Constant.ChannelID.ALIPAY);
		channelMap.put("2", 		Constant.ChannelID.WCPAY);
		channelMap.put("4", 		Constant.ChannelID.JHPAY);//TODO 淘点点
		channelMap.put("5", 		Constant.ChannelID.JHPAY);//TODO 大众点评
		channelMap.put("6", 		Constant.ChannelID.ALIPAY);
		channelMap.put("7", 		Constant.ChannelID.ALIPAY);
		channelMap.put("12", 		Constant.ChannelID.WCPAY);
		channelMap.put("13", 		Constant.ChannelID.ALIPAY);
		channelMap.put("14", 		Constant.ChannelID.ALIPAY);
		channelMap.put("15", 		Constant.ChannelID.WCPAY);
		channelMap.put("16", 		Constant.ChannelID.JHPAY);//TODO QQ钱包直连
		channelMap.put("17", 		Constant.ChannelID.JHPAY);//TODO 工行互联网聚合
		channelMap.put("18", 		Constant.ChannelID.WCPAY);
		channelMap.put("19", 		Constant.ChannelID.ALIPAY);
		channelMap.put("20", 		Constant.ChannelID.WCPAY);
		channelMap.put("21", 		Constant.ChannelID.JHPAY);//TODO FOMOPay-百度钱包
		channelMap.put("24", 		Constant.ChannelID.JHPAY);//TODO RoyalPay-聚合
		channelMap.put("25", 		Constant.ChannelID.ALIPAY);
		channelMap.put("26", 		Constant.ChannelID.JHPAY);//TODO 银商互联网聚合
		channelMap.put("27", 		Constant.ChannelID.UNIONPAY);//TODO 中行江苏银行卡
		channelMap.put("28", 		Constant.ChannelID.ALIPAY);
		channelMap.put("29", 		Constant.ChannelID.WCPAY);
		channelMap.put("30", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("31", 		Constant.ChannelID.JHPAY);//TODO 农行互联网聚合
		channelMap.put("32", 		Constant.ChannelID.ALIPAY);
		channelMap.put("33", 		Constant.ChannelID.WCPAY);
		channelMap.put("34", 		Constant.ChannelID.JHPAY);//TODO DBS-BankCard
		channelMap.put("35", 		Constant.ChannelID.JHPAY);//TODO 银联聚合支付
		channelMap.put("36", 		Constant.ChannelID.UNIONPAY);//TODO 农商行江苏银行卡
		channelMap.put("37", 		Constant.ChannelID.ALIPAY);
		channelMap.put("38", 		Constant.ChannelID.WCPAY);
		channelMap.put("39", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("40", 		Constant.ChannelID.JHPAY);//TODO KiplePay-Qrcode
		channelMap.put("41", 		Constant.ChannelID.WCPAY);
		channelMap.put("42", 		Constant.ChannelID.UNIONPAY);//TODO 交行POS银行卡
		channelMap.put("43", 		Constant.ChannelID.WCPAY);
		channelMap.put("44", 		Constant.ChannelID.ALIPAY);
		channelMap.put("45", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("46", 		Constant.ChannelID.JHPAY);//TODO 交行POS-QQ
		channelMap.put("47", 		Constant.ChannelID.UNIONPAY);//TODO 农行POS银行卡
		channelMap.put("48", 		Constant.ChannelID.WCPAY);
		channelMap.put("49", 		Constant.ChannelID.ALIPAY);
		channelMap.put("50", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("51", 		Constant.ChannelID.JHPAY);//TODO  农行POS-QQ
		channelMap.put("52", 		Constant.ChannelID.UNIONPAY);//TODO 建行POS银行卡
		channelMap.put("53", 		Constant.ChannelID.WCPAY);
		channelMap.put("54", 		Constant.ChannelID.ALIPAY);
		channelMap.put("55", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("56", 		Constant.ChannelID.JHPAY);//TODO 建行POS-QQ
		channelMap.put("61", 		Constant.ChannelID.UNIONPAY);//TODO 中行POS银行卡
		channelMap.put("62", 		Constant.ChannelID.ALIPAY);
		channelMap.put("63", 		Constant.ChannelID.WCPAY);
		channelMap.put("64", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("65", 		Constant.ChannelID.JHPAY);//TODO 中行POS-QQ
		channelMap.put("66", 		Constant.ChannelID.UNIONPAY);//TODO 工行POS银行卡
		channelMap.put("67", 		Constant.ChannelID.WCPAY);
		channelMap.put("68", 		Constant.ChannelID.ALIPAY);
		channelMap.put("69", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("70", 		Constant.ChannelID.JHPAY);//TODO 工行POS-QQ
		channelMap.put("71", 		Constant.ChannelID.JHPAY);//TODO 工行POS聚合
		channelMap.put("72", 		Constant.ChannelID.JHPAY);//TODO 慧金宝
		channelMap.put("73", 		Constant.ChannelID.UNIONPAY);
		channelMap.put("74", 		Constant.ChannelID.UNIONPAY);//TODO 建行POS龙支付
		channelMap.put("75", 		Constant.ChannelID.UNIONPAY);//TODO 华润预付卡
		channelMap.put("76", 		Constant.ChannelID.UNIONPAY);//杉徳银联二维码
		channelMap.put("77", 		Constant.ChannelID.UNIONPAY);//TODO 富友汇丰银行卡
		channelMap.put("78", 		Constant.ChannelID.ALIPAY);//富友支付宝
		channelMap.put("79", 		Constant.ChannelID.WCPAY);//富友微信
		channelMap.put("80", 		Constant.ChannelID.JHPAY);//TODO 中行互联网聚合
		channelMap.put("81", 		Constant.ChannelID.ALIPAY);//富友服务商支付宝
		channelMap.put("82", 		Constant.ChannelID.WCPAY);//富友服务商微信
		channelMap.put("83", 		Constant.ChannelID.UNIONPAY);//富友服务商银联二维码
		channelMap.put("92", 		Constant.ChannelID.UNIONPAY);//贵州农信聚合付
		channelMap.put("93", 		Constant.ChannelID.UNIONPAY);//敏付聚合付
		channelMap.put("BDPAY", 		Constant.ChannelID.JHPAY);//TODO 富基-直连-百度钱包
		channelMap.put("BESTPAY", 		Constant.ChannelID.JHPAY);//TODO 富基-直连-翼支付-直连
		channelMap.put("ICBC", 		Constant.ChannelID.UNIONPAY);//TODO 工行PatT银行卡
		channelMap.put("IPS", 			Constant.ChannelID.UNIONPAY);//TODO 环迅银行卡
		channelMap.put("JDPAY", 		Constant.ChannelID.JHPAY);//TODO 富基-直连-京东钱包-直连
		channelMap.put("JSABC", 		Constant.ChannelID.UNIONPAY);//TODO 农行江苏银行卡
		channelMap.put("MIAOJIE", 		Constant.ChannelID.JHPAY);//TODO 富基-直连-喵街-直连
		channelMap.put("ogs1", 		Constant.ChannelID.JHPAY);//TODO OGS点餐
		channelMap.put("ogs2", 		Constant.ChannelID.JHPAY);//TODO OGS充值
		channelMap.put("SNPAY", 		Constant.ChannelID.JHPAY);//TODO 富基-直连-易付宝-直连
		channelMap.put("TENPAY", 		Constant.ChannelID.JHPAY);//TODO 富基-直连-QQ钱包-直连

		return channelMap;
	};*/

	/*参照平台返回渠道号及子渠道号，映射出【支付宝】，【微信】，【云闪付】三种渠道类型
	  否则返回【聚合扫码】交易渠道类型
	*/
/*	public static String getSoftposChannelId(String channelId, String brachChannelId)
	{
		String tmpChannelId = "";
		if(brachChannelId!=null && !"".equals(brachChannelId)) {
			tmpChannelId = getBrachChannelMap().get(brachChannelId);
		}
		if(tmpChannelId==null || "".equals(tmpChannelId)){
			tmpChannelId = getChannelMap().get(channelId);
		}
		if(tmpChannelId==null || "".equals(tmpChannelId)) {
			tmpChannelId = Constant.ChannelID.JHPAY;
		}
		return tmpChannelId;
	}*/

	/*参照平台返回渠道号及子渠道号，映射出【支付宝】，【微信】，【云闪付】三种渠道类型名称
	  否则返回【聚合扫码】渠道类型名称
	*/
	/*public static String getSoftposChannelName(String channelId, String brachChannelId)
	{
		String tmpChannelName = "";
		if(brachChannelId!=null && !"".equals(brachChannelId)) {
			tmpChannelName = getBrachChannelNameMap().get(brachChannelId);
		}
		if(tmpChannelName==null || "".equals(tmpChannelName)){
			tmpChannelName = getChannelNameMap().get(channelId);
		}
		if(tmpChannelName==null || "".equals(tmpChannelName)) {
			tmpChannelName = "聚合支付";
		}
		return tmpChannelName;
	}*/

	public static String getSoftposChannelName(String accType)
	{
		String tmpChannelName = "";
		if(TextUtils.isEmpty(accType)) {
			tmpChannelName = "聚合支付";
		}else{
			tmpChannelName = getAccTypeNameMap().get(accType);
		}

		return tmpChannelName;
	}
}
