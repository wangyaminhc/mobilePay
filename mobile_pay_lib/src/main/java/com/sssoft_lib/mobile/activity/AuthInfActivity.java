package com.sssoft_lib.mobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
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
import com.sssoft_lib.mobile.view.SelfDialog;
import com.sssoft_lib.mobile.view.SuccinctProgress;

import org.apache.log4j.Logger;
import org.json.sssoft.JSONObject;

import java.util.Date;

public class AuthInfActivity extends BaseActivity {
	
	private static Logger log = Logger.getLogger(AuthInfActivity.class);
	private SharedPreferences preferences;
	private static RequestObj requestObj = new RequestObj();
	private String barCode;
	//private MyApplication myApp;
	private JSONObject backJson;
	private Intent backIntent = new Intent();
	private Context mContext;
	private int queryCount = 0 ;
	private String cannelFlag = "";
	private Integer transType;
	private SelfDialog selfDialog;
	private String title ="";
	private String mid = "";
	private String tid = "";
	private static Integer autoQuery;
	//	用于校验渠道和扫描匹配
	private String tmpPayMode = "";
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
			String rc = CheckUtil.chkAuth(mContext, uri);
			if(rc.equals("")){
				requestObj.setSmType(uri.getQueryParameter("SmType")==null?"":uri.getQueryParameter("SmType"));
				requestObj.setMerchantTxnNo(uri.getQueryParameter("MerchantTxnNo")==null?"":uri.getQueryParameter("MerchantTxnNo"));
				requestObj.setTxnAmt(uri.getQueryParameter("TxnAmt")==null?"":uri.getQueryParameter("TxnAmt"));
				requestObj.setTxnReqTime(uri.getQueryParameter("TxnReqTime")==null?"":uri.getQueryParameter("TxnReqTime"));
				requestObj.setCashierID(uri.getQueryParameter("CashierID")==null?"":uri.getQueryParameter("CashierID"));
				requestObj.setTxnShortDesc(uri.getQueryParameter("TxnShortDesc")==null?"":uri.getQueryParameter("TxnShortDesc"));
				requestObj.setAddInfo(uri.getQueryParameter("AddInfo")==null?"":uri.getQueryParameter("AddInfo"));
				requestObj.setChannelID(uri.getQueryParameter("ChannelID")==null?"":uri.getQueryParameter("ChannelID"));
				barCode=uri.getQueryParameter("QRCode")==null?"":uri.getQueryParameter("QRCode");
				requestObj.setMerchantOrderNo(uri.getQueryParameter("MerchantOrderNo")==null?"":uri.getQueryParameter("MerchantOrderNo"));
				tmpPayMode=uri.getQueryParameter("PayMode")==null?"":uri.getQueryParameter("PayMode");
				if(requestObj.getSmType()!=null && requestObj.getSmType().equals("1")){
					SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_CAmessage1), false, false);
					transType = 1;
					new Thread(runnable).start();
				} else if(!TextUtils.isEmpty(barCode)){
					SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_CAmessage1), false, false);
					transType = 0;
					new Thread(runnable).start();
				}else{
					backIntent(Constant.FAILE,mContext.getString(R.string.no_qr_code));
				}
			}else{
				backIntent(Constant.FAILE, rc);
			}
			
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//AIDLService.init(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//AIDLService.unBindDeviceService();
	}

	private void processScanResult(String val){
		SuccinctProgress.showSuccinctProgress(mContext, mContext.getString(R.string.mobile_CAmessage1), false, false);
		barCode = val;
		if("".equals(barCode)){
			backIntent(Constant.FAILE, mContext.getString(R.string.mobile_SImessage1));
		}else{
			transType = 0;
			new Thread(runnable).start();
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
	public void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		if (data != null) {
			SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_CAmessage1), false, false);
			barCode = data.getStringExtra("result");
			String channelId = MobileUtil.code2Channel(barCode, MobileUtil.getBarCodeFlagMap());
			transType = 0;
			new Thread(runnable).start();
		}else{
			backIntent(Constant.FAILE, mContext.getString(R.string.mobile_SImessage3));
		}
	}
	@Override
	public void onDestroy() { 
		 SuccinctProgress.dismiss();
		 /*if(ServiceUtil.getXposDriverService()!=null)
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
	        	switch (msg.what) {
				case 0:
					//TODO 20190412 测试
					//RespCode = Constant.UNK;

					if(Constant.SUCC.equals(RespCode)){
						requestObj.setPrintFlag("1");
						backJson.put("PrintInfo", writePrintText(backJson));
						backSuccIntent();
			        }else if(Constant.UNK.equals(RespCode)){
			    		if(cannelFlag.equals("1")){
			        		transType = 3;
			        		new Thread(runnable).start();
			        	}else{
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
							            	 	transType = 2; //继续反扫查询交易
							            	 	new Thread(runnable).start();
							             }  
							             });
							             StopDialog.setNegativeButton(R.string.mobile_quxiao,new DialogInterface.OnClickListener() {
							                 public void onClick(DialogInterface dialog, int which) {
								            	 cannelFlag = "1";//撤销标志
								            	 transType = 2; //取消前先做查询交易
								            	 new Thread(runnable).start();
							                 }
							             });
							             Dialog a = StopDialog.create();
								  	     a.getWindow().addFlags(5);
								  	     a.show();
								  	     a.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
						        	}else{
						        		 transType = 2; //继续反扫查询交易
						            	 new Thread(runnable).start();
						        	}
			     				}
			     			}, 5000);
			        	}
			        }else{
			        	setResult(Activity.RESULT_OK,backIntent);
			    		finish();
			        }
					break;
				case 1:
					SuccinctProgress.dismiss();
					if(Constant.SUCC.equals(RespCode)){
						try {
							autoQuery = 5;
		    	       		selfDialog = new SelfDialog(AuthInfActivity.this);
		    	       		BitmapDrawable bmpDraw=new BitmapDrawable(getResources(), MobileUtil.createQRCode(backJson.getString("QRCode")));
		    	       		/*if(requestObj.getChannelID().equals("6"))
		    	       			title = mContext.getString(R.string.mobile_zfbQRCode);*/
		    	       		title = "预授权交易";
		    	            selfDialog.setTitle(title + "\n\n"+mContext.getString(R.string.mobile_autoQuery)+"...");
		    	            selfDialog.setBmpDraw(bmpDraw);
		    	            selfDialog.setCanceled(false);
		    	            selfDialog.setCanceledOutside(false);
		    	            selfDialog.setNoOnclickListener(mContext.getString(R.string.mobile_quxiao), new SelfDialog.onNoOnclickListener() {
		    	                @Override
		    	                public void onNoClick() {
		    	                	if(autoQuery == 0){
			    	                    selfDialog.dismiss();
			    	                    SuccinctProgress.showSuccinctProgress(mContext, mContext.getString(R.string.mobile_QImessage1), false, false);
			    	                    cannelFlag = "1";//撤销标志
					            	 	transType = 2; //取消前先做查询交易
					            	 	new Thread(runnable).start();
		    	                	}
		    	                }
		    	            });

		    	            selfDialog.setYesOnclickListener(mContext.getString(R.string.mobile_sdQuery), new SelfDialog.onYesOnclickListener() {
		    	                @Override
		    	                public void onYesClick() {
		    	                	if(autoQuery == 0){
			    	                    SuccinctProgress.showSuccinctProgress(mContext, mContext.getString(R.string.mobile_QImessage1), false, false);
					            	 	transType = 4; 
					            	 	new Thread(runnable).start();
		    	                	}
		    	                }
		    	            });
		    	            selfDialog.getWindow().addFlags(5);
		    	            selfDialog.getWindow().addFlags(3);
		    	            selfDialog.show();
		    	            selfDialog.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		    	            handler.postDelayed(new Runnable() {
			     				@Override
			     				public void run() {
			     					if(autoQuery > 0){
							        	transType = 4; //正扫查询交易
							            new Thread(runnable).start();
			     					}
			     				}
			     			}, 8000);
		    	            
						} catch (Exception e) {
							backIntent(Constant.FAILE,e.getLocalizedMessage());
						}
			        }else{
			        	setResult(Activity.RESULT_OK,backIntent);
			    		finish();
			        }
					break;
				case 2:
					Log.d("sunhao", "handleMessage: "+backJson.toString());
					if(Constant.SUCC.equals(RespCode)) {
						backIntent(Constant.FAILE, mContext.getString(R.string.mobile_SImessage3));
					}else {
						selfDialog=new SelfDialog(mContext);
						selfDialog.setTitle("提示");
						selfDialog.setCanceled(false);
						selfDialog.setCanceledOutside(false);
						selfDialog.setMessage("用户支付中，不允许撤销，是否继续等待？");
						selfDialog.setNoOnclickListener("否", new SelfDialog.onNoOnclickListener() {
							@Override
							public void onNoClick() {
								backIntent(Constant.FAILE, mContext.getString(R.string.mobile_SImessage3));
							}
						});
						selfDialog.setYesOnclickListener("是", new SelfDialog.onYesOnclickListener() {
							@Override
							public void onYesClick() {
								selfDialog.dismiss();
								SuccinctProgress.showSuccinctProgress(mContext, mContext.getString(R.string.mobile_QImessage1), false, false);
								cannelFlag="";//不做撤销了
								transType = 2; //继续查询交易
								new Thread(runnable).start();
							}
						});
						selfDialog.show();

					}
					break;
				case 3:
					if(Constant.SUCC.equals(RespCode)){
						requestObj.setPrintFlag("1");
						backJson.put("PrintInfo", writePrintText(backJson));
						backSuccIntent();
					}else if(Constant.UNK.equals(RespCode)){
			    		SuccinctProgress.dismiss();
			        	if(autoQuery>0){
			        		autoQuery--;
			        		handler.postDelayed(new Runnable() {
				     				@Override
				     				public void run() {
				     					if(autoQuery > 0){
								        	transType = 4; //正扫查询交易
								            new Thread(runnable).start();
				     					}
				     				}
				     			}, 5000);
				        }
			        }else{
			        	setResult(Activity.RESULT_OK,backIntent);
			    		finish();
			        }
					break;
				default:
					break;
				}
	        }
	        
	    }
	};
	Runnable runnable = new Runnable(){
	    @Override
	    public void run() {
	    	 Message msg = new Message();
	    	 String respContent = "";
	    	 switch (transType) {
			 case 0:
				 if( CheckUtil.checkIsMatch( tmpPayMode,barCode)){
					 respContent = authPay();
				 }else{
					 JSONObject back = new JSONObject();
					 back.put("RespCode",Constant.FAILE);
					 back.put("RespDesc","支付方式不匹配或扫码错误");
					 respContent = back.toString();
				 }
				 msg.what = 0;
				break;
			 case 1:
				 respContent = preAuth();
				 msg.what = 1;
				break;
			 case 2:
				 respContent = authQuery();
				 msg.what = 0;
				break;
			 case 3:
				 respContent = authCancel();
				 msg.what = 2;
				break;
			 case 4:
				 respContent = authQuery();
				 msg.what = 3;
				break;
			 }
		     Bundle data = new Bundle();
		     data.putString("respContent",respContent);
		     msg.setData(data);
		     handler.sendMessage(msg);
	    }
	};
	public String authCancel(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
    		postData.put("TxnType", Constant.TxnType.AUTHCANCEL);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
    		postData.put("ChannelID", requestObj.getChannelID());
    		postData.put("OrgTxnNo", requestObj.getMerchantTxnNo());
			postData.put("MerchantID", mid);
			postData.put("TerminalID", tid);
    		postData.put("TxnAmt", requestObj.getTxnAmt());
    		postData.put("CashierID", requestObj.getCashierID());
    		postData.put("TxnReqTime", DateUtil.dateToStr(new Date(), "yyyy/MM/dd HH:mm:ss"));
    		//postData.put("TxnShortDesc", requestObj.getTxnShortDesc());
			if(requestObj.getTxnShortDesc()!=null && !"".equals(requestObj.getTxnShortDesc())) {
				postData.put("TxnShortDesc", requestObj.getTxnShortDesc());
			} else {
				return "TxnShortDesc字段为空";
			}
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		//postData.put("AddInfo", requestObj.getAddInfo());
    		log.info("预授权等待撤销交易信息："+postData.toString());
    		respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
    	} catch (Exception e1) {
			log.info("预授权等待撤销交易报错："+e1.getLocalizedMessage());
	        respContent = mContext.getString(R.string.mobile_CAmessage3);
	    }
		return respContent;
	}
	public String authPay(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
			postData.put("AccTypeFlag", "1");
    		postData.put("TxnType", Constant.TxnType.AUTHPAY);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
    		postData.put("MerchantTxnNo", requestObj.getMerchantTxnNo());
			postData.put("MerchantID", mid);
			postData.put("TerminalID", tid);
    		//postData.put("IMEI", imei);
    		postData.put("QRCode", barCode);
    		postData.put("TxnAmt", requestObj.getTxnAmt());
    		postData.put("CashierID", requestObj.getCashierID());
			postData.put("MerchantOrderNo", requestObj.getMerchantOrderNo());
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
    		log.info("预授权交易信息："+postData.toString());
    		respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("预授权交易报错："+e1.getLocalizedMessage());
	        respContent = mContext.getString(R.string.mobile_CAmessage3);
	    }
		return respContent;
	}
	public String preAuth(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
    		postData.put("TxnType", Constant.TxnType.PREAUTH);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
    		postData.put("MerchantTxnNo", requestObj.getMerchantTxnNo());
			postData.put("MerchantID", mid);
			postData.put("TerminalID", tid);
			postData.put("AccTypeFlag", "1");
    		//postData.put("IMEI", imei);
			postData.put("MerchantOrderNo", requestObj.getMerchantOrderNo());
    		postData.put("TxnAmt", requestObj.getTxnAmt());
    		postData.put("CashierID", requestObj.getCashierID());
    		postData.put("TxnReqTime", requestObj.getTxnReqTime());
    		if(requestObj.getTxnShortDesc()!=null && !"".equals(requestObj.getTxnShortDesc())) {
				postData.put("TxnShortDesc", requestObj.getTxnShortDesc());
			}
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		//postData.put("AddInfo", requestObj.getAddInfo());
    		log.info("预授权发码交易信息："+postData.toString());
    		respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("预授权发码交易报错："+e1.getLocalizedMessage());
	        respContent = mContext.getString(R.string.mobile_CAmessage3);
	    }
		return respContent;
	}
	public String authQuery(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
    		postData.put("TxnType", Constant.TxnType.AUTHQUERY);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
    		postData.put("OrgTxnNo", requestObj.getMerchantTxnNo());
			postData.put("MerchantID", mid);
			postData.put("AccTypeFlag", "1");
			postData.put("TerminalID", tid);
    		postData.put("OpType", Constant.OpType.Freeze);
    		//postData.put("IMEI", imei);
    		postData.put("TxnReqTime", DateUtil.dateToStr(new Date(), "yyyy/MM/dd HH:mm:ss"));
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE? Constant.HttpArg.PROD_KEY: Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		log.info("预授权查询交易信息："+postData.toString());
			respContent = HttpProxy.doRequest(mContext, Constant.PROD_MODE? Constant.HttpArg.PROD_POST_URL: Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("预授权查询交易报错："+e1.getLocalizedMessage());
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
			if(idx==0){
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr2)).append(formatLCEnd).append(formatEnter);
			}else{
				stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr3)).append(formatLCEnd).append(formatEnter);
			}
			stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);


			if (!"".equals(merName)) {
				stringBuilder.append(formatNCStart).append(merName).append(formatNCEnd).append(formatEnter);
				preferences.edit().putString("MerchantName", merName).commit();
			} else {
				stringBuilder.append(formatNCStart).append(preferences.getString("MerchantName","未知特约商户")).append(formatNCEnd).append(formatEnter);
			}
			stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr22)+":"+ FormatUtil.double2StrAmt(json.getDouble("TxnAmt"))).append(formatLCEnd).append(formatEnter);

			stringBuilder.append(formatSCStart).append(mContext.getString(R.string.mobile_tranSucc)).append(formatSCEnd).append(formatEnter);

			stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr16)+":"+json.getString("PayerID")).append(formatNLEnd).append(formatEnter);

			//stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11)+":"+ MobileUtil.getChannelMap().get(requestObj.getChannelID())).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11) + ":" + MobileUtil.getSoftposChannelName(json.getString("AccType"))).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr10)+":"+json.getString("TxnAnsTime")).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr4)+":"+json.getString("MerchantID")).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr5)+":"+json.getString("TerminalID")).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr6)+":"+requestObj.getCashierID()).append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr19)+"(预授权撤销使用):").append(formatNLEnd).append(formatEnter);

			stringBuilder.append(formatNCQRStart).append(requestObj.getMerchantTxnNo()).append(formatNCQREnd).append(formatEnter);

			stringBuilder.append(formatNCStart).append(requestObj.getMerchantTxnNo()).append(formatNCEnd).append(formatEnter);

			//20190415 支付宝交易打印出授权号二维码
			if("6".equals(json.getString("ChannelID"))) {
			    stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo)+"(预授权完成/解冻使用):").append(formatNLEnd).append(formatEnter);
                stringBuilder.append(formatNCQRStart).append(json.getString("AuthNo")).append(formatNCQREnd).append(formatEnter);
            } else {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_authNo)+"(预授权完成使用):").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatNCQRStart).append(json.getString("AuthNo")).append(formatNCQREnd).append(formatEnter);
			}

			stringBuilder.append(formatNCStart).append(json.getString("AuthNo")).append(formatNCEnd).append(formatEnter);

			if(requestObj.getAddInfo()!=null && !requestObj.getAddInfo().equals("")){
				JSONObject jsonAdd = new JSONObject(requestObj.getAddInfo());
				stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);
				for (int i = 1; i < jsonAdd.length()/2+1; i++) {
					stringBuilder.append(formatNLStart).append(jsonAdd.getString("Key"+i)+":"+jsonAdd.getString("Value"+i)).append(formatNLEnd).append(formatEnter);
				}
			}

			stringBuilder.append(formatSCStart).append("------------------------------------------").append(formatSCEnd).append(formatEnter);

			if (idx == 0) {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr14) + ":").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatSCStart).append("-----------------------------------------").append(formatSCEnd).append(formatEnter);
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_memo)).append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatEnter);
				stringBuilder.append(formatCutpaper);
			} else {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_memo)).append(formatNLEnd).append(formatEnter);
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
