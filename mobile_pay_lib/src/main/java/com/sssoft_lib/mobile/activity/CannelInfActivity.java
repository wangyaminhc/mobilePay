package com.sssoft_lib.mobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.sssoft_lib.mobile.view.SuccinctProgress;

import org.apache.log4j.Logger;
import org.json.sssoft.JSONObject;

import java.util.Date;

public class CannelInfActivity extends BaseActivity{
	private static Logger log = Logger.getLogger(CannelInfActivity.class);
	private SharedPreferences preferences;
	private RequestObj requestObj = new RequestObj();
	private JSONObject backJson;
	private Intent backIntent = new Intent();
	private static Context mContext;
	//private MyApplication myApp;
	private String txnTime = "";
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

		Intent intent = getIntent();
		if(null!=intent){
			Uri uri = intent.getData();
			if(uri == null){
				return;
			}
			String rc = CheckUtil.chkCancel(mContext, uri);
			if(rc.equals("")){
				requestObj.setOrgTxnNo(uri.getQueryParameter("OrgTxnNo")==null?"":uri.getQueryParameter("OrgTxnNo"));
				requestObj.setOrgPlatformTxnNo(uri.getQueryParameter("OrgPlatformTxnNo")==null?"":uri.getQueryParameter("OrgPlatformTxnNo"));
				requestObj.setRefundAmt(uri.getQueryParameter("RefundAmt")==null?"":uri.getQueryParameter("RefundAmt"));
				requestObj.setCurrencyCode(uri.getQueryParameter("CurrencyCode")==null?"":uri.getQueryParameter("CurrencyCode"));
				requestObj.setTxnReqTime(uri.getQueryParameter("TxnReqTime")==null?"":uri.getQueryParameter("TxnReqTime"));
				requestObj.setCashierID(uri.getQueryParameter("CashierID")==null?"":uri.getQueryParameter("CashierID"));
				requestObj.setChannelID(uri.getQueryParameter("ChannelID")==null?"":uri.getQueryParameter("ChannelID"));
				requestObj.setMerchantOrderNo(uri.getQueryParameter("MerchantOrderNo")==null?"":uri.getQueryParameter("MerchantOrderNo"));

				SuccinctProgress.showSuccinctProgress(this, mContext.getString(R.string.mobile_CAmessage1), false, false);
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
		/*mid = "104650053110020";
		tid = "12345678";*/
        if(mid.equals("")){
        	backIntent(Constant.FAILE, mContext.getString(R.string.mobile_CAmessage2));
        }
	}
	@Override 
	public void onDestroy() { 
		 SuccinctProgress.dismiss();
		 super.onDestroy();
	}


	public void backIntent(String Rc, String message){
    	Intent intent = new Intent();
		intent.putExtra("RespCode", Rc);
		intent.putExtra("RespDesc", message);
		setResult(RESULT_OK, intent);
		finish();
    }

	Handler handler = new Handler(){
	    @SuppressLint("NewApi")
		@Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        String val = data.getString("respContent");
	        log.info("扫码交易返回信息："+val);
	        if(!val.contains("RespCode")){
	        	backIntent(Constant.FAILE, val);
	        }else{
	        	backJson = new JSONObject(val);
		        String RespCode = backJson.getString("RespCode");
		        backIntent.putExtra("RespCode", RespCode);
		        backIntent.putExtra("RespDesc", backJson.getString("RespDesc"));
				if(Constant.SUCC.equals(RespCode)){
					txnTime = DateUtil.dateToStr(new Date(), "yyyy/MM/dd HH:mm:ss");
					requestObj.setPrintFlag("1");
		        	setPrintText();
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
	    	 String  respContent = cancel();
	    	 Message msg = new Message();
		     Bundle data = new Bundle();
		     data.putString("respContent",respContent);
		     msg.setData(data);
		     handler.sendMessage(msg);
	    }
	};
	public String cancel(){
    	String respContent = "";
    	try {
    		JSONObject postData = new JSONObject();
			postData.put("AccTypeFlag", "1");
    		postData.put("TxnType", Constant.TxnType.CANNEL);
    		postData.put("PartnerID", Constant.HttpArg.PARTNER_ID);
    		if(!TextUtils.isEmpty(requestObj.getOrgPlatformTxnNo())){
				 postData.put("OrgPlatformTxnNo", requestObj.getOrgPlatformTxnNo());
			}else{
				postData.put("OrgTxnNo", requestObj.getOrgTxnNo());
			}
			postData.put("MerchantID", mid);
			postData.put("MerchantOrderNo", requestObj.getMerchantOrderNo());
    		postData.put("TxnAmt", requestObj.getRefundAmt());
    		postData.put("CashierID", requestObj.getCashierID());
    		postData.put("CurrencyCode", requestObj.getCurrencyCode());
    		postData.put("TxnReqTime", requestObj.getTxnReqTime());
    		String sign = SignVerifyUtil.sign(postData, Constant.PROD_MODE?Constant.HttpArg.PROD_KEY:Constant.HttpArg.TEST_KEY);
    		postData.put("Sign", sign);
    		log.info("扫码撤销交易信息："+postData.toString());
    		respContent = HttpProxy.doRequest(mContext,Constant.PROD_MODE?Constant.HttpArg.PROD_POST_URL:Constant.HttpArg.TEST_POST_URL,postData.toString(), true);
		} catch (Exception e1) {
			log.info("扫码撤销交易报错："+e1.getLocalizedMessage());
	        respContent = mContext.getString(R.string.mobile_CAmessage3);
	    }
		return respContent;
	}
	
	public void setPrintText(){
		/*Bundle formatSC = new Bundle();
		formatSC.putString("font", "small");
		formatSC.putString("align", "center");
		Bundle formatNC = new Bundle();
		formatNC.putString("font", "normal");
		formatNC.putString("align", "center");
		Bundle formatNL = new Bundle();
		formatNL.putString("font", "normal");
		formatNL.putString("align", "left");
		Bundle formatNR = new Bundle();
		formatNR.putString("font", "normal");
		formatNR.putString("align", "right");
		Bundle formatLC = new Bundle();
		formatLC.putString("font", "large");
		formatLC.putString("align", "center");
		Bundle formatLL = new Bundle();
		formatLL.putString("font", "large");
		formatLL.putString("align", "left");
		try {
			//Printer printer = ServiceUtil.getXposDriverService().getPrinter();
			Printer printer = MobileUtil.xposDriverService.getPrinter();
			if(requestObj.getPrintFlag().equals("1")){
				printer.addText(formatLC, mContext.getString(R.string.mobile_printStr2));
			}else{
				printer.addText(formatLC, mContext.getString(R.string.mobile_printStr3));
			}
			printer.addText(formatSC, "----------------------------------------------");

			//printer.addText(formatNC, backJson.getString("MerchantName"));
			
			printer.addText(formatLC, mContext.getString(R.string.mobile_printStr8)+":"+FormatUtil.double2StrAmt(new Double(requestObj.getRefundAmt())));
			
			printer.addText(formatSC, mContext.getString(R.string.mobile_tranSucc));

			printer.addText(formatSC, "----------------------------------------------");
			
			printer.addText(formatNL, mContext.getString(R.string.mobile_printStr11)+":"+MobileUtil.getChannelMap().get(requestObj.getChannelID()));
			
			printer.addText(formatNL, mContext.getString(R.string.mobile_printStr10)+":"+txnTime);
			
			printer.addText(formatNL, mContext.getString(R.string.mobile_printStr4)+":"+mid);
			
			printer.addText(formatNL, mContext.getString(R.string.mobile_printStr5)+":"+tid);
			
			printer.addText(formatNL, mContext.getString(R.string.mobile_printStr6)+":"+requestObj.getCashierID());

			printer.addText(formatSC, "----------------------------------------------");
			if(requestObj.getPrintFlag().equals("1")){
				printer.addText(formatNL, mContext.getString(R.string.mobile_printStr14)+":");
				printer.addText(formatLC, "           ");
				printer.addText(formatLC, "           ");
				printer.addText(formatSC, "----------------------------------------------");
			}
			printer.paperSkip(2);
    		printer.startPrinter(new PrinterListener.Stub() {
				@Override
				public void onFinish() throws RemoteException {
					 Message msg = printHandler.obtainMessage();
					 if(requestObj.getPrintFlag().equals("1")){
           				 msg.what = 0; 
					 }else{
						 msg.what = 2; 
					 }
					 printHandler.sendMessage(msg);
				}
				@Override
				public void onError(String code, String detail) throws RemoteException {
					 Message msg = printHandler.obtainMessage();
      				 msg.what = 1;
      				 msg.obj = detail;
      				 printHandler.sendMessage(msg);
      			}
			});
		} catch (Exception e) {
			log.info("打印失败："+e.getLocalizedMessage());
			Message msg = printHandler.obtainMessage();
			msg.obj = mContext.getString(R.string.mobile_printBtn3)+"："+e.getLocalizedMessage();
			msg.what = 3; 
			printHandler.sendMessage(msg);
		}*/
	}
  	public Handler printHandler = new Handler(Looper.getMainLooper(),new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
		    if (msg.what==0) {
				 AlertDialog.Builder StopDialog =new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);//定义一个弹出框对象
				 StopDialog.setCancelable(false);
				 StopDialog.setPositiveButton(R.string.mobile_printBtn1, new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {  
	            	 backSuccIntent();
	             }  
	             });
	             StopDialog.setNegativeButton(R.string.mobile_printBtn2,new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {  
	                	 	 requestObj.setPrintFlag("2");
	                		 setPrintText();
	                 }
	             });
	             Dialog a = StopDialog.create();
		  	     a.getWindow().addFlags(5);
		  	     a.show();
		  	     a.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			}
		    if (msg.what==1) {
		    	 AlertDialog.Builder StopDialog =new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);//定义一个弹出框对象
				 StopDialog.setCancelable(false);
				 StopDialog.setMessage((String)msg.obj);
	             StopDialog.setPositiveButton(R.string.mobile_ok,new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {  
	                	 try {
	                		 setPrintText();
						 } catch (Exception e) {
							log.info("打印失败："+e.getLocalizedMessage());
						 }
	                 }
	             });
	             StopDialog.setNegativeButton(R.string.mobile_quxiao,new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int which) {  
	                	 backSuccIntent();
	                 }
	             });
	             Dialog a = StopDialog.create();
		  	     a.getWindow().addFlags(5);
		  	     a.show();
		  	     a.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			}
		    if (msg.what==2 || msg.what==3){
		    	 if(msg.what==3)
		    		 Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_LONG).show();
		    	 backSuccIntent();
			}
			return false;
		}
	});
  	public void backSuccIntent(){
    	SuccinctProgress.dismiss();
		backIntent.putExtra("TxnAnsTime", backJson.getString("TxnAnsTime"));
		backIntent.putExtra("PlatformTxnNo", backJson.getString("PlatformTxnNo"));
		backIntent.putExtra("RefundTxnNo", requestObj.getRefundTxnNo());
		backIntent.putExtra("ChannelTxnNo", backJson.getString("ChannelTxnNo"));
		backIntent.putExtra("TotalAmt", new Double(requestObj.getRefundAmt()));
		backIntent.putExtra("IncomeAmt", backJson.getDouble("IncomeAmt"));
		backIntent.putExtra("InvoiceAmt", backJson.getDouble("InvoiceAmt"));
		backIntent.putExtra("PointAmt", backJson.getDouble("PointAmt"));
		backIntent.putExtra("MerchantDisctAmt", backJson.getDouble("MerchantDisctAmt"));
		backIntent.putExtra("ChannelDisctAmt", backJson.getDouble("ChannelDisctAmt"));
		backIntent.putExtra("MerchantRechargeAmt", backJson.getDouble("MerchantRechargeAmt"));
		backIntent.putExtra("MerchantName", backJson.getString("MerchantName"));
		backIntent.putExtra("PayerID", backJson.getString("PayerID"));
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
  	@Override
	public void onAttachedToWindow() {
	     this.getWindow().addFlags(5);
	     this.getWindow().setType(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	     super.onAttachedToWindow();
	}
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)//返回键监听
		{
			return true;
        }
         return super.onKeyDown(keyCode, event);
		
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
			//stringBuilder.append(formatNCStart).append(json.optString("MerchantName")).append(formatNCEnd).append(formatEnter);

			if (!"".equals(merName)) {
				stringBuilder.append(formatNCStart).append(merName).append(formatNCEnd).append(formatEnter);
				preferences.edit().putString("MerchantName", merName).commit();
			} else {
				stringBuilder.append(formatNCStart).append(preferences.getString("MerchantName","未知特约商户")).append(formatNCEnd).append(formatEnter);
			}
			stringBuilder.append(formatLCStart).append(mContext.getString(R.string.mobile_printStr8) + ": -" + FormatUtil.double2StrAmt(new Double(requestObj.getRefundAmt()))).append(formatLCEnd).append(formatEnter);
			stringBuilder.append(formatSCStart).append(mContext.getString(R.string.mobile_tranSucc)).append(formatSCEnd).append(formatEnter);
			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr16) + ":" + json.optString("PayerID")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr11) + ":" + MobileUtil.getSoftposChannelName(json.getString("AccType"))).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr10) + ":").append("".equals(json.optString("TxnAnsTime"))?DateUtil.dateToStr(new Date(),"yyyy/MM/dd HH:mm:ss"):json.optString("TxnAnsTime")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr4) + ":" + mid).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr5) + ":" + tid).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr6) + ":" + requestObj.getCashierID()).append(formatNLEnd).append(formatEnter);
			//stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr20) + ":" + json.getString("ChannelTxnNo")).append(formatNLEnd).append(formatEnter);
			stringBuilder.append(formatNCStart).append(mContext.getString(R.string.mobile_printStr9)).append(formatNCEnd).append(formatEnter);
			stringBuilder.append(formatNCBRStart).append(json.getString("OrgPlatformTxnNo")).append(formatNCBREnd).append(formatEnter);
//			stringBuilder.append(formatNCStart).append(json.getString("OrgPlatformTxnNo")).append(formatNCEnd).append(formatEnter);
			stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);

			if (i == 0) {
				stringBuilder.append(formatNLStart).append(mContext.getString(R.string.mobile_printStr14) + ":").append(formatNLEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatLCStart).append("           ").append(formatLCEnd).append(formatEnter);
				stringBuilder.append(formatSCStart).append("----------------------------------------------").append(formatSCEnd).append(formatEnter);
				stringBuilder.append(formatEnter);
				stringBuilder.append(formatCutpaper);
			} else {
				stringBuilder.append(formatEnter);
			}
		}

		log.info("WritePrnFile="+stringBuilder.toString());
		return stringBuilder.toString();
	}


}
