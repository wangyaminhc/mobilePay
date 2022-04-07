/**
 * Copyright  2008 SilverStone Computer System Co.,Ltd
 *
 * History:
 *   2015-8-12 下午1:28:52 Created by dingwm
 */
package com.sssoft_lib.mobile.util;

import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

/**
 * http代理
 * @author <a href="mailto:dingwm@ss-soft.com">dingwm</a>
 * @version 1.0  2015-8-12 下午1:28:52
 */
public class HttpProxy {
	//返回数据编码格式  
	private static String encoding = "UTF-8";
	
	
	/*//获得HttpClient
	private static DefaultHttpClient getHttpClient(Context mContext,URI uri,Boolean isSSL) throws Exception{
		DefaultHttpClient client = new DefaultHttpClient();
//		if(isSSL){
//			//获得密匙库,加载到系统受信任证书
//			KeyStore trustStore = KeyStore.getInstance("truststore");
//			//密匙库的密码
//			trustStore.load(mContext.getResources().getAssets().open(Constant.TRUST_STORE_FILE_NAME), Constant.TRUST_STORE_PASSWORD.toCharArray());
//			//注册密匙库
//			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
//			Scheme sch = new Scheme(uri.getScheme(), socketFactory, 443);
//			client.getConnectionManager().getSchemeRegistry().register(sch);
//		}
		//设置连接等待超时时间
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,Constant.CONN_TIMEOUT*1000);
		//设置读取等待超时时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,Constant.SO_TIMEOUT*1000);
		return client;
	}*/
	//获得HttpClient
	private static DefaultHttpClient getHttpClient(Context mContext,URI uri,Boolean isSSL) throws Exception{
		DefaultHttpClient client = new DefaultHttpClient();
		if(isSSL){
//			KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//			//FileInputStream instream = new FileInputStream(new File("d:/client.truststore"));
//			//密匙库的密码
//			//trustStore.load(instream,"888888".toCharArray());
//			trustStore.load(mContext.getResources().getAssets().open("client.truststore"), "888888".toCharArray());
//			//注册密匙库
//			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
//                        //忽略证书验证 - ALLOW_ALL_HOSTNAME_VERIFIER
//			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//			Scheme sch = new Scheme(uri.getScheme(), socketFactory, 443);
			client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", SSLTrustAllSocketFactory.getSocketFactory(), 443));
		}
		//设置连接等待超时时间
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,Constant.CONN_TIMEOUT*1000);
		//设置读取等待超时时间
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,Constant.SO_TIMEOUT*1000);
		return client;
	}
	
	/*public static String doRequest(String url,String content) throws Exception{
		Log.i("info", "Https request content:" + content);
		return doRequest(null,url,content,false);
	}*/
	public static String doRequest(Context mContext,String url,String content) throws Exception{
		//MobileUtil.Log("i", "doRequest", "Https request content:" + content);
		return doRequest(mContext,url,content,false);
	}
	
	
	public static String doRequest(Context mContext,String url,String content,Boolean isSSL) throws Exception{
		Log.i("url", url);
		HttpPost httpPost = new HttpPost(url);  
		//设定直接post的数据内容
		if(content!=null && !content.trim().equals("")){
			HttpEntity entity = new StringEntity(content,encoding);
			httpPost.setEntity(entity);
		}
		DefaultHttpClient client = null;
		try{
			client = getHttpClient(mContext,httpPost.getURI(),isSSL);
            HttpResponse httpResponse = client.execute(httpPost);  
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null){
            	String respContent = EntityUtils.toString(httpEntity,encoding);
            	Log.i("info", "Https response content:" + respContent);
            	return respContent;
            }
        }finally {
            //关闭连接，释放资源 
        	if(client != null)
        		client.getConnectionManager().shutdown();  
        }  
        return null;
	} 
}
