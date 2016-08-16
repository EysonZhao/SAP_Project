package sme.perf.utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

//import org.apache.log4j.ConsoleAppender;
//import org.apache.log4j.FileAppender;
//import org.apache.log4j.Layout;
//import org.apache.log4j.Level;
//import org.apache.log4j.PatternLayout;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class LogHelper {
	public static final Logger logger = Logger.getLogger(LogHelper.class); 
	
	public static void info(Exception e){
		logger.info(getExceptionStackTrace(e));
	}
	
	public static void info(String msg){
		logger.info(msg);
	}
	
	public static void debug(Exception e){
		logger.debug(getExceptionStackTrace(e));
	}
	
	public static void debug(String msg){
		logger.debug(msg);
	}
	
	public static void error(Exception e){
		logger.error(getExceptionStackTrace(e));
	}

	public static void error(String msg){
		logger.error(msg);
	}
		
	///enable to print the exception stack trace to log
	private static String getExceptionStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static Logger getLogger(){
		return logger;
	}
	
	public static Logger getLogger(String name, Level level, String logFileName){
		Logger logger = Logger.getLogger(name);
		logger.setLevel(level);
		int pos = logFileName.lastIndexOf(File.separator);
		String parentFolder = logFileName.substring(0, pos);
		File file = new File(parentFolder);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(logFileName);
		try {
			logger.removeAllAppenders();
			Layout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%t] %m%n");
			FileAppender appender = new FileAppender(patternLayout, file.getAbsolutePath(), false);
			
			logger.addAppender(appender);
			logger.addAppender(new ConsoleAppender(patternLayout));
		} catch (IOException e) {
			LogHelper.error(e);
		}
		return logger;
	}
}
