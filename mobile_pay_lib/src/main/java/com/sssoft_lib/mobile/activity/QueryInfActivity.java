package com.sssoft_lib.mobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.sssoft_lib.mobile.object.RequestObj;
import com.sssoft_lib.mobile.util.CheckUtil;
import com.sssoft_lib.mobile.util.Constant;
import com.sssoft_lib.mobile.util.FormatUtil;
import com.sssoft_lib.mobile.util.HttpProxy;
import com.sssoft_lib.mobile.util.MobileUtil;
import com.sssoft_lib.mobile.util.SignVerifyUtil;
import com.sssoft_lib.mobile.view.SuccinctProgress;

import org.apache.log4j.Logger;
import org.json.sssoft.JSONObject;

public class QueryInfActivity extends BaseActivity {
	
	private static Logger log = Logger.getLogger(QueryInfActivity.class);
	private SharedPreferences preferences;
	private static RequestObj requestObj = new RequestObj();
	private JSONObject backJson;
	private Intent backIntent = new Intent();
	private static Context mContext;
	private String mid = "";
	private String tid = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initPreferences();
        MobileUtil.setLanguage(mContext, preferences);
		setContentView(R.layout.activity_inf);
        TextView view = (TextView) findViewById(android.R.id.title);
        view.setGravity(Gravity.CENTER);

        //ServiceUtil.startServiceConnect(mContext);      
		Intent intent = getIntent();
		if(null!=intent){
			Uri uri = intent.getData();
			if(uri == null){
				return;
			}
			Log.e("QueryInfActivity", "uri="+uri.toString());
			String rc = CheckUtil.chkQuery(mContext, uri);
			if(rc.equals("")){
				requestObj.setOrgPlatformTxnNo(uri.getQueryParameter("OrgPlatformTxnNo")==null?"":uri.getQueryParameter("OrgPlatformTxnNo"));
				requestObj.setTxnAmt(uri.getQueryParameter("TxnAmt")==null?"":uri.getQueryParameter("TxnAmt"));
				requestObj.setOrgTxnNo(uri.getQueryParameter("OrgTxnNo")==null?"":uri.getQueryParameter("OrgTxnNo"));
				requestObj.setCurrencyCode(uri.getQueryParameter("CurrencyCode")==null?"":uri.getQueryParameter("CurrencyCode"));
				requestObj.setTxnReqTime(uri.getQueryParameter("TxnReqTime")==null?"":uri.getQueryParameter("TxnReqTime"));
				requestObj.setChannelID(uri.getQueryParameter("ChannelID")==null?"":uri.getQueryParameter("ChannelID"));
				requestObj.setQueryType(uri.getQueryParameter("QueryType")==null?"":uri.getQueryParameter("QueryType"));
				requestObj.setCashierID(uri.getQueryParameter("CashierID")==null?"":uri.getQueryParameter("CashierID"));
				SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_QImessage1), false, false);
				new Thread(runnable).start();
			}else{
				backIntent(Constant.FAILE, rc);
			}
		}
	} 
	private void initPreferences(){
		preferences = getSharedPreferences(Constant.APPSTR,MODE_ENABLE_WRITE_AHEAD_LOGGING | MODE_WORLD_WRITEABLE | MODE_MULTI_PROCESS);
        mid = preferences.getString("mid", "");
        tid = preferences.getString("tid", "");
        if(mid.equals("")){
        	backIntent(Constant.FAILE, mContext.getString(R.string.mobile_CAmessage2));
        }
	}
	@Override 
	public void onDestroy() { 
		 SuccinctProgress.dismiss();
//		 if(ServiceUtil.getXposDriverService()!=null)
//			 unbindService(ServiceUtil.mserviceConnection);
		 super.onDestroy();
	} 
	public void backIntent(String Rc,String message){
    	Intent intent = new Intent();
		intent.putExtra("RespCode", Rc);
		intent.putExtra("RespDesc", message);
		setResult(RESULT_OK, intent);
		finish();
    }
	public void backSuccIntent(){
    	SuccinctProgress.dismiss();
    	backIntent.putExtra("TxnAnsTime", backJson.getString("TxnAnsTime"));
     	backIntent.putExtra("PlatformTxnNo", backJson.getString("PlatformTxnNo"));
     	backIntent.putExtra("ChannelTxnNo", backJson.getString("ChannelTxnNo"));
     	backIntent.putExtra("TotalAmt",  FormatUtil.str2DoubleAmt(requestObj.getTxnAmt()));
     	backIntent.putExtra("MerchantName", backJson.getString("MerchantName"));
     	backIntent.putExtra("PayerID", backJson.getString("PayerID"));
//     	backIntent.putExtra("ChannelID", MobileUtil.getSoftposChannelId(backJson.getString("ChannelID"), backJson.getString("BranchChannelID")));
		backIntent.putExtra("ChannelID", backJson.getString("ChannelID"));
		backIntent.putExtra("ChannelIDName", MobileUtil.getSoftposChannelName(backJson.getString("AccType")));
		backIntent.putExtra("MerchantID", mid);
      	backIntent.putExtra("TerminalID", tid);
     	backIntent.putExtra("ExchangeRate", backJson.getDouble("ExchangeRate"));
		backIntent.putExtra("AccType", backJson.getString("AccType"));
     	if(requestObj.getQueryType().equals(Constant.QueryType.SALE)){
	     	backIntent.putExtra("IncomeAmt", backJson.getDouble("IncomeAmt"));
	     	backIntent.putExtra("InvoiceAmt", backJson.getDouble("InvoiceAmt"));
	     	backIntent.putExtra("PointAmt", backJson.getDouble("PointAmt"));
	     	backIntent.putExtra("MerchantDisctAmt", backJson.getDouble("MerchantDisctAmt"));
	     	backIntent.putExtra("ChannelDisctAmt", backJson.getDouble("ChannelDisctAmt"));
	     	backIntent.putExtra("MerchantRechargeAmt", backJson.getDouble("MerchantRechargeAmt"));
	     	backIntent.putExtra("BuyerPayedAmt", backJson.getDouble("BuyerPayedAmt"));
	     	backIntent.putExtra("BuyerPayedCurrency", backJson.getString("BuyerPayedCurrency"));
     	}
     	if(requestObj.getQueryType().equals(Constant.QueryType.REFUND)){
	     	backIntent.putExtra("ChannelCurrency", backJson.getString("ChannelCurrency"));
	     	backIntent.putExtra("ChannelAmt", backJson.getDouble("ChannelAmt"));
     	}
		backIntent.putExtra("PrintInfo", backJson.getString("PrintInfo"));
		setResult(Activity.RESULT_OK,backIntent);
		finish();
    }
	Handler handler = new Handler(){
	    @SuppressLint("NewApi")
		@Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        String val = data.getString("respContent");
	        log.info("交易查询返回信息："+val);
	        if(!val.contains("RespCode")){
	        	backIntent(Constant.FAILE, val);
	        }else{
	        	backJson = new JSONObject(val);
		        String RespCode = backJson.getString("RespCode");
		        backIntent.putExtra("RespCode", RespCode);
		        backIntent.putExtra("RespDesc", backJson.getString("RespDesc"));
		        if(Constant.SUCC.equals(RespCode)){
//		        	requestObj.setPrintFlag("1");
//		        	setPrintText(backJson);
					backJson.put("PrintInfo", writePrintText(backJson));
		        	backSuccIntent();
		        }else{
		        	setResult(Activity.RESULT_OK,backIntent);
		    		finish();
		        }
	        }
	    }
	};
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	    	 String  respContent = query();
	    	 Message msg = new Message();
		     Bundle data = new Bundle();
		     data.putString("respContent",respContent);
		     msg.setData(data);
		     handler.sendMessage(msg);
	    }
	};

	private String getQueryTxnType(String type){
	    String queryType = "";
		if( Constant.QueryType.QUERY_RESULT_AUTH_COMP.equals(requestObj.getQueryType())){
			queryType = Constant.QueryType.SALE ;
		}else if(Constant.QueryType.QUERY_RESULT_CANCEL.equals(requestObj.getQueryType())){
		    queryType = Constant.QueryType.REFUND;
		}else{
		    queryType = requestObj.getQueryType();
		}
		return  queryType;
	}

	public String query(){
    	String respContent = "";
    	try {
    		String queryType = getQueryTxnType(requestObj.getQueryType());
    		JSONObject postData = new JSONObject();
			postData.put("TxnType", queryType);

			postData.put("AccTypeFlag", "1");
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
			if (!"".equals(requestObj.getChannelID())) {
				postData.put("ChannelID", requestObj.getChannelID());
			}
    		if(requestObj.getOrgPlatformTxnNo()!=null && !requestObj.getOrgPlatformTxnNo().equals(""))
    			postData.put("OrgPlatformTxnNo", requestObj.getOrgPlatformTxnNo());

    		if(queryType.equals(Constant.QueryType.SALE) ){
    			postData.put("TxnAmt", requestObj.getTxnAmt());
    			if(requestObj.getOrgTxnNo()!=null && !requestObj.getOrgTxnNo().equals(""))
        			postData.put("OrgTxnNo", requestObj.getOrgTxnNo());
    		}

    		if(queryType.equals(Constant.QueryType.REFUND)){
    			postData.put("RefundAmt", requestObj.getTxnAmt());
    			if(requestObj.getOrgTxnNo()!=null && !requestObj.getOrgTxnNo().equals(""))
        			postData.put("OrgRefundTxnNo", requestObj.getOrgTxnNo());
    		}
    		postData.put("IMEI", Build.SERIAL);
    		postData.put("CashierID", requestObj.getCashierID());
    		postData.put("CurrencyCode", requestObj.getCurrencyCode());
    		postData.put("TxnReqTime", requestObj.getTxnReqTime());
			postData.put("MerchantID", mid);
			postData.put("TerminalID", tid);
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		log.info("查询交易信息："+postData.toString());
			respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("查询交易报错："+e1.getLocalizedMessage());
	        respContent = mContext.getString(R.string.mobile_CAmessage3);
	    }
		return respContent;
	}
  	@Override
	public void onAttachedToWindow() {
	     this.getWindow().addFlags(5);
	     this.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	     super.onAttachedToWindow();
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK  || keyCode == KeyEvent.KEYCODE_HOME)//返回键监听
		{
			return true;
        }
         return super.onKeyDown(keyCode, event);
		
	}

	/**
	 * 查询结果类型
	 */
	public class QueryResultType {
		public static final String QUERY_RESULT_SALE="5"; // 消费结果查询
		public static final String QUERY_RESULT_REFUND="6"; // 退货结果查询
		public static final String QUERY_RESULT_CANCEL="30"; // 撤销结果查询
		public static final String QUERY_RESULT_AUTH_COMP="33"; // 预授权完成结果查询

	}
	private String getOrgTxnType(String queryType){
		String txnType = "";
		if(QueryResultType.QUERY_RESULT_SALE.equals(queryType)) {
			txnType = Constant.TxnType.SALE;
		} else if(QueryResultType.QUERY_RESULT_CANCEL.equals(queryType)) {
			txnType = Constant.TxnType.CANNEL;
		} else if(QueryResultType.QUERY_RESULT_REFUND.equals(queryType)) {
			txnType = Constant.TxnType.REFUND;
		}else if(QueryResultType.QUERY_RESULT_AUTH_COMP.equals(queryType)) {
			txnType = Constant.TxnType.AUTHPAY;
		}else {
			txnType = Constant.TxnType.SALE;
		}
		return txnType;
	}
	public String writePrintText(JSONObject json){
		String formatSCStart = "<center><small>";
		String formatSCEnd = "</small></center>";

		String formatNCStart = "<center><normal>";
		String formatNCEnd = "</normal></center>";

		String formatNLStart = "<left><normal>";
		String formatNLEnd = "</normal></left>";

		String formatNRStart = "<right><normal>";
		String formatNREnd = "</normal></right>";

		String formatLCStart = "<center><large>";
		String formatLCEnd = "</large></center>";

		String formatLLStart = "<left><large>";
		String formatLLEnd = "</large></left>";

		String formatNCQRStart = "<center><normal><qrCode>";
		String formatNCQREnd = "</qrCode></normal></center>";

		String formatLCQRStart = "<center><large><qrCode>";
		String formatLCQREnd = "</qrCode></large></center>";

		String formatNCBRStart = "<center><normal><barCode>";
		String formatNCBREnd = "</barCode></normal></center>";

		String formatLCBRStart = "<center><large><barCode>";
		String formatLCBREnd = "</barCode></large></center>";

		String formatEnter = "<br>";
		String formatCutpaper = "<cut>";

		StringBuilder stringBuilder = new StringBuilder();

		for (int i=0; i<2; i++) {
			if (i==0) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr2)).append(formatLCEnd).append(formatEnter);
			} else {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr3)).append(formatLCEnd).append(formatEnter);
			}
			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);
			String merName = json.optString("MerchantName");
			if (!"".equals(merName)) {
				stringBuilder.append(formatNCStart).append(merName).append(formatNCEnd).append(formatEnter);
				preferences.edit().putString("MerchantName", merName).commit();
			} else {
				stringBuilder.append(formatNCStart).append(preferences.getString("MerchantName","未知特约商户")).append(formatNCEnd).append(formatEnter);
			}
			stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr21)).append(formatLCEnd).append(formatEnter);
			if(Constant.TxnType.SALE.equals(getOrgTxnType(requestObj.getQueryType()))) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr17) + ":" + FormatUtil.double2StrAmt(new Double(requestObj.getTxnAmt()))).append(formatLCEnd).append(formatEnter);
			} else if(Constant.TxnType.CANNEL.equals(getOrgTxnType(requestObj.getQueryType()))) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr8) + ": -" + FormatUtil.double2StrAmt(new Double(requestObj.getTxnAmt()))).append(formatLCEnd).append(formatEnter);
			} else if(Constant.TxnType.AUTHPAY.equals(getOrgTxnType(requestObj.getQueryType()))) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr23) + ": -" + FormatUtil.double2StrAmt(new Double(requestObj.getTxnAmt()))).append(formatLCEnd).append(formatEnter);
			} else {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr18) + ": -" + FormatUtil.double2StrAmt(new Double(requestObj.getTxnAmt()))).append(formatLCEnd).append(formatEnter);
			}
			stringBuilder.append(formatSCStart).append(mContext.getString(R.string.mobile_tranSucc)).append(formatSCEnd).append(formatEnter);
			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr16) + ":" + json.getString("PayerID")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11) + ":" + MobileUtil.getSoftposChannelName(json.getString("AccType"))).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr10) + ":" + json.getString("TxnAnsTime")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr4) + ":" + mid).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr5) + ":" + tid).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr6) + ":" + requestObj.getCashierID()).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr26) + ":" + json.getString("PlatformTxnNo")).append(formatNLEnd).append(formatEnter);

			if(Constant.TxnType.SALE.equals(getOrgTxnType(requestObj.getQueryType())) || Constant.TxnType.AUTHPAY.equals(getOrgTxnType(requestObj.getQueryType()))) {
				/*if(requestObj.getAuthNo()!=null && !requestObj.getAuthNo().trim().equals("")){
					stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo) + ":" + requestObj.getAuthNo()).append(formatNLEnd).append(formatEnter);
				}*/
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr20) + ":" + json.getString("ChannelTxnNo")).append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatNCStart).append(mContext.getString(R.string.mobile_ptSystrace)).append(formatNCEnd).append(formatEnter);
				stringBuilder.append(formatNCBRStart).append(json.getString("PlatformTxnNo")).append(formatNCBREnd).append(formatEnter);
//				stringBuilder.append(formatNCStart).append(json.getString("PlatformTxnNo")).append(formatNCEnd).append(formatEnter);
			} else {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr20) + ":" + json.getString("ChannelTxnNo")).append(formatNLEnd).append(formatEnter);
				if (!"".equals(json.optString("OrgPlatformTxnNo"))) {
					stringBuilder.append(formatNCStart).append(mContext.getString(R.string.mobile_printStr9)).append(formatNCEnd).append(formatEnter);
					stringBuilder.append(formatNCBRStart).append(json.optString("OrgPlatformTxnNo")).append(formatNCBREnd).append(formatEnter);
//					stringBuilder.append(formatNCStart).append(json.optString("OrgPlatformTxnNo")).append(formatNCEnd).append(formatEnter);
				}
			}

			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);

			if (i == 0) {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr14) + ":").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatCutpaper);
			} else {
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
			}
		}

		log.info("WritePrnFile="+stringBuilder.toString());
		return stringBuilder.toString();
	}
}
