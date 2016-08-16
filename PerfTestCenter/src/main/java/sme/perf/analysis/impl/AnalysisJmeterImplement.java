package sme.perf.analysis.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.apache.log4j.Logger;

import sme.perf.result.entity.*;
import sme.perf.analysis.entity.*;
import sme.perf.analysis.dao.*;

/**
 * @author I311112
 * 
 */
public class AnalysisJmeterImplement {
	private final AnalysisSession analysisSession;
	private final ResultSession rs;
	private Logger logger;
	private String jmeterRequestList;
	private boolean isCreated;
	private final AnalysisJmeterDao dao=new AnalysisJmeterDao();

	public AnalysisJmeterImplement(AnalysisSession analysisSession,Logger logger, 
			ResultSession rs, String jmeterRequestList, boolean isCreated) {
		this.analysisSession = analysisSession;
		this.logger=logger;
		this.rs=rs;
		this.jmeterRequestList=jmeterRequestList;
		this.isCreated=isCreated;
	}

	public void analysisJmeterLog() {
		logger.info("Start Analysis Jmeterlog...");
		long start = System.currentTimeMillis();
		DateTime startDate = analysisSession.getTestStartDate();
		DateTime endDate = analysisSession.getTestEndDate();

		Duration d = new Duration(startDate, endDate);  
		long diffSeconds = d.getStandardSeconds();
		if (!isCreated){
			int deleteCount=dao.deleteAnalysisJmeterAllByAnalysisSession(analysisSession);
			logger.info(String.format("%d rows in AnalysisJmeter have been deleted.", deleteCount));
		}
		List<Object[]> resultList=dao.getAnalysisResult(rs, startDate, endDate,jmeterRequestList);
		for (Object[] ob:resultList){
			double hps = 0, p9r = 0, avgr = 0, maxr = 0, failrate=0; 
			long passCount=0,allCount=0;
			String request="";
			avgr = Double.parseDouble(ob[0].toString());
			maxr = Double.parseDouble(ob[1].toString());
			passCount=Long.parseLong(ob[2].toString());
			allCount=Long.parseLong(ob[3].toString());
			p9r=Double.parseDouble(ob[4].toString());
			request = ob[5].toString();
			
			BigDecimal avgrBig = new BigDecimal(avgr);
			avgr = avgrBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			BigDecimal hpsBig = new BigDecimal(passCount/ (double) diffSeconds);
			hps = hpsBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			BigDecimal failrateBig = new BigDecimal((allCount-passCount)/allCount);
			failrate=failrateBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

			AnalysisJmeter analysisJmeter = new AnalysisJmeter();
			analysisJmeter.setRequest(request);
			analysisJmeter.setAnalysisSession(analysisSession);
			analysisJmeter.setHps(hps);
			analysisJmeter.setMaxResponseTime(maxr);
			analysisJmeter.setPercent90ResponseTime(p9r);
			analysisJmeter.setAverageResponseTime(avgr);
			analysisJmeter.setPassCount(passCount);
			analysisJmeter.setAllCount(allCount);
			analysisJmeter.setfailRate(failrate);
			dao.update(analysisJmeter);
		}
		
		logger.info(String.format("%d Analysis JmeterLog is Finish. Response time (ms) = "
				+ (System.currentTimeMillis() - start), resultList.size()));
			
	}
}
