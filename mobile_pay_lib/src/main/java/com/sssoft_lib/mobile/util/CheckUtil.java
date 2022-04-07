package com.sssoft_lib.mobile.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.sssoft_lib.mobile.activity.R;

import org.json.sssoft.JSONObject;

import java.util.regex.Pattern;

public class CheckUtil {
/*			1 Alipay/支付宝 支付宝 ^((25)|(26)|(27)|(28)|(29)|(30))\d{14,22}+
			2 WeChat/微信 微信 ^((10)|(11)|(12)|(13)|(14)|(15))\d{16}
			15 UnionPayQRCode/银联二维码 银联二维码 ^((6))\d{10,30}+
			21 Elem/饿了么企餐 饿了么企餐 ^((EBU)).+
			23 DIGICCY/数字货币 数字货币 ^((01)).+  */
    public static final String  ALI = "^((25)|(26)|(27)|(28)|(29)|(30))\\d{14,22}+";
    public static final String  WeChant = "^((10)|(11)|(12)|(13)|(14)|(15))\\d{16}";
    public static final String  DIGICCY = "^((01)).+";

	public static boolean checkIsMatch(String payMode, String qrCode){
		boolean isMatch = false;
		Pattern r = null;
		if(TextUtils.isEmpty(payMode)){
		    isMatch = true;
		}else{
			switch (payMode){
				case Constant.PayMode.ALIPAY_SHIJI:
					r = Pattern.compile(ALI);
					isMatch = r.matcher(qrCode).matches();
					break;
				case Constant.PayMode.WCPAY_SHIJI:
					r = Pattern.compile(WeChant);
					isMatch = r.matcher(qrCode).matches();
					break;
				case Constant.PayMode.QRPAY_SHIJI:
					isMatch = true;
					break;
				case Constant.PayMode.DIGITAL_CASH:
					r = Pattern.compile(DIGICCY);
					isMatch = r.matcher(qrCode).matches();
					break;
			}
		}

		return isMatch;
	}

	public static String chkSale (Context context , Uri uri){
		String smType = uri.getQueryParameter("SmType");
		String merchantTxnNo = uri.getQueryParameter("MerchantTxnNo");
		String txnAmt = uri.getQueryParameter("TxnAmt");
		String currencyCode = uri.getQueryParameter("CurrencyCode");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String cashierID = uri.getQueryParameter("CashierID");
		String permitDisctAmt = uri.getQueryParameter("PermitDisctAmt");
		String txnLongDesc = uri.getQueryParameter("TxnLongDesc");
		String txnShortDesc = uri.getQueryParameter("TxnShortDesc");
		String channelID = uri.getQueryParameter("ChannelID");
		if(merchantTxnNo==null || merchantTxnNo.equals(""))
			return context.getString(R.string.mobile_merchantTxnNo_empty);
		else if(merchantTxnNo.length()>64)
			return context.getString(R.string.mobile_merchantTxnNo_length);
		
		String patm = "^((\\d{1,3}(,\\d{3})*?)|\\d+)(\\.\\d{2})$";
		if(txnAmt==null || txnAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(txnAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);
		
		if(currencyCode==null || currencyCode.equals(""))
			return context.getString(R.string.mobile_currencyCode_empty);
		else if(currencyCode.length()>3)
			return context.getString(R.string.mobile_currencyCode_length);

		
		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);
		/*
		if(smType!=null && smType.equals("1")){
			if(channelID==null || channelID.equals(""))
				return context.getString(R.string.mobile_channelID_empty);
			else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
					!channelID.equals(Constant.ChannelID.WCPAY)&&
					!channelID.equals(Constant.ChannelID.JHPAY))
				return context.getString(R.string.mobile_channelID_err);
		}
		*/
		if(permitDisctAmt!=null && !permitDisctAmt.equals("") && !Pattern.compile(patm).matcher(permitDisctAmt).matches())
			return context.getString(R.string.mobile_permitDisctAmt_formate);
		
		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);
		
		if(txnLongDesc!=null && !txnLongDesc.equals("") && txnLongDesc.length()>400)
			return context.getString(R.string.mobile_txnLongDesc_length);
		
		if(txnShortDesc!=null && !txnShortDesc.equals("") && txnShortDesc.length()>256)
			return context.getString(R.string.mobile_txnShortDesc_length);
		return "";
	}
	public static String chkCancel (Context context , Uri uri){
		String orgPlatformTxnNo = uri.getQueryParameter("OrgPlatformTxnNo");
		String orgTxnNo = uri.getQueryParameter("OrgTxnNo");
		String refundAmt = uri.getQueryParameter("RefundAmt");
		String currencyCode = uri.getQueryParameter("CurrencyCode");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String cashierID = uri.getQueryParameter("CashierID");
		String channelID = uri.getQueryParameter("ChannelID");
		if(TextUtils.isEmpty(orgPlatformTxnNo) && TextUtils.isEmpty(orgTxnNo))
			return context.getString(R.string.mobile_orgPlatformTxnNo_empty);

		String patm = "^((\\d{1,3}(,\\d{3})*?)|\\d+)(\\.\\d{2})$";
		if(refundAmt==null || refundAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(refundAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);
		
		if(currencyCode==null || currencyCode.equals(""))
			return context.getString(R.string.mobile_currencyCode_empty);
		else if(currencyCode.length()>3)
			return context.getString(R.string.mobile_currencyCode_length);
		
		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);
		
		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);
		
		/*if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/
		
		return "";
	}
	public static String chkRefund(Context context , Uri uri){
		String orgPlatformTxnNo = uri.getQueryParameter("OrgPlatformTxnNo");
		String orgTxnNo = uri.getQueryParameter("OrgTxnNo");
		String orgMerchantID = uri.getQueryParameter("OrgMerchantID");
		String refundTxnNo = uri.getQueryParameter("RefundTxnNo");
		String txnAmt = uri.getQueryParameter("RefundAmt");
		String currencyCode = uri.getQueryParameter("CurrencyCode");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String cashierID = uri.getQueryParameter("CashierID");
		String txnLongDesc = uri.getQueryParameter("TxnLongDesc");
		String txnShortDesc = uri.getQueryParameter("TxnShortDesc");
		String channelID = uri.getQueryParameter("ChannelID");
		String refundReason = uri.getQueryParameter("RefundReason");
		if(refundTxnNo==null || refundTxnNo.equals(""))
			return context.getString(R.string.mobile_refundTxnNo_empty);
		else if(refundTxnNo.length()>64)
			return context.getString(R.string.mobile_refundTxnNo_length);

		if(TextUtils.isEmpty(orgPlatformTxnNo) && TextUtils.isEmpty(orgTxnNo))
			return context.getString(R.string.mobile_orgPlatformTxnNo_empty);

		if(orgMerchantID!=null && !orgMerchantID.equals("") && orgMerchantID.length()>20)
		return context.getString(R.string.mobile_orgMerchantID_length);
		
		String patm = "^((\\d{1,3}(,\\d{3})*?)|\\d+)(\\.\\d{2})$";
		if(txnAmt==null || txnAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(txnAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);
		
		if(currencyCode==null || currencyCode.equals(""))
			return context.getString(R.string.mobile_currencyCode_empty);
		else if(currencyCode.length()>3)
			return context.getString(R.string.mobile_currencyCode_length);
		
		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);
		
		/*if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/
		
		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);
		
		if(txnLongDesc!=null && !txnLongDesc.equals("") && txnLongDesc.length()>400)
			return context.getString(R.string.mobile_txnLongDesc_length);
		
		if(txnShortDesc!=null && !txnShortDesc.equals("") && txnShortDesc.length()>256)
			return context.getString(R.string.mobile_txnShortDesc_length);
		
		if(refundReason!=null && !refundReason.equals("") && refundReason.length()>100)
			return context.getString(R.string.mobile_refundReason_length);
		
		return "";
	}
	public static String chkQuery(Context context , Uri uri){
		String orgPlatformTxnNo = uri.getQueryParameter("OrgPlatformTxnNo");
		String orgTxnNo = uri.getQueryParameter("OrgTxnNo");
		String txnAmt = uri.getQueryParameter("TxnAmt");
		String currencyCode = uri.getQueryParameter("CurrencyCode");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String channelID = uri.getQueryParameter("ChannelID");
		String queryType = uri.getQueryParameter("QueryType");
		String cashierID = uri.getQueryParameter("CashierID");
		
		if((orgPlatformTxnNo==null || orgPlatformTxnNo.equals("")) && (orgTxnNo==null || orgTxnNo.equals(""))){
			return context.getString(R.string.mobile_orgTxnNo_empty);
		}else{
			if(orgPlatformTxnNo!=null && !orgPlatformTxnNo.equals("") && orgPlatformTxnNo.length()>20)
				return context.getString(R.string.mobile_orgPlatformTxnNo_length);
			if(orgTxnNo!=null && !orgTxnNo.equals("") && orgTxnNo.length()>64)
				return context.getString(R.string.mobile_orgTxnNo_length);
		}
		
		String patm = "^((\\d{1,3}(,\\d{3})*?)|\\d+)(\\.\\d{2})$";
		if(txnAmt==null || txnAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(txnAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);
		
		if(currencyCode==null || currencyCode.equals(""))
			return context.getString(R.string.mobile_currencyCode_empty);
		else if(currencyCode.length()>3)
			return context.getString(R.string.mobile_currencyCode_length);
		
		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);
		
		/*if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/
		
		if(queryType==null || queryType.equals(""))
			return context.getString(R.string.mobile_queryType_empty);
		else if(!queryType.equals(Constant.QueryType.SALE)
					&& !queryType.equals(Constant.QueryType.REFUND)
					&& !queryType.equals(Constant.QueryType.QUERY_RESULT_AUTH_COMP)
					&& !queryType.equals(Constant.QueryType.QUERY_RESULT_CANCEL))
			return context.getString(R.string.mobile_queryType_err);
		
		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);
		
		return "";
	}

	public static String chkAuth (Context context , Uri uri){
		String merchantTxnNo = uri.getQueryParameter("MerchantTxnNo");
		String txnAmt = uri.getQueryParameter("TxnAmt");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String cashierID = uri.getQueryParameter("CashierID");
		String txnShortDesc = uri.getQueryParameter("TxnShortDesc");
		String channelID = uri.getQueryParameter("ChannelID");
		String addInfo = uri.getQueryParameter("AddInfo");
		if(merchantTxnNo==null || merchantTxnNo.equals(""))
			return context.getString(R.string.mobile_merchantTxnNo_empty);
		else if(merchantTxnNo.length()>64)
			return context.getString(R.string.mobile_merchantTxnNo_length);

		String patm = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
		if(txnAmt==null || txnAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(txnAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);

		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);

		/*if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/

		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);

		try {
			if(txnShortDesc!=null && !txnShortDesc.equals("") && txnShortDesc.getBytes("GBK").length>256)
				return context.getString(R.string.mobile_txnShortDesc_length);
		} catch (Exception e) {
			return "length err";
		}
		if(addInfo!=null && !addInfo.equals("")){
			try {
				if(addInfo.getBytes("GBK").length>500){
					return context.getString(R.string.mobile_addInfo_length);
				}else{
					new JSONObject(addInfo);
				}
			} catch (Exception e) {
				return context.getString(R.string.mobile_addInfo_err);
			}
		}
		return "";
	}
	public static String chkAuthCancel (Context context , Uri uri){
		String cancelTxnNo = uri.getQueryParameter("CancelTxnNo");
		String orgTxnNo = uri.getQueryParameter("OrgTxnNo");
		//String orgPlatformTxnNo = uri.getQueryParameter("OrgPlatformTxnNo");
		String refundAmt = uri.getQueryParameter("RefundAmt");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String cashierID = uri.getQueryParameter("CashierID");
		String channelID = uri.getQueryParameter("ChannelID");
		String addInfo = uri.getQueryParameter("AddInfo");
		String txnShortDesc = uri.getQueryParameter("TxnShortDesc");
		if(cancelTxnNo==null || cancelTxnNo.equals(""))
			return context.getString(R.string.mobile_merchantTxnNo_empty);
		else if(cancelTxnNo.length()>64)
			return context.getString(R.string.mobile_merchantTxnNo_length);

		/*if(orgPlatformTxnNo==null || orgPlatformTxnNo.equals(""))
			return context.getString(R.string.mobile_orgPlatformTxnNo_empty);
		else if(orgPlatformTxnNo.length()>20)
			return context.getString(R.string.mobile_orgPlatformTxnNo_length);*/

		if(orgTxnNo==null || orgTxnNo.equals(""))
			return context.getString(R.string.mobile_orgTxnNo_empty);
		else if(orgTxnNo.length()>64)
			return context.getString(R.string.mobile_orgTxnNo_length);

		String patm = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
		if(refundAmt==null || refundAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(refundAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);

		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);

		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);

	/*	if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/

		if(addInfo!=null && !addInfo.equals("")){
			try {
				if(addInfo.getBytes("GBK").length>500){
					return context.getString(R.string.mobile_addInfo_length);
				}else{
					new JSONObject(addInfo);
				}
			} catch (Exception e) {
				return context.getString(R.string.mobile_addInfo_err);
			}
		}
		try {
			if(txnShortDesc!=null && !txnShortDesc.equals("") && txnShortDesc.getBytes("GBK").length>256)
				return context.getString(R.string.mobile_txnShortDesc_length);
		} catch (Exception e) {
			return "length err";
		}
		return "";
	}
	public static String chkAuthThaw (Context context , Uri uri){
		String merchantTxnNo = uri.getQueryParameter("CancelTxnNo");
		String txnAmt = uri.getQueryParameter("RefundAmt");
		String authNo = uri.getQueryParameter("AuthNo");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String cashierID = uri.getQueryParameter("CashierID");
		String txnShortDesc = uri.getQueryParameter("TxnShortDesc");
		String channelID = uri.getQueryParameter("ChannelID");
		String addInfo = uri.getQueryParameter("AddInfo");
		if(merchantTxnNo==null || merchantTxnNo.equals(""))
			return context.getString(R.string.mobile_merchantTxnNo_empty);
		else if(merchantTxnNo.length()>64)
			return context.getString(R.string.mobile_merchantTxnNo_length);

		String patm = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";
		if(txnAmt==null || txnAmt.equals(""))
			return context.getString(R.string.mobile_txnAmt_empty);
		else if(!Pattern.compile(patm).matcher(txnAmt).matches())
			return context.getString(R.string.mobile_txnAmt_formate);

		if(authNo==null || authNo.equals(""))
			return context.getString(R.string.mobile_authNo_empty);
		else if(authNo.length()>128)
			return context.getString(R.string.mobile_authNo_length);

		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);

		/*if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/

		if(cashierID!=null && !cashierID.equals("") && cashierID.length()>20)
			return context.getString(R.string.mobile_cashierID_length);

		try {
			if(txnShortDesc!=null && !txnShortDesc.equals("") && txnShortDesc.getBytes("GBK").length>256)
				return context.getString(R.string.mobile_txnShortDesc_length);
		} catch (Exception e) {
			return "length err";
		}
		if(addInfo!=null && !addInfo.equals("")){
			try {
				if(addInfo.getBytes("GBK").length>500){
					return context.getString(R.string.mobile_addInfo_length);
				}else{
					new JSONObject(addInfo);
				}
			} catch (Exception e) {
				return context.getString(R.string.mobile_addInfo_err);
			}
		}
		return "";
	}
	public static String chkAuthQuery(Context context , Uri uri){
		String orgTxnNo = uri.getQueryParameter("OrgTxnNo");
		String txnReqTime = uri.getQueryParameter("TxnReqTime");
		String channelID = uri.getQueryParameter("ChannelID");
		String opType = uri.getQueryParameter("OpType");
		if(orgTxnNo==null || orgTxnNo.equals(""))
			return context.getString(R.string.mobile_orgTxnNo_empty);
		else if(orgTxnNo.length()>64)
			return context.getString(R.string.mobile_orgTxnNo_length);

		if(txnReqTime==null || txnReqTime.equals(""))
			return context.getString(R.string.mobile_txnReqTime_empty);
		else if(!DateUtil.isDateTime(txnReqTime))
			return context.getString(R.string.mobile_txnReqTime_formate);

		/*if(channelID==null || channelID.equals(""))
			return context.getString(R.string.mobile_channelID_empty);
		else if(!channelID.equals(Constant.ChannelID.ALIPAY) &&
				!channelID.equals(Constant.ChannelID.WCPAY)&&
				!channelID.equals(Constant.ChannelID.JHPAY))
			return context.getString(R.string.mobile_channelID_err);*/

		if(opType==null || opType.equals(""))
			return context.getString(R.string.mobile_opType_empty);
		else if(!opType.equals(Constant.OpType.Freeze) && !opType.equals(Constant.OpType.Unfreeze))
			return context.getString(R.string.mobile_opType_err);

		return "";
	}
}

