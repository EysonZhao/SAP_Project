package sme.perf.result.impl.benchmark;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.apache.log4j.Logger;

import sme.perf.result.entity.ResultSession;
import sme.perf.result.entity.benchmark.*;

/**
 * @author I311112
 * 
 */
public class IOInfoSummaryParser {
	private final RequestSummary requestsummary;
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenterResult";
	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	private final EntityManager em = emf.createEntityManager();
	private Logger logger;
	private final ResultSession rs;

	public IOInfoSummaryParser(RequestSummary requestsummary, Logger logger) {
		this.requestsummary = requestsummary;
		this.logger=logger;
		int rsmaxId = requestsummary.getId();
		this.rs = em
				.createQuery("SELECT r FROM ResultSession r where r.id=:ID",
						ResultSession.class).setParameter("ID", rsmaxId)
				.getSingleResult();
	}

	public List<IOInfoSummary> setIOInfoSummaryList() {
		long start = System.currentTimeMillis();
		List<IOInfoSummary> ioInfoSummarylist = new ArrayList<IOInfoSummary>();
		
		DateTime startDate =requestsummary.getTestStartDate();
		DateTime endDate =requestsummary.getTestEndDate();
			
        List<Object[]> cmisDockerList = em
                .createQuery(
                        "SELECT r.hostName,AVG(o.writePerSecond),MAX(o.writePerSecond),AVG(o.averageQueueSize),MAX(o.averageQueueSize),AVG(o.utility),MAX(o.utility),AVG(o.await),MAX(o.await),o.device FROM IOSubLine o, IOHeader r where r.resultSession=:RESULTSESSION and o.ioHeader=r and r.date>=:DATE and r.date <:DATEEND group by r.hostName,o.device",
                        Object[].class).setParameter("DATEEND", endDate).setParameter("DATE", startDate).setParameter("RESULTSESSION", rs).getResultList();

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
        
            IOInfoSummary iis = new IOInfoSummary();
            iis.setHostname(hname);
            iis.setRequestSummary(requestsummary);
            iis.setAvgWritePerSecond(avgWPS);
            iis.setMaxWritePerSecond(maxWPS);
            iis.setAvgAverageQueueSize(avgAQS);
            iis.setMaxAverageQueueSize(maxAQS);
            iis.setAvgUtility(avgUTL);
            iis.setMaxUtility(maxUTL);
            iis.setAvgAwait(avgAwait);
            iis.setMaxAwait(maxAwait);
            iis.setDevice(dev);
            ioInfoSummarylist.add(iis);
        }
        
		logger.info("IO Info Summary Has been parsed. Response time (ms) = "
				+ (System.currentTimeMillis() - start));
		return ioInfoSummarylist;
	}
}
