package com.sssoft_lib.mobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sssoft_lib.mobile.object.RequestObj;
import com.sssoft_lib.mobile.util.CheckUtil;
import com.sssoft_lib.mobile.util.Constant;
import com.sssoft_lib.mobile.util.DateUtil;
import com.sssoft_lib.mobile.util.FormatUtil;
import com.sssoft_lib.mobile.util.HttpProxy;
import com.sssoft_lib.mobile.util.MobileUtil;
import com.sssoft_lib.mobile.util.SignVerifyUtil;
import com.sssoft_lib.mobile.view.SuccinctProgress;

import org.apache.log4j.Logger;
import org.json.sssoft.JSONObject;

import java.util.Date;

public class AuthThawInfActivity extends BaseActivity {
	
	private static Logger log = Logger.getLogger(AuthThawInfActivity.class);
	private SharedPreferences preferences;
	private static RequestObj requestObj = new RequestObj();
	//private MyApplication myApp;
	private JSONObject backJson;
	private Intent backIntent = new Intent();
	private static Context mContext;
	private int queryCount = 0 ;
	public static Context getmContext() {
		return mContext;
	}
	private String mid, tid;
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
        //ServiceUtil.startServiceConnect(mContext);
		Intent intent = getIntent();
		if(null!=intent){
			Uri uri = intent.getData();
			if(uri == null){
				return;
			}
			String rc = CheckUtil.chkAuthThaw(mContext, uri);
			if(rc.equals("")){
				requestObj.setMerchantTxnNo(uri.getQueryParameter("CancelTxnNo")==null?"":uri.getQueryParameter("CancelTxnNo"));
				requestObj.setOrgTxnNo(uri.getQueryParameter("OrgTxnNo")==null?"":uri.getQueryParameter("OrgTxnNo"));
				requestObj.setTxnAmt(uri.getQueryParameter("RefundAmt")==null?"":uri.getQueryParameter("RefundAmt"));
				requestObj.setTxnReqTime(uri.getQueryParameter("TxnReqTime")==null?"":uri.getQueryParameter("TxnReqTime"));
				requestObj.setCashierID(uri.getQueryParameter("CashierID")==null?"":uri.getQueryParameter("CashierID"));
				requestObj.setTxnShortDesc(uri.getQueryParameter("TxnShortDesc")==null?"":uri.getQueryParameter("TxnShortDesc"));
				requestObj.setAddInfo(uri.getQueryParameter("AddInfo")==null?"":uri.getQueryParameter("AddInfo"));
				requestObj.setChannelID(uri.getQueryParameter("ChannelID")==null?"":uri.getQueryParameter("ChannelID"));
				requestObj.setAuthNo(uri.getQueryParameter("AuthNo")==null?"":uri.getQueryParameter("AuthNo"));
				requestObj.setMerchantOrderNo(uri.getQueryParameter("MerchantOrderNo")==null?"":uri.getQueryParameter("MerchantOrderNo"));

				SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_CAmessage1), false, false);
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
		/* if(ServiceUtil.getXposDriverService()!=null)
			 unbindService(ServiceUtil.mserviceConnection);*/
		super.onDestroy();
	} 
	public void backIntent(String Rc, String message){
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
		backIntent.putExtra("RefundTxnNo", requestObj.getMerchantTxnNo());
		backIntent.putExtra("ChannelTxnNo", backJson.getString("ChannelTxnNo"));
		backIntent.putExtra("TotalAmt", new Double(requestObj.getTxnAmt()));
		backIntent.putExtra("MerchantName", backJson.getString("MerchantName"));
		backIntent.putExtra("PayerID", backJson.getString("PayerID"));
		//backIntent.putExtra("ChannelID", requestObj.getChannelID());
//		backIntent.putExtra("ChannelID", MobileUtil.getSoftposChannelId(backJson.getString("ChannelID"), backJson.getString("BranchChannelID")));
        backIntent.putExtra("ChannelID", backJson.getString("ChannelID"));
		backIntent.putExtra("ChannelIDName", MobileUtil.getSoftposChannelName(backJson.getString("AccType")));
		backIntent.putExtra("MerchantID", mid);
		backIntent.putExtra("TerminalID", tid);
		backIntent.putExtra("ExchangeRate", backJson.getDouble("ExchangeRate"));
		backIntent.putExtra("ChannelAmt", backJson.getDouble("ChannelAmt"));
		backIntent.putExtra("ChannelCurrency", backJson.getString("ChannelCurrency"));
		backIntent.putExtra("PrintInfo", backJson.getString("PrintInfo"));
		backIntent.putExtra("AccType", backJson.getString("AccType"));
		setResult(Activity.RESULT_OK,backIntent);
		finish();
    }
	
	public Handler handler = new Handler(){
		@Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        String val = data.getString("respContent");
	        log.info("交易返回信息："+val);
	        if(!val.contains("RespCode")){
	        	backIntent(Constant.UNK, val);
	        }else{
	        	backJson = MobileUtil.doJsonMessage(val);
		        String RespCode = backJson.getString("RespCode");
		        backIntent.putExtra("RespCode", RespCode);
		        backIntent.putExtra("RespDesc", backJson.getString("RespDesc"));
		        //TODO 20190415 测试预授权阶段查询
		        //RespCode = Constant.UNK;
		        if(Constant.SUCC.equals(RespCode)){
					requestObj.setPrintFlag("1");
					backJson.put("PrintInfo", writePrintText(backJson));
					backSuccIntent();
		        }else if(Constant.UNK.equals(RespCode)){
	        		queryCount++;
	        		handler.postDelayed(new Runnable() {
	     				@Override
	     				public void run() {
	     					if(queryCount>2 && queryCount%2==0){
				        		 AlertDialog.Builder StopDialog =new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);//定义一个弹出框对象
								 StopDialog.setCancelable(false);
								 StopDialog.setMessage(R.string.mobile_SImessage4);
								 StopDialog.setPositiveButton(R.string.mobile_goon, new DialogInterface.OnClickListener() {
					             public void onClick(DialogInterface dialog, int which) {
					            	 new Thread(runnableQuery).start();
					             }  
					             });
					             StopDialog.setNegativeButton(R.string.mobile_quxiao,new DialogInterface.OnClickListener() {
					                 public void onClick(DialogInterface dialog, int which) {
										 backIntent(Constant.FAILE, "用户取消");
					                 }
					             });
					             Dialog a = StopDialog.create();
						  	     a.getWindow().addFlags(5);
						  	     a.show();
						  	     a.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
				        	}else
				            	 new Thread(runnableQuery).start();
	     				}
	     			}, 5000);
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
	    	 Message msg = new Message();
	    	 String respContent =  authThaw();;
		     Bundle data = new Bundle();
		     data.putString("respContent",respContent);
		     msg.setData(data);
		     handler.sendMessage(msg);
	    }
	};
	Runnable runnableQuery = new Runnable(){
	    @Override
	    public void run() {
	    	 //String respContent = authQuery();
			String respContent = authThaw();
	    	 Message msg = new Message();
		     Bundle data = new Bundle();
		     data.putString("respContent",respContent);
		     msg.setData(data);
		     handler.sendMessage(msg);
	    }
	};
	public String authQuery(){
        String respContent = "";
        try {
            JSONObject postData = new JSONObject();
            postData.put("TxnType", Constant.TxnType.AUTHQUERY);
            postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
            postData.put("ChannelID", requestObj.getChannelID());
            postData.put("OrgTxnNo", requestObj.getOrgTxnNo());
            postData.put("MerchantID", mid);
            postData.put("TerminalID", tid);
            postData.put("TxnAmt", requestObj.getTxnAmt());
            postData.put("CashierID", requestObj.getCashierID());
            //postData.put("CurrencyCode", requestObj.getCurrencyCode());
            postData.put("OpType", Constant.OpType.Unfreeze);
            postData.put("TxnReqTime", DateUtil.dateToStr(new Date(), "yyyy/MM/dd HH:mm:ss"));
            String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
            postData.put("Sign", sign);
            log.info("扫码退货查询交易信息："+postData.toString());
            respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
        } catch (Exception e1) {
            log.info("扫码退货查询交易报错："+e1.getLocalizedMessage());
            respContent =  mContext.getString(R.string.mobile_CAmessage3);
        }
        return respContent;
    }
	public String authThaw(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
			postData.put("AccTypeFlag", "1");
    		postData.put("TxnType", Constant.TxnType.AUTHTHAW);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
    		postData.put("MerchantTxnNo", requestObj.getMerchantTxnNo());
    		//postData.put("IMEI", imei);
			postData.put("MerchantID", mid);
			postData.put("TerminalID", tid);
			postData.put("MerchantOrderNo", requestObj.getMerchantOrderNo());
    		postData.put("AuthNo", requestObj.getAuthNo());
    		postData.put("TxnAmt", requestObj.getTxnAmt());
    		postData.put("CashierID", requestObj.getCashierID());
    		postData.put("TxnReqTime", requestObj.getTxnReqTime());
			if(requestObj.getTxnShortDesc()!=null && !"".equals(requestObj.getTxnShortDesc())) {
				postData.put("TxnShortDesc", requestObj.getTxnShortDesc());
			} else {
				//postData.put("TxnShortDesc", "Empty TxnShortDesc");
				return "TxnShortDesc字段为空";
			}
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		//postData.put("AddInfo", requestObj.getAddInfo());
    		log.info("预授权解冻交易信息："+postData.toString());
    		respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("预授权解冻交易报错："+e1.getLocalizedMessage());
	        respContent = mContext.getString(R.string.mobile_CAmessage3);
	    }
		return respContent;
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
		for(int idx=0; idx<2; idx++) {
			if(idx == 0){
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr2)).append(formatLCEnd).append(formatEnter);
			}else{
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr3)).append(formatLCEnd).append(formatEnter);
			}
			stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);

//			stringBuilder.append(formatNCStart).append(json.getString("MerchantName")).append(formatNCEnd).append(formatEnter);

			if (!"".equals(merName)) {
				stringBuilder.append(formatNCStart).append(merName).append(formatNCEnd).append(formatEnter);
				preferences.edit().putString("MerchantName", merName).commit();
			} else {
				stringBuilder.append(formatNCStart).append(preferences.getString("MerchantName","未知特约商户")).append(formatNCEnd).append(formatEnter);
			}

			stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr25)+":"+ FormatUtil.double2StrAmt(json.getDouble("TxnAmt"))).append(formatLCEnd).append(formatEnter);

			stringBuilder.append(formatSCStart).append(mContext.getString(R.string.mobile_tranSucc)).append(formatSCEnd).append(formatEnter);

			stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);

			//stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11)+":"+ MobileUtil.getChannelMap().get(requestObj.getChannelID())).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11) + ":" + MobileUtil.getSoftposChannelName(json.getString("AccType"))).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr10)+":"+json.getString("TxnAnsTime")).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr4)+":"+json.getString("MerchantID")).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr5)+":"+json.getString("TerminalID")).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr6)+":"+requestObj.getCashierID()).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr19)+":").append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNCStart).append(requestObj.getMerchantTxnNo()).append(formatNCEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo)+":").append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNCStart).append(json.getString("AuthNo")).append(formatNCEnd).append(formatEnter);

			if(requestObj.getAddInfo()!=null && !requestObj.getAddInfo().equals("")){
				JSONObject jsonAdd = new JSONObject(requestObj.getAddInfo());
				stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);
				for (int i = 1; i < jsonAdd.length()/2+1; i++) {
					stringBuilder.append(formatNLStart).append(jsonAdd.getString("Key"+i)+":"+jsonAdd.getString("Value"+i)).append(formatNLEnd).append(formatEnter);
				}
			}
			stringBuilder.append(formatSCStart).append("------------------------------------------").append(formatSCEnd).append(formatEnter);

			if(idx==0){
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr14)+":").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);
				stringBuilder.append(formatEnter);
				stringBuilder.append(formatCutpaper);
			} else {
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatEnter);
			}
		}
		return stringBuilder.toString();
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
		if (keyCode == KeyEvent.KEYCODE_BACK)//返回键监听
		{
			return true;
        }
         return super.onKeyDown(keyCode, event);
		
	}

}
