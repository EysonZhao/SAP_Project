package sme.perf.result.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import sme.perf.result.entity.JmeterLog;
import sme.perf.result.entity.ResultSession;

public class JmeterLogParser implements IParseResult<JmeterLog> {

    private Logger logger;
    // private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    // private final static Pattern dataSizePattern = Pattern.compile("\\d+");
    private ResultSession resultSession;
    private String filterString;
    private Pattern interfacePattern = null;


    public JmeterLogParser(ResultSession session,Logger logger,String filter) {
        this.resultSession = session;
        this.logger=logger;
    	this.filterString = filter;
        if (filterString != null && filterString.length() > 0) {
            interfacePattern = Pattern.compile(filterString);
        }
    }

    @Override
    public List<JmeterLog> parse(String fileName) throws Exception {
        // File f = new File(fileName);
        // String shortFileName = f.getName();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        List<JmeterLog> jllist = new ArrayList<JmeterLog>();

        int nCurrentLineNum = 0;
        try {
            String newLine = null;
            while ((newLine = reader.readLine()) != null) {
                nCurrentLineNum++;
                if (newLine.length() > 0) {
                    JmeterLog newRecord = ParseSingleResult(newLine);
                    if (null != newRecord) {
                        jllist.add(newRecord);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("Parse JmeterLog result fail @line: %d", nCurrentLineNum), ex);
            reader.close();
            throw ex;
        }

        reader.close();
        logger.info(String.format("%d Jmeter Log records are parsed from file %s", jllist.size(), fileName));
        return jllist;
    }

    private JmeterLog ParseSingleResult(String newLine) {
		String[] strNewLine = newLine.split("\t");
		//if Split by Comma
		if (strNewLine.length<3){
			strNewLine= newLine.split(",");
		}
		
		DateTime newDateTime = new DateTime(1970, 1, 1, 0, 0, 0)
				.plusSeconds(Integer.parseInt(strNewLine[0].substring(0, 10)));// .plusHours(8);

		boolean isMatched = true;
		if (interfacePattern != null) {
			Matcher matcher = interfacePattern.matcher(strNewLine[2]);
			if (!matcher.find())
				isMatched = false;
		}
		if (isMatched) {
			//Fix Non HTTP ErrorIssue
			int strSize=strNewLine.length;
			JmeterLog jl = new JmeterLog();
			jl.setDate(newDateTime);
			jl.setRequest(strNewLine[2]);
			jl.setResponseTime(Double.parseDouble(strNewLine[1]));
			//String tempStrNewLine3=strNewLine[3];
			Pattern responseCodePattern = Pattern.compile("Non HTTP response code: (.+?)\t");
			Matcher responseCodeMatcher = responseCodePattern.matcher(strNewLine[3]+"\t");
			if (responseCodeMatcher.find()) {
				strNewLine[3] = (strNewLine[3]+"\t").replace(responseCodeMatcher.group(0),"600");
			}
			jl.setReturnCode(Integer.parseInt(strNewLine[3]));
			
			//Avoid special cases split by comma
			if (!strNewLine[strSize-3].equals("true")) {
				jl.setStatus("error");
			}
			jl.setSize(Integer.parseInt(strNewLine[strSize-2]));
			jl.setLatency(Integer.parseInt(strNewLine[strSize-1]));

			jl.setResultSession(resultSession);
			return jl;
		}
		
        return null;
    }
}
