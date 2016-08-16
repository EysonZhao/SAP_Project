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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.result.entity.DockerStatsHeader;
import sme.perf.result.entity.DockerStatsSubLine;
import sme.perf.result.entity.ResultSession;

public class DockerStatsParser implements IParseResult<DockerStatsHeader> {
    private static final int RecordIntervalInSeconds = 1;
    private Logger logger;
    private final ResultSession resultSession;
    private final Pattern datePattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}_\\d+_\\d+_\\d+");
    private final Pattern floatValuePattern = Pattern.compile("\\d+\\.{0,1}\\d{0,3}");
    private final Pattern servicePattern = Pattern.compile("_\\w*_TOP_");

    //private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm_ss");
    // private final static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy_MM_dd");
    // private Pattern filterPattern = null;

    private DateTime lastRecordDateTime = null;

    public DockerStatsParser(ResultSession session,Logger logger) {
        this.resultSession = session;
        this.logger=logger;
    }
    
    @Override
    public List<DockerStatsHeader> parse(String fileName) throws Exception {
        File f = new File(fileName);
        String shortFileName = f.getName();
        String service="";
        String hostName = shortFileName.substring(0, shortFileName.indexOf('_'));
        
        Matcher matcher = datePattern.matcher(shortFileName);
        if (matcher.find()) {
            lastRecordDateTime = dateTimeFormatter.parseDateTime(matcher.group(0));
        }
        Matcher serviceMatcher = servicePattern.matcher(shortFileName);
        if (serviceMatcher.find()) {
        	service = serviceMatcher.group(0).substring(1,serviceMatcher.group(0).indexOf("_TOP_"));
        }
        
        List<String> bufferList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        List<DockerStatsHeader> headerList = new ArrayList<DockerStatsHeader>();
        DockerStatsHeader newDockerStatsHeader = null;

        int nCurrentLineNum = 0;
        int bufferBlock = 0;
        try {
            String newLine = null;
            while ((newLine = reader.readLine()) != null) {
                nCurrentLineNum++;
                // check whether new block starts
                if (nCurrentLineNum%4==0||nCurrentLineNum%4==3){
                	 if (newLine.trim().contains("HCONTAINER"))    
                     {
                         // block ends, process the block
                         if(bufferList.size() > 0){
                             //create new header
                             newDockerStatsHeader = new DockerStatsHeader();
                             headerList.add(newDockerStatsHeader);
//                             resultSession.getDockerStatsHeaders().add(newDockerStatsHeader);
                             newDockerStatsHeader.setDate(lastRecordDateTime);
                             newDockerStatsHeader.setHostName(hostName);
                             newDockerStatsHeader.setResultSession(resultSession);
                             newDockerStatsHeader.setService(service);
                             //parse sub lines
                             parseDockerSubLine(newDockerStatsHeader, bufferList, hostName, bufferBlock);
                             
                             //prepare for next record
                             lastRecordDateTime = lastRecordDateTime.plusSeconds(RecordIntervalInSeconds);
                             bufferList = new ArrayList<String>();
                         }
                     }
                     else {
                         bufferList.add(newLine);
                     }
                }
            }   //process the last block
            if(bufferList.size() > 0){
                newDockerStatsHeader = new DockerStatsHeader(); 
//                resultSession.getDockerStatsHeaders().add(newDockerStatsHeader);
                headerList.add(newDockerStatsHeader);
                newDockerStatsHeader.setDate(lastRecordDateTime);
                newDockerStatsHeader.setHostName(hostName);
                newDockerStatsHeader.setResultSession(resultSession);
                parseDockerSubLine(newDockerStatsHeader, bufferList, hostName, bufferBlock);

            }
        } catch (Exception ex) {
            logger.error(String.format("Parse Docker Stats result error @line: %d", nCurrentLineNum), ex);
            reader.close();
            throw ex;
        }
        reader.close();
        logger.info(String.format("%d Docker Stats records are parsed from file %s", headerList.size(), fileName));
        return headerList;
    }

    private List<DockerStatsSubLine> parseDockerSubLine(DockerStatsHeader header, List<String> bufferList, String hostName, int bufferBlock) {
        for (int i = 0; i < bufferList.size(); i++) {
            String newLine = bufferList.get(i);
            
            // trim duplicated space characters
            String temp = newLine.replaceAll("  ", " ");
            while (temp.length() < newLine.length()) {
                newLine = temp;
                temp = newLine.replaceAll("  ", " ");
            }
            String[] values = newLine.split(" ");
            
            DockerStatsSubLine newSubLine = new DockerStatsSubLine();
            newSubLine.setDockerId(values[0]);
            newSubLine.setCpuUsage(extractFloatValue(values[1]));
            newSubLine.setMemUsage(extractFloatValueWithUnit(values[2], values[3]));
            newSubLine.setMemLimit(extractFloatValueWithUnit(values[3], values[4]));
            newSubLine.setMemPercentage(extractFloatValue(values[5]));
            newSubLine.setNetIn(extractFloatValueWithUnit(values[6], values[7]));
            newSubLine.setNetOut(extractFloatValueWithUnit(values[7], values[8]));
            newSubLine.setDockerStatsHeader(header);
            
            header.getSubLines().add(newSubLine);
        }
        return header.getSubLines();
    }

    private float extractFloatValue(String str){
        Matcher matcher = floatValuePattern.matcher(str);
        try{
            matcher.find();
            return Float.parseFloat(matcher.group());
        }
        catch(Exception ex){
            logger.error(str);
            throw ex;
        }
    }

    private float extractFloatValueWithUnit(String strValue, String strUnit){
        float retValue = extractFloatValue(strValue);
        strUnit = strUnit.toLowerCase();
        if(strUnit.contains("k")){
            retValue *= 1024;
        }
        else if(strUnit.contains("m")){
            retValue *= 1024 * 1024;
        }
        else if(strUnit.contains("g")){
            retValue *= 1024 * 1024 * 1024;
        }
        return retValue;
    }
}
