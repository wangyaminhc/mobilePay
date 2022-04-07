package cn.darkranger.log.log4j_lib;

import android.os.Environment;

import com.sssoft_lib.mobile.util.Constant;

import org.apache.log4j.Level;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogUtil 工具类
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class LogUtil {
	private static String fileName;
	
	/** 这里的AppName决定log的文件位置和名称 **/
	private static final String APP_NAME = "sssoft/log";

	/** 设置log文件全路径，这里是 MyApp/Log/myapp.log **/
	private static String LOG_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + APP_NAME + File.separator;

	/**
	 *  ### log文件的格式
	 *  
	 * 	### 输出格式解释：
	 *	### [%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n
	 *	
	 *	### %d{yyyy-MM-dd HH:mm:ss}: 时间，大括号内是时间格式
	 *	### %c: 全类名
	 *	### %M: 调用的方法名称
	 *	### %F:%L  类名:行号（在控制台可以追踪代码）
	 *	###	%n: 换行
	 *	### %p: 日志级别，这里%-5p是指定的5个字符的日志名称，为的是格式整齐
	 *	### %m: 日志信息
		
	 *	### 输出的信息大概如下：
	 *	### [时间{时间格式}][信息所在的class.method(className：lineNumber)] 换行
	 *	###	[Level: 5个字符的等级名称] - Msg: 输出信息 换行
	 */
	private static final String LOG_FILE_PATTERN = "[%-d{yyyy-MM-dd HH:mm:ss}][Class: %c.%M(%F:%L)] %n[Level: %-5p] - Msg: %m%n";

	/** 生产环境下的log等级 **/
	private static final Level LOG_LEVEL_PRODUCE = Level.ALL;

	/** 发布以后的log等级 **/
	private static final Level LOG_LEVEL_RELEASE = Level.INFO;

	/**
	 * 配置log4j参数
	 */
	public static void configLog(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		fileName = sdf.format(new Date()); 
		LOG_FILE_PATH = LOG_FILE_PATH + Constant.FileName+ fileName + ".log";
		LogConfig logConfig = new LogConfig(LOG_FILE_PATH, 5, 1024 * 1024 * 10, LOG_FILE_PATTERN, Level.ERROR);
		/** 设置Log等级，生产环境下调用setLogToProduce()，发布后调用setLogToRelease() **/
		setLogToRelease(logConfig);
		
//		logConfig.setFileName(LOG_FILE_PATH);
//
//		logConfig.setLevel("org.apache", Level.ERROR);
//
//		logConfig.setFilePattern(LOG_FILE_PATTERN);
//
//		logConfig.setMaxFileSize(1024 * 1024 * 10);
//		logConfig.setMaxBackupSize(10);
//		logConfig.setImmediateFlush(true);

		logConfig.configure();
		String LOG_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + APP_NAME + File.separator;


	}

	/**
	 * 将log设置为生产环境
	 * 
	 * @param logConfig
	 */
	private static void setLogToProduce(LogConfig logConfig) {
		logConfig.setRootLevel(LOG_LEVEL_PRODUCE);
	}

	/**
	 * 将log设置为发布以后的环境
	 * 
	 * @param logConfig
	 */
	private static void setLogToRelease(LogConfig logConfig) {
		logConfig.setRootLevel(LOG_LEVEL_RELEASE);
	}
}
