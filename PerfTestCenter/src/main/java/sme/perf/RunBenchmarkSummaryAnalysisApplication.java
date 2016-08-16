package sme.perf;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sme.perf.result.entity.ResultSession;
import sme.perf.result.entity.benchmark.*;
import sme.perf.result.impl.benchmark.*;

public class RunBenchmarkSummaryAnalysisApplication {

	private static Logger logger = Logger.getLogger("RunBenchmarkSummaryAnalysisApplication");
	private static final String PUNAME = "PerfTestCenterResult";
	public static final int durationHour = 1;
	private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		/* args
		 * args[0]:specifcRequsetID(default value is 0, means last RequestID)
		 * args[1]:baseResultRequestID
		 * args[2]:standard
		 * */
		logger.setLevel(Level.DEBUG);
		
		int specificId=0,baseResultRequestID=0;
		double standard=0;
		if (args.length >=1) {
        	specificId = Integer.parseInt(args[0]);
        	if (args.length >= 2) {
            	baseResultRequestID=Integer.parseInt(args[1]);
			}
        	if (args.length >= 3) {
        		standard=Double.parseDouble(args[2]);
			}
        	RequestSummary rs = CreateRequestSummary(specificId);

    		if (rs != null) {
    			logger.info("Start to Run Benchmark Summary ...");
    			long start = System.currentTimeMillis();
    			ImportDataToDB(new JmeterLogSummaryParser(rs,logger).setJmeterLogSummaryList(baseResultRequestID,standard));
    			ImportDataToDB(new CpuMemoryInfoSummaryParser(rs,logger).setCpuMemoryInfoSummaryList());
    			ImportDataToDB(new IOInfoSummaryParser(rs,logger).setIOInfoSummaryList());
    			logger.info("Benchmark Summary is finish. Response time (ms) = "
    					+ (System.currentTimeMillis() - start));
    		} else {
    			logger.info("No need to migrate data.");
    		}	
		} else {
			logger.fatal("Wrong Parameters.");
		}			
	}

	public static <T> void ImportDataToDB(List<T> dataList) {
		logger.info(dataList.size() + " line records need to be imported.");
		long start = System.currentTimeMillis();

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PUNAME);
		EntityManager em = emf.createEntityManager();

		int i = 0;

		em.getTransaction().begin();
		while (i < dataList.size()) {
			em.persist(dataList.get(i));
			i++;
		}
		em.getTransaction().commit();

		em.close();
		logger.info(dataList.size()
				+ " line records have been imported. Response time (ms) = "
				+ (System.currentTimeMillis() - start));
	};

	private static RequestSummary CreateRequestSummary(int id) {
		RequestSummary requestSummary = null;

		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory(PUNAME);
		EntityManager em = emf.createEntityManager();

		int rsmaxId = id;

		if (id == 0) {
			rsmaxId = (int) em.createQuery(
					"SELECT MAX(r.id) FROM ResultSession r").getSingleResult();
		}

		
		List<Integer> listRequestSummary = em.createQuery(
				"SELECT r.id FROM RequestSummary r", Integer.class)
				.getResultList();

		if (!listRequestSummary.contains(rsmaxId)) {
			ResultSession rs = (ResultSession) em
					.createQuery("SELECT r FROM ResultSession r where r.id=:ID")
					.setParameter("ID", rsmaxId).getSingleResult();
			requestSummary = new RequestSummary();
			requestSummary.setId(rs.getId());
			requestSummary.setName(rs.getName());
			requestSummary.setCreateDate(rs.getCreateDate());
			requestSummary.setUserNumber(rs.getUserNumber());
			requestSummary.setScenario(rs.getScenario());
			requestSummary.setBranch(rs.getBranch());
			requestSummary.setBuildInfo(rs.getBuildInfo());

			DateTime dateStartdate = new DateTime(1970, 1, 1, 0, 0, 0);
			DateTime dateEnddate = new DateTime(1970, 1, 1, 0, 0, 0);

			String strDateStartdate=em
					.createQuery(
							"SELECT Max(r.date) FROM JmeterLog r where r.resultSession=:RESULTSESSION and r.request=:REQUEST")
					.setParameter("RESULTSESSION", rs)
					// .setParameter("REQUEST", "WarehouseCount")
					.setParameter("REQUEST", "SalesOrderTag").getSingleResult().toString();
			
			String strDateEnddate=em
					.createQuery(
							"SELECT Max(r.date) FROM JmeterLog r where r.resultSession=:RESULTSESSION")
					.setParameter("RESULTSESSION", rs).getSingleResult().toString();
			
			dateStartdate = DateTime.parse(strDateStartdate.substring(0,strDateStartdate.indexOf(".")), dateTimeFormatter);
			dateEnddate = DateTime.parse(strDateEnddate.substring(0,strDateEnddate.indexOf(".")), dateTimeFormatter);
			
			dateStartdate=dateStartdate.plusSeconds(300);
			//dateStartdate=dateStartdate.minusSeconds(600);
			DateTime dateMaxDate = dateStartdate.plusHours(durationHour);

			if (dateEnddate.isBefore(dateMaxDate)) {
				dateMaxDate = dateEnddate;
			}

			requestSummary.setTestStartDate(dateStartdate);
			requestSummary.setTestEndDate(dateMaxDate);
			
			em.getTransaction().begin();
			em.persist(requestSummary);
			em.getTransaction().commit();
			logger.info(String.format("New Request Summary is created."));
		} else {
			logger.info(String.format("The Request Summary ID " + rsmaxId
					+ " has already analysised."));
		}
		em.close();
		return requestSummary;

	}
}
