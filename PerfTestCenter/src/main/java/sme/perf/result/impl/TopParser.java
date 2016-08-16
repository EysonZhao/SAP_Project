package sme.perf.result.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.result.entity.ResultSession;
import sme.perf.result.entity.TopHeader;
import sme.perf.result.entity.TopSubLine;

public class TopParser implements IParseResult<TopHeader> {
    private Logger logger;
    private final ResultSession resultSession;
    private final Pattern datePattern = Pattern.compile("\\d{4}_\\d+_\\d+");
    private final static Pattern timePattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy_MM_dd");
    
    private final Pattern fileDatePattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}_\\d+_\\d+_\\d+");
    private final static DateTimeFormatter fileDateTimeFormatter = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm_ss");
    private Pattern filterPattern = null;

    private DateTime lastRecordDateTime = null;
    private DateTime fileRecordDateTime = null;
    private static int diffSeconds=0;
    
    private final Pattern cpuUserPattern = Pattern.compile("(\\d+.\\d+)\\Wus");
    private final Pattern cpuSystemPattern = Pattern.compile("(\\d+.\\d+)\\Wsy");
    private final Pattern cpuNicePattern = Pattern.compile("(\\d+.\\d+)\\Wni");
    private final Pattern cpuIdlePattern = Pattern.compile("(\\d+.\\d+)\\Wid");
    
    private final Pattern memoryTotalPattern = Pattern.compile("(\\d+)([tgmk]?) total");
    private final Pattern memoryUsedPattern = Pattern.compile("(\\d+)([tgmk]?) used");
    private final Pattern memoryFreePattern = Pattern.compile("(\\d+)([tgmk]?) free");
    private final Pattern memoryCachedPattern = Pattern.compile("(\\d+)([tgmk]?) cached");
    Boolean isFirst=true;
    int nMemoryUnit=1;
    int nDaysOffset=0;
    
    public TopParser(ResultSession session,Logger logger) {
        this.resultSession = session;
        this.logger=logger;
    }
    
    public TopParser(ResultSession session,Logger logger,String filterString) {
        this.resultSession = session;
        this.logger=logger;
        if (filterString != null && filterString.length() > 0) {
            filterPattern = Pattern.compile(filterString);
        }
    }

    @Override
    public List<TopHeader> parse(String fileName) throws Exception {
        File f = new File(fileName);
        String shortFileName = f.getName();

        String hostName = shortFileName.substring(0, shortFileName.indexOf('_'));

        Matcher filematcher = fileDatePattern.matcher(shortFileName);
        if (filematcher.find()) {
        	fileRecordDateTime = fileDateTimeFormatter.parseDateTime(filematcher.group(0));
        }
        else{
        	throw new Exception(String.format("File Name (%s) format is not matched the pattern (%s)", shortFileName, fileDatePattern.pattern()));
        }        
        Matcher matcher = datePattern.matcher(shortFileName);
        if (matcher.find()) {
            lastRecordDateTime = dateFormatter.parseDateTime(matcher.group(0));
        }
        
        List<String> bufferList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        boolean isTopHeaderStart = false;
        boolean isTopHeaderEnd = false;
        boolean isSubLineStart = false;
        boolean isSubLineEnd = false;
        List<TopHeader> retHeaderList = new ArrayList<TopHeader>();
        TopHeader newTopHeader = null;
        List<TopSubLine> newSubLines = null;

        int nCurrentLineNum = 0;
        try {
            String newLine = null;
			while ((newLine = reader.readLine()) != null) {
                nCurrentLineNum++;
                // 2. find the start and end of the header           
                if (newLine.trim().startsWith("top")) {
                    isTopHeaderStart = true;
                }
                if (newLine.trim().startsWith("PID")) {
                    isSubLineStart = true;
                }
                if (newLine.trim().length() < 1)    // new breaker
                {
                    if (isTopHeaderStart == true) {
                        isTopHeaderEnd = true;
                    }
                    if (isSubLineStart == true) {
                        isSubLineEnd = true;
                    }
                } else {
                    bufferList.add(newLine);
                }

                if (isTopHeaderEnd == true && isTopHeaderStart == true) {
                    newTopHeader = parseTopHeader(bufferList, hostName);
                    retHeaderList.add(newTopHeader);
                    isTopHeaderEnd = false;
                    isTopHeaderStart = false;
                    bufferList = new ArrayList<String>();
                }

                if (isSubLineEnd == true && isSubLineStart == true) {
                    newSubLines = parseTopSubline(bufferList, newTopHeader,nMemoryUnit);
                    for (TopSubLine subline : newSubLines) {
                        newTopHeader.getTopSubLines().add(subline);
                    }
                    isSubLineStart = false;
                    isSubLineEnd = false;
                    bufferList = new ArrayList<String>();
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("Parse Top result error @line: %d", nCurrentLineNum), ex);
            reader.close();
            throw ex;
        }

        reader.close();
        logger.info(String.format("%d Top records are parsed from file %s", retHeaderList.size(), fileName));
        return retHeaderList;
    }

    private List<TopSubLine> parseTopSubline(List<String> bufferList, TopHeader topHeader, int unit) {
        List<TopSubLine> retList = new ArrayList<TopSubLine>();
        int indexPID = 0;
        int indexUserName = 1;
        int indexVIRTMemory = 4; // VIRTmemory
        int indexMemory = 5;
        int indexCPU = 8;
        int indexCommand = 11;

        for (int i = 1; i < bufferList.size(); i++) {
            String newLine = bufferList.get(i).trim();
            String temp = newLine.replaceAll("  ", " ");
            while (temp.length() < newLine.length()) {	// trim duplicated space characters
                newLine = temp;
                temp = newLine.replaceAll("  ", " ");
            }

            String[] values = newLine.split(" ");

            StringBuilder cmdSB = new StringBuilder();
            for (int j = indexCommand; j < values.length; j++) {
                cmdSB.append(values[j].trim() + " ");
            }
            String strCmd = cmdSB.toString();

            boolean isMatched = true;
            if (filterPattern != null) {
                Matcher matcher = filterPattern.matcher(strCmd);
                isMatched = matcher.find();
            }
            
            //W
            if (isMatched) {
                long processID = Long.parseLong(values[indexPID]);
                long VIRTmemory = -1;
                long memory = -1;
                String strVIRTMemory = values[indexVIRTMemory];
                String strMemory = values[indexMemory];
                if (strVIRTMemory.toLowerCase().contains("k")) {
                    VIRTmemory = (long) (Double.parseDouble(strVIRTMemory.substring(0, strVIRTMemory.length() - 1)) * 1024);
                } else if (strVIRTMemory.toLowerCase().contains("m")) {
                    VIRTmemory = (long) (Double.parseDouble(strVIRTMemory.substring(0, strVIRTMemory.length() - 1)) * 1048576);
                } else if (strVIRTMemory.toLowerCase().contains("g")) {
                    VIRTmemory = (long) (Double.parseDouble(strVIRTMemory.substring(0, strVIRTMemory.length() - 1)) * 1073741824);
                } else if (strVIRTMemory.toLowerCase().contains("t")){
                	VIRTmemory = (long) (Double.parseDouble(strVIRTMemory.substring(0, strVIRTMemory.length() - 1)) * 1073741824 * 1024);
                }else {
                	VIRTmemory = (long) (Double.parseDouble(strVIRTMemory) * unit);
//                    switch(unit){
//                		case 1:   
//                			VIRTmemory = (long) (Double.parseDouble(strVIRTMemory) * 1024);
//                			break;
//                		case 2:
//                			VIRTmemory = (long) (Double.parseDouble(strVIRTMemory) * 1048576);
//                			break;
//                		case 3:
//                			VIRTmemory = (long) (Double.parseDouble(strVIRTMemory) * 1073741824);
//                			break;
//                		default:
//                			VIRTmemory = Long.parseLong(strVIRTMemory);
//                			break;
//                    }
                }	

                if (strMemory.toLowerCase().contains("k")) {
                    memory = (long) (Double.parseDouble(strMemory.substring(0, strMemory.length() - 1)) * 1024);
                } else if (strMemory.toLowerCase().contains("m")) {
                    memory = (long) (Double.parseDouble(strMemory.substring(0, strMemory.length() - 1)) * 1048576);
                } else if (strMemory.toLowerCase().contains("g")) {
                    memory = (long) (Double.parseDouble(strMemory.substring(0, strMemory.length() - 1)) * 1073741824);
                } else if (strMemory.toLowerCase().contains("t")) {
                    memory = (long) (Double.parseDouble(strMemory.substring(0, strMemory.length() - 1)) * 1073741824 * 1024);
                } else {
                	memory = (long) (Double.parseDouble(strMemory) * unit);
//                    switch(unit){
//                		case 1:   
//                			memory = (long) (Double.parseDouble(strMemory) * 1024);
//                			break;
//                		case 2:
//                			memory = (long) (Double.parseDouble(strMemory) * 1048576);
//                			break;
//                		case 3:
//                			memory = (long) (Double.parseDouble(strMemory) * 1073741824);
//                			break;
//                		default:
//                			memory = Long.parseLong(strMemory);
//                			break;
//                    }
                }
               
                TopSubLine newSubLine = new TopSubLine();
                newSubLine.setCommand(strCmd.trim());
                newSubLine.setCpu(Double.parseDouble(values[indexCPU]));
                newSubLine.setMemory(memory);
                newSubLine.setProcessId(processID);
                newSubLine.setTopHeader(topHeader);
                newSubLine.setUserName(values[indexUserName]);
                newSubLine.setVirtMemory(VIRTmemory);
                retList.add(newSubLine);
            }
        }
        return retList;
    }

    private TopHeader parseTopHeader(List<String> bufferList, String hostName) throws Exception {
        Matcher matcher = timePattern.matcher(bufferList.get(0));
        DateTime tempDateTime = null;
        if (matcher.find()) {
            String strDateTime = String.format("%d-%d-%d %s", lastRecordDateTime.getYear(),
                    lastRecordDateTime.getMonthOfYear(), lastRecordDateTime.getDayOfMonth(), matcher.group(0));
            tempDateTime = dateTimeFormatter.parseDateTime(strDateTime);
            if(isFirst){
        		Duration d = new Duration(fileRecordDateTime, tempDateTime);  
        		diffSeconds = (int) d.getStandardSeconds();
        		diffSeconds=-1*diffSeconds;
                isFirst=false;
                
                //fix KiB/MB/GB/TB Mem issue
                if (bufferList.get(4).toLowerCase().startsWith("s")){
                	nMemoryUnit=1;
                }else if (bufferList.get(4).toLowerCase().startsWith("k")){
                	nMemoryUnit=1024;
                }else if (bufferList.get(4).toLowerCase().startsWith("m")){
                	nMemoryUnit=1024* 1024;
                }else if (bufferList.get(4).toLowerCase().startsWith("g")){
                	nMemoryUnit=1024 * 1024 * 1024;
                }
            }  
            if (null == lastRecordDateTime) {
            	lastRecordDateTime = tempDateTime;
            }
            if (lastRecordDateTime.isAfter(tempDateTime)) {
                nDaysOffset++;
            }
            lastRecordDateTime = tempDateTime;
        }

        int cpuLoadStartIndex = bufferList.get(0).indexOf("average: ") + "average: ".length();
        int cpuLoadEndIndex = bufferList.get(0).indexOf(",", cpuLoadStartIndex);
        TopHeader newHeader = new TopHeader();
        newHeader.setCpuLoad(Double.parseDouble(bufferList.get(0).substring(cpuLoadStartIndex, cpuLoadEndIndex)));
        newHeader.setDate(tempDateTime.plusDays(nDaysOffset).plusSeconds(diffSeconds));
        newHeader.setHostName(hostName);
        newHeader.setResultSession(resultSession);
        
        newHeader.setCpuUser(parseCpuDouble(bufferList.get(2), cpuUserPattern));
        newHeader.setCpuSystem(parseCpuDouble(bufferList.get(2), cpuSystemPattern));
        newHeader.setCpuNice(parseCpuDouble(bufferList.get(2), cpuNicePattern));
        newHeader.setCpuIdle(parseCpuDouble(bufferList.get(2), cpuIdlePattern));
        
        newHeader.setMemoryTotal(parseMemoryLong(bufferList.get(3), memoryTotalPattern));
        newHeader.setMemoryUsed(parseMemoryLong(bufferList.get(3), memoryUsedPattern));
        newHeader.setMemoryFree(parseMemoryLong(bufferList.get(3), memoryFreePattern));
        newHeader.setMemoryCached(parseMemoryLong(bufferList.get(4), memoryCachedPattern));
        return newHeader;
    }
    private double parseCpuDouble(String input, Pattern p) throws Exception{
    	Matcher m = p.matcher(input.toLowerCase());
    	double value = 0.0;
    	if(m.find()){
    		String strValue = m.group(1);
    		value = Double.parseDouble(strValue);
    	}
    	else{
    		throw new Exception(String.format("cannot match the pattern (%s) in string (%s)", p.pattern(), input));
    	}
    	return value;
    }
    
    private long parseMemoryLong(String input, Pattern p) throws Exception{
    	//fix '+' issue like: KiB Mem:  25213724+total, 12066348 used, 24007089+free,    64896 buffers
    	input = input.replace("+", "0 ");
    	
    	Matcher m = p.matcher(input.toLowerCase());
    	long value = 0;
    	if(m.find()){
    		String strValue = m.group(1);
    		String strUnit = m.group(2);
    		value = Long.parseLong(strValue);
    		switch(strUnit){
    		case "t": 
    			value *= 1024 * 1024 * 1024 * 1024;
    			break;
    		case "g":
    			value *= 1024 * 1024 * 1024;
    			break;
    		case "m":
    			value *= 1024 * 1024;
    			break;
    		case "k":
    			value *= 1024;
    			break;
    		default: 
    			value *= nMemoryUnit;
    			break;
    		}
    	}
    	else{
    		throw new Exception(String.format("cannot match the pattern (%s) in string (%s)", p.pattern(), input));
    	}
    	return value;
    }
}
