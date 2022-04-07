package com.sssoft_lib.mobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class AuthQueryInfActivity extends BaseActivity {
	
	private static Logger log = Logger.getLogger(AuthQueryInfActivity.class);
	private SharedPreferences preferences;
	private static RequestObj requestObj = new RequestObj();
	private JSONObject backJson;
	private Intent backIntent = new Intent();
	private static Context mContext;
	private static String mid, tid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		//myApp = (MyApplication)getApplication();
        //preferences = myApp.getPreferences();
		initPreferences();
        MobileUtil.setLanguage(mContext, preferences);
		setContentView(R.layout.activity_inf);
        TextView view = (TextView) findViewById(android.R.id.title);
        view.setGravity(Gravity.CENTER);
		Intent intent = getIntent();
		if(null!=intent){
			Uri uri = intent.getData();
			if(uri == null){
				return;
			}
			Log.e("AuthQueryInfActivity", "uri="+uri.toString());
			String rc = CheckUtil.chkAuthQuery(mContext, uri);
			if(rc.equals("")){
				requestObj.setOrgTxnNo(uri.getQueryParameter("OrgTxnNo")==null?"":uri.getQueryParameter("OrgTxnNo"));
				requestObj.setTxnReqTime(uri.getQueryParameter("TxnReqTime")==null?"":uri.getQueryParameter("TxnReqTime"));
				requestObj.setChannelID(uri.getQueryParameter("ChannelID")==null?"":uri.getQueryParameter("ChannelID"));
				requestObj.setCurrencyCode(uri.getQueryParameter("CurrencyCode")==null?"":uri.getQueryParameter("CurrencyCode"));
				requestObj.setOpType(uri.getQueryParameter("OpType")==null?"":uri.getQueryParameter("OpType"));
				requestObj.setQueryType(uri.getQueryParameter("QueryType")==null?"":uri.getQueryParameter("QueryType"));
				requestObj.setCashierID(uri.getQueryParameter("CashierID")==null?"":uri.getQueryParameter("CashierID"));
				SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_QImessage1), false, false);
				new Thread(runnable).start();
			}else{
				backIntent(Constant.FAILE, rc);
			}
		}
	}

	@SuppressLint("WrongConstant")
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
	public void backIntent(String Rc, String message){
    	Intent intent = new Intent();
		intent.putExtra("RespCode", Rc);
		intent.putExtra("RespDesc", message);
		log.info("交易查询返回信息："+Rc+message);
		setResult(RESULT_OK, intent);
		finish();
    }
	public void backSuccIntent(){
    	SuccinctProgress.dismiss();
    	backIntent.putExtra("TxnAnsTime", backJson.getString("TxnAnsTime"));
    	backIntent.putExtra("AuthNo", backJson.getString("AuthNo"));
    	backIntent.putExtra("ChannelTxnNo", backJson.getString("ChannelTxnNo"));
     	backIntent.putExtra("PlatformTxnNo", backJson.getString("PlatformTxnNo"));
     	backIntent.putExtra("TotalAmt", backJson.getDouble("TxnAmt"));
     	backIntent.putExtra("MerchantName", backJson.getString("MerchantName"));
     	backIntent.putExtra("PayerID", backJson.getString("PayerID"));
     	//backIntent.putExtra("ChannelID", requestObj.getChannelID());
//		backIntent.putExtra("ChannelID", MobileUtil.getSoftposChannelId(backJson.getString("ChannelID"), backJson.getString("BranchChannelID")));
		backIntent.putExtra("ChannelID", backJson.getString("ChannelID"));
		backIntent.putExtra("ChannelIDName", MobileUtil.getSoftposChannelName(backJson.getString("AccType")));
		backIntent.putExtra("MerchantID", backJson.getString("MerchantID"));
      	backIntent.putExtra("TerminalID", backJson.getString("TerminalID"));
     	backIntent.putExtra("AddInfo", backJson.getString("AddInfo"));
		backIntent.putExtra("PrintInfo", backJson.getString("PrintInfo"));
		backIntent.putExtra("AccType", backJson.getString("AccType"));
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
	        log.info("预授权查询返回信息："+val);
	        if(!val.contains("RespCode")){
	        	backIntent(Constant.UNK, val);
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
	    	 String respContent = query();
	    	 Message msg = new Message();
		     Bundle data = new Bundle();
		     data.putString("respContent",respContent);
		     msg.setData(data);
		     handler.sendMessage(msg);
	    }
	};
	public String query(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
			postData.put("AccTypeFlag", "1");
    		postData.put("TxnType", Constant.TxnType.AUTHQUERY);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
            postData.put("MerchantID", mid);
            postData.put("TerminalID", tid);
    		postData.put("OpType", requestObj.getOpType());
        	postData.put("OrgTxnNo", requestObj.getOrgTxnNo());
    		postData.put("TxnReqTime", requestObj.getTxnReqTime());
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		log.info("预授权查询交易信息："+postData.toString());
			respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("预授权查询交易信息："+e1.getLocalizedMessage());
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
		public static final String QUERY_RESULT_AUTH = "31";      // 预授权结果查询
		public static final String QUERY_RESULT_VOID_AUTH = "32"; // 预授权撤销结果查询
		//public static final String QUERY_RESULT_AUTH_COMP = "33"; // 预授权完成结果查询
		//public static final String QUERY_RESULT_VOID_COMP = "34"; // 完成撤消结果查询
		//public static final String QUERY_RESULT_ADD_AUTH = "35";  // 追加预授权结果查询
		public static final String QUERY_RESULT_AUTH_THAW = "36"; // 预授权撤销结果查询
	}
	private String getOrgTxnType(String queryType){
		String txnType = "";
		if(QueryResultType.QUERY_RESULT_AUTH.equals(queryType)) {
			txnType = Constant.TxnType.AUTHPAY;
		} else if(QueryResultType.QUERY_RESULT_VOID_AUTH.equals(queryType)) {
			txnType = Constant.TxnType.AUTHCANCEL;
		} else if(QueryResultType.QUERY_RESULT_AUTH_THAW.equals(queryType)) {
			txnType = Constant.TxnType.AUTHTHAW;
		} else {
			txnType = Constant.TxnType.AUTHPAY;
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
		String merName = json.optString("MerchantName");
		for (int i=0; i<2; i++) {
			if (i==0) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr2)).append(formatLCEnd).append(formatEnter);
			} else {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr3)).append(formatLCEnd).append(formatEnter);
			}
			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);

			if (!"".equals(merName)) {
				stringBuilder.append(formatNCStart).append(merName).append(formatNCEnd).append(formatEnter);
				preferences.edit().putString("MerchantName", merName).commit();
			} else {
				stringBuilder.append(formatNCStart).append(preferences.getString("MerchantName","未知特约商户")).append(formatNCEnd).append(formatEnter);
			}
			if(Constant.TxnType.AUTHPAY.equals(getOrgTxnType(requestObj.getQueryType()))) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr22) + ":" + FormatUtil.double2StrAmt(new Double(json.getString("TxnAmt")))).append(formatLCEnd).append(formatEnter);
			} else if(Constant.TxnType.AUTHCANCEL.equals(getOrgTxnType(requestObj.getQueryType()))) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr24) + ": -" + FormatUtil.double2StrAmt(new Double(json.getString("TxnAmt")))).append(formatLCEnd).append(formatEnter);
			} else if(Constant.TxnType.AUTHTHAW.equals(getOrgTxnType(requestObj.getQueryType()))) {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr25) + ": -" + FormatUtil.double2StrAmt(new Double(json.getString("TxnAmt")))).append(formatLCEnd).append(formatEnter);
			} else {
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr21) + ": -" + FormatUtil.double2StrAmt(new Double(json.getString("TxnAmt")))).append(formatLCEnd).append(formatEnter);
			}
			stringBuilder.append(formatSCStart).append(mContext.getString(R.string.mobile_tranSucc)).append(formatSCEnd).append(formatEnter);
			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr16) + ":" + json.getString("PayerID")).append(formatNLEnd).append(formatEnter);
			//stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11) + ":" + MobileUtil.getChannelMap().get(requestObj.getChannelID())).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11) + ":" + MobileUtil.getSoftposChannelName(json.getString("AccType"))).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr10) + ":" + json.getString("TxnAnsTime")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr4) + ":" + mid).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr5) + ":" + tid).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr6) + ":" + requestObj.getCashierID()).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr26) + ":" + json.getString("PlatformTxnNo")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr9) +"(撤销使用):").append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNCQRStart).append(requestObj.getOrgTxnNo()).append(formatNCQREnd).append(formatEnter);
			stringBuilder.append(formatNCStart).append(requestObj.getOrgTxnNo()).append(formatNCEnd).append(formatEnter);

			if(Constant.TxnType.AUTHPAY.equals(getOrgTxnType(requestObj.getQueryType()))) {
				if (!"".equals(json.optString("AuthNo", ""))) {
					//20190415 支付宝交易打印出授权号二维码
					if ("6".equals(json.getString("ChannelID"))) {
						stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo) + "(预授权完成/解冻使用):").append(formatNLEnd).append(formatEnter);
						stringBuilder.append(formatNCQRStart).append(json.getString("AuthNo")).append(formatNCQREnd).append(formatEnter);
					} else {
						stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo) + "(预授权完成使用):").append(formatNLEnd).append(formatEnter);
						stringBuilder.append(formatNCQRStart).append(json.getString("AuthNo")).append(formatNCQREnd).append(formatEnter);
					}
					stringBuilder.append(formatNCStart).append(json.getString("AuthNo")).append(formatNCEnd).append(formatEnter);
				}
			} else {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr9) + ":").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatNCStart).append(requestObj.getOrgTxnNo()).append(formatNCEnd).append(formatEnter);

				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo)+":").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatNCStart).append(json.getString("AuthNo")).append(formatNCEnd).append(formatEnter);
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
