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
public class CpuMemoryInfoSummaryParser {
	private final RequestSummary requestsummary;
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenterResult";
	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	private final EntityManager em = emf.createEntityManager();
	private Logger logger;
	private final ResultSession rs;

	public CpuMemoryInfoSummaryParser(RequestSummary requestsummary, Logger logger) {
		this.requestsummary = requestsummary;
		this.logger=logger;
		int rsmaxId = requestsummary.getId();
		this.rs = em
				.createQuery("SELECT r FROM ResultSession r where r.id=:ID",
						ResultSession.class).setParameter("ID", rsmaxId)
				.getSingleResult();	
	}

	public List<CpuMemoryInfoSummary> setCpuMemoryInfoSummaryList() {
		long start = System.currentTimeMillis();
		List<CpuMemoryInfoSummary> cpuMemoryInfoSummarylist = new ArrayList<CpuMemoryInfoSummary>();

		DateTime startDate = requestsummary.getTestStartDate();
		DateTime endDate = requestsummary.getTestEndDate();

		// for docker
		List<Object[]> cmisDockerList = em
				.createQuery(
						"SELECT r.hostName,AVG(o.cpuUsage),MAX(o.cpuUsage),AVG(o.memUsage),MAX(o.memUsage) FROM DockerStatsSubLine o, DockerStatsHeader r where r.resultSession=:RESULTSESSION and o.dockerStatsHeader=r and r.date>=:DATE and r.date <:DATEEND group by r.hostName",
						Object[].class).setParameter("DATEEND", endDate)
				.setParameter("DATE", startDate)
				.setParameter("RESULTSESSION", rs).getResultList();
		
		for (Object[] ob : cmisDockerList) {
			double avgCpu, maxCpu, avgMem, maxMem;
			String hname = (String) ob[0];
			avgCpu = Double.parseDouble(ob[1].toString());
			maxCpu = Double.parseDouble(ob[2].toString());
			avgMem = Double.parseDouble(ob[3].toString());
			maxMem = Double.parseDouble(ob[4].toString());

			avgCpu = new BigDecimal((double) avgCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			avgMem = new BigDecimal((double) avgMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxMem = new BigDecimal((double) maxMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();

			CpuMemoryInfoSummary cmis = new CpuMemoryInfoSummary();
			cmis.setHostname(hname);
			cmis.setRequestSummary(requestsummary);
			cmis.setAvgCPU(avgCpu);
			cmis.setMaxCPU(maxCpu);
			cmis.setAvgMemory(avgMem);
			cmis.setMaxMemory(maxMem);
			cpuMemoryInfoSummarylist.add(cmis);
		}

		// for Hana
		double avgCpuSUM = 0, maxCpuSUM = 0, avgMemSUM = 0, maxMemSUM = 0;
		String hname = "";

		List<Object[]> cmisHanaList = em
				.createQuery(
						"SELECT r.hostName,AVG(o.cpu),MAX(o.cpu),AVG(o.memory),MAX(o.memory),o.command FROM TopSubLine o, TopHeader r where r.resultSession=:RESULTSESSION and o.topHeader=r and o.command in ('hdbxsengine','hdbstatisticsserv','hdbcompileserver','hdbnameserver','hdbpreprocessor','hdbindexserver') and r.date>=:DATE and r.date <:DATEEND group by r.hostName,o.command",
						Object[].class).setParameter("DATEEND", endDate)
				.setParameter("DATE", startDate)
				.setParameter("RESULTSESSION", rs).getResultList();
		
		for (Object[] ob : cmisHanaList) {
			double avgCpu, maxCpu, avgMem, maxMem;
			hname = ob[0].toString();
			avgCpu = Double.parseDouble(ob[1].toString());
			maxCpu = Double.parseDouble(ob[2].toString());
			avgMem = Double.parseDouble(ob[3].toString());
			maxMem = Double.parseDouble(ob[4].toString());
			
			avgCpu = new BigDecimal((double) avgCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			avgMem = new BigDecimal((double) avgMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxMem = new BigDecimal((double) maxMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();

			avgCpuSUM += avgCpu;
			maxCpuSUM += maxCpu;
			avgMemSUM += avgMem;
			maxMemSUM += maxMem;
		}

		if (hname != "") {
			CpuMemoryInfoSummary cmis = new CpuMemoryInfoSummary();
			cmis.setHostname(hname);
			cmis.setRequestSummary(requestsummary);
			cmis.setAvgCPU(avgCpuSUM);
			cmis.setMaxCPU(maxCpuSUM);
			cmis.setAvgMemory(avgMemSUM);
			cmis.setMaxMemory(maxMemSUM);
			cpuMemoryInfoSummarylist.add(cmis);
		}
		logger.info("CPU Memory Info Summary Has been parsed. Response time (ms) = "
				+ (System.currentTimeMillis() - start));
		return cpuMemoryInfoSummarylist;
	}
}
