package sme.perf.analysis.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.apache.log4j.Logger;

import sme.perf.analysis.dao.AnalysisIODao;
import sme.perf.analysis.dao.AnalysisJmeterDao;
import sme.perf.analysis.entity.AnalysisIO;
import sme.perf.analysis.entity.AnalysisSession;
import sme.perf.result.entity.ResultSession;
import sme.perf.result.entity.benchmark.*;

/**
 * @author I311112
 * 
 */
public class AnalysisIOImplement {
	private final AnalysisSession analysisSession;
	private final ResultSession rs;
	private Logger logger;
	private boolean isCreated;
	private final AnalysisIODao dao=new AnalysisIODao();

	public AnalysisIOImplement(AnalysisSession analysisSession, Logger logger,ResultSession rs, boolean isCreated) {
		this.analysisSession = analysisSession;
		this.logger=logger;
		this.rs=rs;
		this.isCreated=isCreated;
	}

	public void analysisIO() {
		logger.info("Start Analysis IO...");
		long start = System.currentTimeMillis();
		DateTime startDate = analysisSession.getTestStartDate();
		DateTime endDate = analysisSession.getTestEndDate();
		
		if (!isCreated){
			int deleteCount=dao.deleteAnalysisIOAllByAnalysisSession(analysisSession);
			logger.info(String.format("%d rows in AnaysisIO have been deleted.", deleteCount));
		}	
		
		List<Object[]> cmisDockerList=dao.getAllIOResult(rs,startDate, endDate);
        for (Object[] ob : cmisDockerList) {
        	double avgWPS, maxWPS,avgAQS, maxAQS,avgUTL, maxUTL,avgAwait, maxAwait;	
            String hname = ob[0].toString();
            avgWPS = Double.parseDouble(ob[1].toString());
            maxWPS = Double.parseDouble(ob[2].toString());
            avgAQS = Double.parseDouble(ob[3].toString());
            maxAQS = Double.parseDouble(ob[4].toString());
            avgUTL = Double.parseDouble(ob[5].toString());
            maxUTL = Double.parseDouble(ob[6].toString());
            avgAwait = Double.parseDouble(ob[7].toString());
            maxAwait = Double.parseDouble(ob[8].toString());
            String dev = ob[9].toString();
            
            avgWPS = new BigDecimal((double) avgWPS).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            avgAQS = new BigDecimal((double) avgAQS).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            avgUTL = new BigDecimal((double) avgUTL).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            avgAwait = new BigDecimal((double) avgAwait).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        
            AnalysisIO ai = new AnalysisIO();
            ai.setHostname(hname);
            ai.setAnalysisSession(analysisSession);
            ai.setAvgWritePerSecond(avgWPS);
            ai.setMaxWritePerSecond(maxWPS);
            ai.setAvgAverageQueueSize(avgAQS);
            ai.setMaxAverageQueueSize(maxAQS);
            ai.setAvgUtility(avgUTL);
            ai.setMaxUtility(maxUTL);
            ai.setAvgAwait(avgAwait);
            ai.setMaxAwait(maxAwait);
            ai.setDevice(dev);     
            dao.update(ai);
        }
		logger.info(String.format("%d Analysis IO is Finish. Response time (ms) = "
				+ (System.currentTimeMillis() - start),cmisDockerList.size()));
		
	}
}
