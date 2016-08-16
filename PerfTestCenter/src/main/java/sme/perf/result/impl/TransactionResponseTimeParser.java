package sme.perf.result.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.result.entity.ResultSession;
import sme.perf.result.entity.TransactionResponseTime;
import sme.perf.result.entity.TransactionResponseTimeCache;

public class TransactionResponseTimeParser implements IParseResult<TransactionResponseTimeCache> {

    private Logger logger;
    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSS");// 2014-04-06
                                                                                                             // 19:52:48.6153
    //support old literunner format
    private final static DateTimeFormatter formatter2 = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");

    private final ResultSession resultSession;
    
    public TransactionResponseTimeParser(ResultSession session,Logger logger) {
        this.resultSession = session;
        this.logger=logger;
    }

    @Override
    public List<TransactionResponseTimeCache> parse(String fileName) throws Exception {
        File file = new File(fileName);
        String shortFileName = file.getName();
        String hostName = shortFileName.substring(0, shortFileName.indexOf('_'));

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<TransactionResponseTimeCache> retList = new ArrayList<TransactionResponseTimeCache>();

        int nCurrentLineNum = 0;
        try {
            String newLine = null;
            while ((newLine = reader.readLine()) != null) {
                nCurrentLineNum++;
                if (newLine.trim().length() < 1)
                    continue;

                TransactionResponseTimeCache newTR = ParseResult(newLine, hostName);
                retList.add(newTR);
                if (null != resultSession) {
//                    resultSession.getTransactionResponseTimes().add(newTR);
                    newTR.setResultSession(resultSession);
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("Parse Transaction result error @line: %d", nCurrentLineNum), ex);
            reader.close();
            throw ex;
        }
        reader.close();
        logger.info(String.format("%d records are parsed from file %s", retList.size(), fileName));
        return retList;
    }

    private TransactionResponseTimeCache ParseResult(String newLine, String hostName) {
        String[] rawResultProperties = newLine.split("\t");
    	for(int i=0;i<rawResultProperties.length;i++){
			rawResultProperties[i]=rawResultProperties[i].trim();
		}
        TransactionResponseTimeCache newRT = new TransactionResponseTimeCache();
        try{

        	newRT.setDate(new Timestamp((formatter.parseDateTime(rawResultProperties[0])).getMillis()));
            newRT.setResult(rawResultProperties[1]);
            newRT.setTransactionName(rawResultProperties[2]);
            newRT.setResponseTime(Double.parseDouble(rawResultProperties[3].replaceAll(",", "")));            
            //July 11 added by Yansong
            newRT.setThread_id(Thread.currentThread().getId());

            if(rawResultProperties.length>4){
        		float CPUUtil = Float.parseFloat(rawResultProperties[4].substring(rawResultProperties[4].indexOf(":")+2));
        		newRT.setCpuUtil(CPUUtil);
        		float CPUTime = Float.parseFloat(rawResultProperties[5].substring(rawResultProperties[5].indexOf(":")+2));
        		newRT.setCpuTime(CPUTime);
            }
            if(rawResultProperties.length>6){
        		String [] mem1 = rawResultProperties[6].split(" ");
        		long pfrom = Long.parseLong(mem1[1]);
        		long pto = Long.parseLong(mem1[3]);
        		
        		String [] mem2 = rawResultProperties[7].split(" ");
        		long pkfrom = Long.parseLong(mem2[1]);
        		long pkto = Long.parseLong(mem2[3]);
        		newRT.setMemPrivateFrom(pfrom);
        		newRT.setMemPrivateTo(pto);
        		newRT.setMemPeakFrom(pkfrom);
        		newRT.setMemPeakTo(pkto);
            }
            
        }catch(Exception ex){	//support old literunner format
        	newRT.setDate(new Timestamp(formatter2.parseDateTime(String.format("%s %s", rawResultProperties[0], rawResultProperties[1])).getMillis()));
            newRT.setResult(rawResultProperties[2]);
            newRT.setTransactionName(rawResultProperties[3]);
            newRT.setResponseTime(Double.parseDouble(rawResultProperties[5].substring(0, rawResultProperties[5].length() - 2).replaceAll(",", "")));
            newRT.setThread_id(Thread.currentThread().getId());

            if(rawResultProperties.length>4){
        		float CPUUtil = Float.parseFloat(rawResultProperties[4].substring(rawResultProperties[4].indexOf(":")+2));
        		newRT.setCpuUtil(CPUUtil);
        		float CPUTime = Float.parseFloat(rawResultProperties[5].substring(rawResultProperties[5].indexOf(":")+2));
        		newRT.setCpuTime(CPUTime);
            }
            if(rawResultProperties.length>6){
        		String [] mem1 = rawResultProperties[6].split(" ");
        		long pfrom = Long.parseLong(mem1[1]);
        		long pto = Long.parseLong(mem1[3]);
        		
        		String [] mem2 = rawResultProperties[7].split(" ");
        		long pkfrom = Long.parseLong(mem2[1]);
        		long pkto = Long.parseLong(mem2[3]);
        		newRT.setMemPrivateFrom(pfrom);
        		newRT.setMemPrivateTo(pto);
        		newRT.setMemPeakFrom(pkfrom);
        		newRT.setMemPeakTo(pkto);
            }
        }
        newRT.setHostName(hostName);
        return newRT;
    }
}
