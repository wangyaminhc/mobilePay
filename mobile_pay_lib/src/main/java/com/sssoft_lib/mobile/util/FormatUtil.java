/**
 * Copyright  2008 SilverStone Computer System Co.,Ltd
 *
 * History:
 *   2015-1-23 上午9:49:50 Created by dingwm
 */
package com.sssoft_lib.mobile.util;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 格式化
 * @author <a href="mailto:dingwm@ss-soft.com">dingwm</a>
 * @version 1.0  2015-1-23 上午9:49:50
 */
public class FormatUtil {
	
	//替换换行符（页面显示）
	public static String fmtWrap(String cont){
		if(cont == null || cont.trim().equals(""))
			return cont;
		return cont.replace("\r\n", "<br/>").replace("\n", "<br/>").replace("\r", "<br/>");
	}
	
	//转成M格式
	public static Double int2M(Integer size){
		return int2K(size)/1024;
	}

	//转成K格式
	public static Double int2K(Integer size){
		return size.doubleValue()/1000;
	}
	
	//格式化金额，默认保留2位小数
	public static String double2StrAmt(Double amt){
		BigDecimal formatAmt = new BigDecimal(amt);
		formatAmt = formatAmt.setScale(2,BigDecimal.ROUND_HALF_UP);
		return formatAmt.toString();
	}
	
	//格式化金额，scale位小数
	public static String double2Str(Double amt, int scale){
		BigDecimal formatAmt = new BigDecimal(amt);
		formatAmt = formatAmt.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return formatAmt.toString();
	}
	/**
     * 补齐不足长度
     * @param length 长度
     * @param number 数字
     * @return
     */
	public static String lpad(int length, int number) {
        String f = "%0" + length + "d";
        return String.format(f, number);
    }
	
	public static String formatAmtFloat(String amt){
		if(amt == null || amt.trim().equals(""))
			return amt;
		return amt.replace( "," , "" );
	}
	
	//把金额格式转化成分为单位的格式
	public static String formatAmt2Fen(String amt){
		if(amt == null || amt.trim().equals(""))
			return amt;
		return amt.replace( "," , "" ).replace(".", "");
	}
	
	public static Double str2DoubleAmt(String amt){
		if(amt == null || amt.trim().equals(""))
			return new Double(0);
		return new Double(amt.replace(",", ""));
	}
	
	
	public static Double double2Double(Double amt,int newScale){
		BigDecimal formatAmt = new BigDecimal(amt);
		formatAmt = formatAmt.setScale(newScale,BigDecimal.ROUND_HALF_UP);
		return formatAmt.doubleValue();
	}
	
	//定长转成string型金额
	public static String fixedLength2StrAmt(String amt){
		if(amt==null || amt.trim().equals("")){
			return "0.00";
		}
		BigDecimal formatAmt = new BigDecimal(new Double(amt)/100);
		formatAmt = formatAmt.setScale(2,BigDecimal.ROUND_HALF_UP);
		return formatAmt.toString();
	}
	
	//yyyy-MM-dd格式的字符串日期转化成yyyyMMdd格式的
	public static String strDateFormat(String strDate){
		if(strDate==null) return null;
		else if(strDate.trim().equals("")) return "";
		else return strDate.replace("-", "");
	}
	
	
	public static String strDateAddFormat(String strDate){
		if(strDate==null) return null;
		else if(strDate.trim().equals("")) return "";
		return DateUtil.dayAddToStr(DateUtil.strToDate(strDate,"yyyy-MM-dd"),1,"yyyy-MM-dd");
	}
	
	
	public static String strToStr(String str){
		if(str==null)
			return null;
		else if(str.trim().equals(""))
			return "";
		else
			return str.replace("-", "");
	}
	
	//取得校验位
	public static String getPanLrc( String card ){
		int i,sum,len;
		int ch;	   
		String WEIGHT="121212121212121212";
		if( card.length() > 18 ) return "";
		len = 18 - card.length(); 
		for( sum=0,i=0; i<card.length(); i++ ){
			int tmp = ( card.charAt(i)-'0' )* (WEIGHT.charAt(i+len)-'0');
			sum += ( tmp % 10 ) +( tmp / 10 );
		}
		ch=(10-sum%10)%10; // (int)'0';
		return ""+ch;
	} 
	
	
	public static String addDay(String dateStr,int amount){
		String dateFormat = "yyyy-MM-dd";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.strToDate(dateStr, dateFormat));
		calendar.add(Calendar.DAY_OF_MONTH,amount);
		return DateUtil.dateToStr(calendar.getTime(), dateFormat);
	}
	
	
	//计算尾卡号
	public static String estimateEndPan(String startPan,Integer cardNum){
		if(cardNum.equals(1)){
			return startPan;
		}
		Long pan = new Long(startPan.substring(0,startPan.length()-1));
		pan = pan+cardNum-1;
		return ""+pan + getPanLrc(pan.toString());
	}
	//计算卡号
	public static String getEndPan(String startPan,Integer cardNum){
		if(cardNum.equals(1)){
			return startPan;
		}
		return estimateEndPan(startPan,cardNum);
	}
	
	/**
	 * @param len	预定格式总长度
	 * @param ch	右补字符
	 * @param str	当前字符串
	 * @return
	 */
	public static String append(int len,String ch,String str){
		StringBuffer temp=new StringBuffer();
		String result="";
		if(str.getBytes().length>=len){
			return str;
		}
		else{
			for(int i=0;i<len-str.getBytes().length;i++){
				temp.append(ch);
			}
			result =  str + temp;
		}
		return result;
	}
	public static String formatAmt(String txnAmt){
		String amt = FormatUtil.formatAmt2Fen(txnAmt);
		StringBuffer temp=new StringBuffer();
		for(int i=0;i<12-amt.getBytes().length;i++){
			temp.append("0");
		}
		amt = temp.append(amt).toString();
		return amt;
	}
	/**
	 * 将单位为元的金额装换成单位为分的金额
	 * @param yuan 输入单位元的金额
	 * @return 输出单位为分的金额
	 */
	public static String getYuanToFen(String yuan) {
		try {
			double d = Double.parseDouble(yuan);
			return String.valueOf((int)(d*100));
		} catch (Exception e) {
			return "0";
		}
		
	}
}
