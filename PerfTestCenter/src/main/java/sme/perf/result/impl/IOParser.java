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

import sme.perf.result.entity.IOHeader;
import sme.perf.result.entity.IOSubLine;
import sme.perf.result.entity.ResultSession;

public class IOParser implements IParseResult<IOHeader> {

    private Logger logger;
    private final Pattern datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{2}");
    private final Pattern timePattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
    private final Pattern ubuntudatePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
    private final Pattern ubuntutimePattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2} (A|P)M");
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yy HH:mm:ss");
    private final Pattern fileDatePattern = Pattern.compile("\\d{4}_\\d{2}_\\d{2}_\\d+_\\d+_\\d+");
    private final static DateTimeFormatter fileDateTimeFormatter = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm_ss");
    private final Pattern servicePattern = Pattern.compile("_\\w*_IO_");

    private final ResultSession resultSession;
    private DateTime fileRecordDateTime = null;
    private static int diffSeconds=0;
    
    public IOParser(ResultSession session,Logger logger) {
        this.resultSession = session;
        this.logger=logger;
    }
    
    @Override
    public List<IOHeader> parse(String fileName) throws Exception {
        File f = new File(fileName);
        String shortFileName = f.getName();
        String hostName = shortFileName.substring(0, shortFileName.indexOf('_'));
        String service="";
        
        Matcher filematcher = fileDatePattern.matcher(shortFileName);
        if (filematcher.find()) {
        	fileRecordDateTime = fileDateTimeFormatter.parseDateTime(filematcher.group(0));
        }
        Matcher serviceMatcher = servicePattern.matcher(shortFileName);
        if (serviceMatcher.find()) {
        	service = serviceMatcher.group(0).substring(1,serviceMatcher.group(0).indexOf("_IO_"));
        }
        
        List<String> bufferList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        List<IOHeader> retHeaderList = new ArrayList<IOHeader>();

        boolean isNewStart = false;
        int nNewStartEmptyLine = 0;
        int nCurrentLineNum = 0;

        try {
            String newLine = null;
            String strDate = null;
            while ((newLine = reader.readLine()) != null) {
                nCurrentLineNum++;

                if (newLine.trim().startsWith("Linux")) {
                    isNewStart = true;
                    nNewStartEmptyLine = 0;
                    // check whether match Ubuntu first
                    Matcher matcher = ubuntudatePattern.matcher(newLine);
                    if (matcher.find()) {
                        strDate = matcher.group(0);
                    } else {
                        matcher = datePattern.matcher(newLine);
                        if (matcher.find()) {
                            strDate = matcher.group(0);
                        }
                    }
                    // add the logic for the overnight case. nDaysOffset is the control value. clear the value once it
                    // is the new start
                    nDaysOffset = 0;
                }
                if (isNewStart == true) // Skip the newStarted result, identified by 2 empty lines
                {
                    if (newLine.trim().length() < 1) {
                        nNewStartEmptyLine++;
                    }
                    if (nNewStartEmptyLine < 2)
                        continue;
                    else {
                        isNewStart = false;
                        nNewStartEmptyLine = 0;
                    }
                } else {
                    if (newLine.trim().length() <= 1)  // Empty Line means reach the new result end
                    {
                        retHeaderList.add(parseResult(bufferList, hostName, strDate,service));
                        bufferList = new ArrayList<String>();
                    } else {
                        bufferList.add(newLine);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("Parse IO result error @ line: %d", nCurrentLineNum), ex);
            reader.close();
            throw ex;
        }
        reader.close();
        logger.info(String.format("%d IO records are parsed from file %s", retHeaderList.size(), fileName));
        return retHeaderList;
    }

    /*
     * Time: 00:00:02
     * Device: rrqm/s wrqm/s r/s w/s rMB/s wMB/s avgrq-sz avgqu-sz await svctm %util
     * sda 0.00 0.83 0.02 0.66 0.00 0.01 19.71 0.00 1.03 0.07 0.00
     * dm-0 0.00 0.00 13.64 13.63 0.11 0.27 27.95 1.02 37.25 0.27 0.73
     * dm-1 0.00 0.00 31.04 22.06 0.15 0.12 10.24 1.57 29.54 0.05 0.25
     * dm-2 0.00 0.00 1.12 3.38 0.02 0.05 30.54 0.14 31.51 0.30 0.14
     * dm-3 0.00 0.00 0.00 0.00 0.00 0.00 74.97 0.00 12.05 1.44 0.00
     * 
     * 
     * On Ubuntu
     * 02/14/2015 06:19:56 PM
     * Device: rrqm/s wrqm/s r/s w/s rMB/s wMB/s avgrq-sz avgqu-sz await r_await w_await svctm %util
     * sda 0.00 0.23 0.00 0.48 0.00 0.02 81.92 0.00 4.27 8.07 4.23 1.25 0.06
     */
    int nDaysOffset = 0;
    DateTime previousRecordDate = null;
    Boolean isFirst=true;

    private IOHeader parseResult(List<String> bufferList, String hostName, String strDate, String service) throws Exception {
        if (bufferList.size() > 2) {
            DateTime date = null;
            // check whether match Ubuntu first
            Matcher matcher = ubuntutimePattern.matcher(bufferList.get(0));
            if (!matcher.find()) {
                matcher = timePattern.matcher(bufferList.get(0));
                if (matcher.find()) {
                    date = dateTimeFormatter.parseDateTime(strDate + " " + matcher.group(0));
                } else {
                    throw new Exception("The datetime is missing in IO log");
                }
            } else {
                // date = dateTimeFormatter.parseDateTime(strDate + " " + matcher.group(0));
                String ubuntutime = matcher.group(0).substring(0, 8);
                String ubuntuflag = matcher.group(0).substring(9);
                if (ubuntuflag.equals("PM")) {

                    if (!ubuntutime.substring(0, 1).equals("12")) {
                        date = dateTimeFormatter.parseDateTime(strDate + " " + ubuntutime).plusHours(12);
                    } else {
                        date = dateTimeFormatter.parseDateTime(strDate + " " + ubuntutime);
                    }
                } else {
                    if (ubuntutime.substring(0, 1).equals("12")) {
                        date = dateTimeFormatter.parseDateTime(strDate + " " + ubuntutime).minusHours(12);
                    } else {
                        date = dateTimeFormatter.parseDateTime(strDate + " " + ubuntutime);
                    }
                }
            }
            if (isFirst){
        		Duration d = new Duration(fileRecordDateTime, date);  
            	diffSeconds = (int) d.getStandardSeconds();
           		if(diffSeconds>0){
        			diffSeconds=-1*diffSeconds;
        		}
            	isFirst=false;
            }
            
            // Matcher matcher = timePattern.matcher(bufferList.get(0));
            // if (matcher.find()) {
            // date = dateTimeFormatter.parseDateTime(strDate + " " + matcher.group(0));
            // } else {
            // throw new Exception("The datetime is missing in IO log");
            // }

            // add the logic for the overnight case. nDaysOffset is the control value. increase the value if last record
            // is larger than current value
            if (null == previousRecordDate) {
                previousRecordDate = date;
            }
            if (previousRecordDate.isAfter(date)) {
                nDaysOffset++;
            }
            previousRecordDate = date;

            // date = date.plusDays(nDaysOffset);
            // previousRecordDate = date;

            IOHeader newHeader = new IOHeader();
            newHeader.setDate(date.plusDays(nDaysOffset).plusSeconds(diffSeconds));
            newHeader.setHostName(hostName);
            newHeader.setResultSession(resultSession);
            newHeader.setService(service);

            long unitBytePerSecond = 1;
            if (bufferList.get(1).contains("rKB/s")) {
                unitBytePerSecond = 1024;
            } else if (bufferList.get(1).contains("rMB/s")) {
                unitBytePerSecond = 1024 * 1024;
            } else if (bufferList.get(1).contains("rGB/s")) {
                unitBytePerSecond = 1024 * 1024 * 1024;
            }
            for (int i = 2; i < bufferList.size(); i++) {
                IOSubLine newLine = parseSubLine(bufferList.get(i), unitBytePerSecond);
                newLine.setIoHeader(newHeader);
                newHeader.getIoSubLines().add(newLine);
            }
            return newHeader;
        } else
            return null;
    }

    private IOSubLine parseSubLine(String strLine, long unitBytePerSecond) {
        int indexDeviceName = 0;
        int indexReadPerSecond = 3;
        int indexWritePerSecond = 4;
        int indexReadBytePerSecond = 5;
        int indexWriteBytePerSecond = 6;
        int indexAvgquSize = 8;
        int indexAwait = 9;
        int indexSvctm = 10;
        int indexUtility = 11;

        String temp = strLine.replaceAll("  ", " ");
        while (temp.length() < strLine.length()) {
            strLine = temp;
            temp = strLine.replaceAll("  ", " ");
        }
        String[] strArray = strLine.split(" ");
        IOSubLine newLine = new IOSubLine();
        newLine.setDevice(strArray[indexDeviceName]);
        newLine.setAverageQueueSize(Double.parseDouble(strArray[indexAvgquSize]));
        newLine.setAwait(Double.parseDouble(strArray[indexAwait]));
        newLine.setReadBytePerSecond((int) (Double.parseDouble(strArray[indexReadBytePerSecond]) * unitBytePerSecond));
        newLine.setServiceTime(Double.parseDouble(strArray[indexSvctm]));
        newLine.setUtility(Double.parseDouble(strArray[indexUtility]));
        newLine.setWriteBytePerSecond((int) (Double.parseDouble(strArray[indexWriteBytePerSecond]) * unitBytePerSecond));
        newLine.setReadPerSecond(Double.parseDouble(strArray[indexReadPerSecond]));
        newLine.setWritePerSecond(Double.parseDouble(strArray[indexWritePerSecond]));

        return newLine;
    }
}
