/**
 * Copyright  2008 SilverStone Computer System Co.,Ltd
 *
 * History:
 *   2009-9-10 下午12:46:24 Created by huangzh
 */
package com.sssoft_lib.mobile.util;

import java.math.BigDecimal;

/**
 * 类说明
 * @author <a href="mailto:huangzh@ss-soft.com">huangzh</a>
 * @version 1.0  2009-9-10 下午12:46:24
 */
public class Arith {
	 //默认除法运算精度   
    private   static   final   int   DEF_DIV_SCALE   =   10;   


    //这个类不能实例化   
    private   Arith(){   
    }   


    /**   
      *   提供精确的加法运算。   
      *   @param   v1   被加数   
      *   @param   v2   加数   
      *   @return   两个参数的和   
      */   

    public   static   double   add(double   v1,double   v2){   
            BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));   
            BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));   
            return   b1.add(b2).doubleValue();   
    }   

    /**   
      *   提供精确的减法运算。   
      *   @param   v1   被减数   
      *   @param   v2   减数   
      *   @return   两个参数的差   
      */   

    public   static   double   sub(double   v1,double   v2){   
            BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));   
            BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));   
            return   b1.subtract(b2).doubleValue();   
    }     

    /**   
      *   提供精确的乘法运算。   
      *   @param   v1   被乘数   
      *   @param   v2   乘数   
      *   @return   两个参数的积   
      */   

    public   static   double   mul(double   v1,double   v2){   
            BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));   
            BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));   
            return   b1.multiply(b2).doubleValue();   
    }   
    /**   
     *   提供精确的乘法运算。   
     *   @param   v1   乘数   
     *   @param   v2   被乘数 
     *   @param   v3   被乘数   
     *   @param   v4   被乘数  
     *   @return   四个参数的积   
     */   
   public   static   double   mulFour(double   v1,double   v2,double   v3,double   v4){   
           BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));   
           BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));  
           BigDecimal   b3   =   new   BigDecimal(Double.toString(v3));   
           BigDecimal   b4   =   new   BigDecimal(Double.toString(v4));   
           return   ((b1.multiply(b2)).multiply(b3)).multiply(b4).doubleValue();   
   } 


    /**   
      *   提供（相对）精确的除法运算，当发生除不尽的情况时，精确到   
      *   小数点以后10位，以后的数字四舍五入。   
      *   @param   v1   被除数   
      *   @param   v2   除数   
      *   @return   两个参数的商   
      */   

    public   static   double   div(double   v1,double   v2){   
            return   div(v1,v2,DEF_DIV_SCALE);   
    }   



    /**   
      *   提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指   
      *   定精度，以后的数字四舍五入。   
      *   @param   v1   被除数   
      *   @param   v2   除数   
      *   @param   scale   表示表示需要精确到小数点以后几位。   
      *   @return   两个参数的商   
      */   

    public   static   double   div(double   v1,double   v2,int   scale){   
            if(scale<0){   
                    throw   new   IllegalArgumentException(   
                            "The   scale   must   be   a   positive   integer   or   zero");   
            }   
            BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));   
            BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));   
            return   b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
    }   



    /**   
      *   提供精确的小数位四舍五入处理。   
      *   @param   v   需要四舍五入的数字   
      *   @param   scale   小数点后保留几位   
      *   @return   四舍五入后的结果   
      */   

    public   static   double   round(double   v,int   scale){   
            if(scale<0){   
                    throw   new   IllegalArgumentException(   
                            "The   scale   must   be   a   positive   integer   or   zero");   
            }   
            BigDecimal   b   =   new   BigDecimal(Double.toString(v));   
            BigDecimal   one   =   new   BigDecimal("1");   
            return   b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();   
    }   
  //定义一个处理字符串的方法
    public static String getMoneyString(String money){ 
         String overMoney = "";//结果  
         String[] pointBoth = money.split("\\.");//分隔点前点后    
         String beginOne = pointBoth[0].substring(pointBoth[0].length()-1);//前一位    
         String endOne = pointBoth[1].substring(0, 1);//后一位  
        //小数点前一位前面的字符串，小数点后一位后面 
         String beginPoint = pointBoth[0].substring(0,pointBoth[0].length()-1);   
         String endPoint = pointBoth[1].substring(1);   
         //Log.e("Sun", pointBoth[0]+"==="+pointBoth[1] + "====" + beginOne + "=======" + endOne+"===>"+beginPoint+"=="+endPoint ); 
        //根据输入输出拼点  
         if (pointBoth[1].length()>2){//说明输入，小数点要往右移    
               overMoney=  pointBoth[0]+endOne+"."+endPoint;//拼接实现右移动    
         }else if (
               pointBoth[1].length()<2){//说明回退,小数点左移        
               overMoney = beginPoint+"."+beginOne+pointBoth[1];//拼接实现左移  
         }else {
                 overMoney = money;   
            }   
       //去除点前面的0 或者补 0    
       String overLeft = overMoney.substring(0,overMoney.indexOf("."));//得到前面的字符串 
       //Log.e("Sun","左邊:"+overLeft+"===去零前"+overMoney); 
       if (overLeft ==null || overLeft == ""||overLeft.length()<1){//如果没有就补零        
         overMoney = "0"+overMoney; 
       }else if(overLeft.length() > 1 && "0".equals(overLeft.subSequence(0, 1))){//如果前面有俩个零      
         overMoney = overMoney.substring(1);//去第一个0  
       }    
       //Log.e("Sun","結果:"+overMoney);  
       return overMoney;
    }
}
