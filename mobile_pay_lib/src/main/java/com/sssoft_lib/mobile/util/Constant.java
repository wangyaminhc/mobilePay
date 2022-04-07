package com.sssoft_lib.mobile.util;

public class Constant {
	public static boolean PROD_MODE=true;
	public static final String SUCC = "00";//交易成功
	public static final String UNK = "X0";//交易未知
	public static final String FAILE = "XX";//交易失败
	
	public static final int RESP01 = 11;//现金交易返回
	public static final int RESP02 = 12;//条码交易返回
	public static final int RESP03 = 13;//收单交易返回
	public static final int RESP04 = 14;//现金交易成功返回
	public static final int RESP05 = 15;//预付卡交易返回
	public static final int RESP06 = 16;//打开蓝牙返回
	public static final int RESP07 = 17;//蓝牙连接返回
	public static final int RESP08 = 18;//蓝牙交易返回
	
	public static final String TXN_TYPE_SALE = "101";//消费交易
	public static final String TXN_TYPE_CHX = "102";//撤销交易
	public static final String TXN_TYPE_REF = "103";//退货交易
	public static final String TXN_TYPE_MAN = "105";//呼出管理程序
	
	public static final int MAIN_RESP01 = 21;//消费交易返回
	public static final int MAIN_RESP02 = 22;//银行卡撤销交易返回
	public static final int MAIN_RESP03 = 23;//银行卡退货交易返回
	public static final int MAIN_RESP04 = 24;//打印返回
	public static final int MAIN_RESP05 = 25;//预付卡退货返回
	
	public final static String DEFAULT_CHARSET = "UTF-8";
	public final static String RC = "txnRespCode";
	public final static String RC_DETAIL = "txnRespDesc";
	public final static String APPSTR = "MoblieApp";
	public class Field{
		public final static String SIGN = "Sign";
		public final static String PARTNER = "PartnerID";
	}
	public class Trans{
		public final static String ServerIP = "58.213.110.146";
		public final static int ServerPort = 19966;
		public final static int TcpTimeout = 60;
	}
	public class OpType{
		public static final String Freeze = "1";
		public static final String Unfreeze = "2";
	}

	public class AccType{
		public static final String ALIPAY = "1";
		public static final String WECHAT = "2";
		public static final String QQ_PAY = "3";
		public static final String BANK   = "4";
		public static final String GIFIT  = "5";
		public static final String BAIDU_PAY = "8"; //百度
		public static final String YI_PAY = "11";  //翼支付
		public static final String JD_PAY = "12";
		public static final String YIFUBAO = "14"; //易付宝
		public static final String UNION   = "15"; //银联二维码
		public static final String MEMBER_CARD  = "20"; //会员卡
		public static final String ELEMA  =  "21"; //饿了么
		public static final String DIGITAL = "23";  //数字货币
	}

	public static final String FileName = "mobile";
	public static final String TRUST_STORE_FILE_NAME = "sj-client.truststore";
	//受信任证书库密码
	public static final String TRUST_STORE_PASSWORD = "888888";
	//APP客户端证书库名称
	public static final String KEY_STORE_FILE_NAME = "appclient.p12";
	//APP客户端证书库密码
	public static final String KEY_STORE_PASSWORD = "888888";
	public class HttpArg{
//		public static final String  POST_URL="https://ss-platform01.shijicloud.com/api-v16";
//		public static final String  PARTNER_ID="1017";
//		public static final String  KEY="t0egmimvup5qppo52idr";
//		public static final String  Constant.PROD_MODE?Constant.HttpArg.PROD_UPDATE_URL:Constant.HttpArg.TEST_UPDATE_URL="https://ss-platform01.shijicloud.com/apk/version-cashier.xml";

		public static final String  PARTNER_ID="1001";
		//测试
		public static final String  TEST_POST_URL="https://ss-platform-test.shijicloud.com/api-v16";
		public static final String  TEST_KEY="1wcyz5wmr6rq23c353xe";
		public static final String  TEST_UPDATE_URL="https://ss-platform-test.shijicloud.com/apk/version-cashier.xml";
		//石基生产
		public static final String  PROD_POST_URL="https://ss-platform01.shijicloud.com/api-v16";
		public static final String  PROD_KEY="nn5nqn2g6tosx68k8gsm";
		public static final String  PROD_UPDATE_URL="https://ss-platform01.shijicloud.com/apk/version-cashier.xml";

	}
	public static final Integer CONN_TIMEOUT = 30;
	public static final Integer SO_TIMEOUT = 30;
	public  class TxnType{
		public static final String PRESALE = "1";
		public static final String SALE = "2";
		public static final String CANNEL = "3";
		public static final String REFUND = "4";
		public static final String SALEQUERY = "5";
		public static final String REFUNDQUERY = "6";
		public static final String PREAUTH = "11";
		public static final String AUTHPAY = "12";
		public static final String AUTHCANCEL = "13";
		public static final String AUTHTHAW = "14";
		public static final String AUTHQUERY = "15";
	}
	public  class QueryType{
		public static final String SALE = "5";
		public static final String REFUND = "6";
		public static final String QUERY_RESULT_CANCEL="30"; // 撤销结果查询
		public static final String QUERY_RESULT_AUTH_COMP="33"; // 预授权完成结果查询
	}



	public class PayMode{
		public static final String ALIPAY_SHIJI  = "10";//石基渠道支付宝
		public static final String WCPAY_SHIJI   = "11";//石基渠道微信
		public static final String DIGITAL_CASH = "13";//数字货币
		public static final String QRPAY_SHIJI   = "15";//石基聚合扫码支付
	}

	public class ChannelID{
		public static final String ALIPAY 	= "6";
		public static final String WCPAY 	= "2";
		public static final String UNIONPAY = "69";//工行云闪付渠道号
		public static final String JHPAY 	= "999";
	}
}
