package sme.perf.analysis.impl;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.apache.log4j.Logger;

import sme.perf.analysis.dao.AnalysisCpuMemoryDao;
import sme.perf.analysis.entity.AnalysisCpuMemory;
import sme.perf.analysis.entity.AnalysisSession;
import sme.perf.result.entity.ResultSession;

/**
 * @author I311112
 * 
 */
public class AnalysisCpuMemoryImplement {
	private final AnalysisSession analysisSession;
	private final ResultSession rs;
	private Logger logger;
	private boolean isCreated;
	private final AnalysisCpuMemoryDao dao=new AnalysisCpuMemoryDao();

	public AnalysisCpuMemoryImplement(AnalysisSession analysisSession, Logger logger,ResultSession rs,boolean isCreated) {
		this.analysisSession = analysisSession;
		this.logger=logger;
		this.rs=rs;
		this.isCreated=isCreated;
	}

	public void analysisCpuMemory() {
		logger.info("Start Analysis Cpu&Memory...");
		long start = System.currentTimeMillis();
		DateTime startDate = analysisSession.getTestStartDate();
		DateTime endDate = analysisSession.getTestEndDate();

		if (!isCreated){
			int deleteCount=dao.deleteAnalysisCpuMemoryAllByAnalysisSession(analysisSession);
			logger.info(String.format("%d rows in AnalysisCpuMemory have been deleted.", deleteCount));
		}	
		
		// for docker
		List<Object[]> acmDockerList = dao.getNamedDockerCpuMemoryResult(rs, startDate, endDate);	
		String service="";
		for (Object[] ob : acmDockerList) {
			double avgCpu, maxCpu, avgMem, maxMem;
			String hname = (String) ob[0];
			avgCpu = Double.parseDouble(ob[1].toString());
			maxCpu = Double.parseDouble(ob[2].toString());
			avgMem = Double.parseDouble(ob[3].toString());
			maxMem = Double.parseDouble(ob[4].toString());
			service=(String) ob[5];
			
			avgCpu = new BigDecimal((double) avgCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxCpu = new BigDecimal((double) maxCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			avgMem = new BigDecimal((double) avgMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxMem = new BigDecimal((double) maxMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();

			AnalysisCpuMemory acm = new AnalysisCpuMemory();
			acm.setHostname(hname);
			acm.setAnalysisSession(analysisSession);
			acm.setAvgCPU(avgCpu);
			acm.setMaxCPU(maxCpu);
			acm.setAvgMemory(avgMem);
			acm.setMaxMemory(maxMem);
			acm.setService(service);
			dao.update(acm);
		}

		// for Hana
		service="HANA";
		List<Object[]> acmHanaList = dao.getNamedHanaCpuMemoryResult(rs, startDate, endDate);
		
		for (Object[] ob : acmHanaList) {
			double avgCpu=0, maxCpu=0, avgMem=0, maxMem=0;
			String hname = ob[0].toString();
			avgCpu = Double.parseDouble(ob[1].toString());
			maxCpu = Double.parseDouble(ob[2].toString());
			avgMem = Double.parseDouble(ob[3].toString());
			maxMem = Double.parseDouble(ob[4].toString());
			
			avgCpu = new BigDecimal((double) avgCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxCpu = new BigDecimal((double) maxCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			avgMem = new BigDecimal((double) avgMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxMem = new BigDecimal((double) maxMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			
			AnalysisCpuMemory acm = new AnalysisCpuMemory();
			acm.setHostname(hname);
			acm.setAnalysisSession(analysisSession);
			acm.setAvgCPU(avgCpu);
			acm.setMaxCPU(maxCpu);
			acm.setAvgMemory(avgMem);
			acm.setMaxMemory(maxMem);
			acm.setService(service);
			dao.update(acm);
		}
		
		// for Machine
		service="MACHINE";
		List<Object[]> acmMachineList = dao.getMachineCpuMemoryResult(rs, startDate, endDate);
		
		for (Object[] ob : acmMachineList) {
			double avgCpu=0, maxCpu=0, avgMem=0, maxMem=0;
			String hname = ob[0].toString();
			avgCpu = Double.parseDouble(ob[1].toString());
			maxCpu = Double.parseDouble(ob[2].toString());
			avgMem = Double.parseDouble(ob[3].toString());
			maxMem = Double.parseDouble(ob[4].toString());
			
			avgCpu = new BigDecimal((double) avgCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxCpu = new BigDecimal((double) maxCpu).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			avgMem = new BigDecimal((double) avgMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			maxMem = new BigDecimal((double) maxMem / 1024 / 1024).setScale(2,
					BigDecimal.ROUND_HALF_UP).doubleValue();
			
			AnalysisCpuMemory acm = new AnalysisCpuMemory();
			acm.setHostname(hname);
			acm.setAnalysisSession(analysisSession);
			acm.setAvgCPU(avgCpu);
			acm.setMaxCPU(maxCpu);
			acm.setAvgMemory(avgMem);
			acm.setMaxMemory(maxMem);
			acm.setService(service);
			dao.update(acm);
		}
		
		logger.info(String.format("%d Analysis Cpu&Memory is Finish. Response time (ms) = "
				+ (System.currentTimeMillis() - start),acmDockerList.size()+acmHanaList.size()+acmMachineList.size()));
	
	}
}
