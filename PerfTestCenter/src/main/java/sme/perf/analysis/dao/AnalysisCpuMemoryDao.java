package sme.perf.analysis.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import sme.perf.analysis.entity.*;
import sme.perf.result.entity.TopHeader;
import sme.perf.result.entity.ResultSession;
import sme.perf.utility.LogHelper;

public class AnalysisCpuMemoryDao extends AnalysisGenericDao<AnalysisCpuMemory> {
	
//	public List<Object[]> getDockerCpuMemoryResult(ResultSession rs, DateTime startDate,DateTime endDate){
//		String strSql = "SELECT r.hostName,AVG(o.cpuUsage),MAX(o.cpuUsage),AVG(o.memUsage),MAX(o.memUsage) "
//				+ "FROM DockerStatsSubLine o, DockerStatsHeader r "
//				+ "where r.resultSession=?1 and o.dockerStatsHeader=r and r.date>=?2 and r.date <?3 group by r.hostName";
//		EntityManager em = this.getEntityManager();
//		try{
//			List<Object[]> cmDockerList = em
//					.createQuery(strSql, Object[].class).setParameter(1, rs)
//					.setParameter(2, startDate)
//					.setParameter(3, endDate).getResultList();
//			return cmDockerList;
//		}
//		catch(Exception ex){
//			LogHelper.error(ex);
//			return null;
//		}
//	}
//	
//	public List<Object[]> getHanaCpuMemoryResult(ResultSession rs, DateTime startDate,DateTime endDate){
//		String strSql = "SELECT r.hostName,AVG(o.cpu),MAX(o.cpu),AVG(o.memory),MAX(o.memory),o.command "
//				+ "FROM TopSubLine o, TopHeader r "
//				+ "where r.resultSession=?1 and o.topHeader=r and o.command like 'hdb%' and r.date>=?2 and r.date <?3 group by r.hostName,o.command";
//		EntityManager em = this.getEntityManager();
//		try{
//			List<Object[]> cmhanaList = em
//					.createQuery(strSql, Object[].class).setParameter(1, rs)
//					.setParameter(2, startDate)
//					.setParameter(3, endDate).getResultList();
//			return cmhanaList;
//		}
//		catch(Exception ex){
//			LogHelper.error(ex);
//			return null;
//		}
//	}
//	
//	public Integer getCpuCoreNumberFromMachineResultByHostname(ResultSession rs, String hostName){
//		String strSql = "Select m.cpuCoreNumber From Machine m where m.resultSession=?1 and m.hostName like ?2 ";
//		EntityManager em = this.getEntityManager();
//		try{
//			int cpuCoreNumber = em
//					.createQuery(strSql, Integer.class).setParameter(1, rs).setParameter(2, hostName+'%').getSingleResult();
//			return cpuCoreNumber;
//		}
//		catch(Exception ex){
//			LogHelper.error(ex);
//			return null;
//		}
//	}
	
	public List<Object[]> getNamedDockerCpuMemoryResult(ResultSession rs,
			DateTime startDate, DateTime endDate) {
		EntityManager em = this.getEntityManager();
		List<Object[]> listresult = new ArrayList<Object[]>();
		try {
			listresult = em
					.createNamedQuery("getDockerCpuMemoryResult",
							Object[].class).setParameter(1, rs.getId())
					.setParameter(2, startDate.toString("yyyy-MM-dd HH:mm:ss"))
					.setParameter(3, endDate.toString("yyyy-MM-dd HH:mm:ss"))
					.setParameter(4, rs.getId()).getResultList();
			return listresult;
		} catch (Exception ex) {
			LogHelper.error(ex);
			return null;
		}
	}
		
	public List<Object[]> getNamedHanaCpuMemoryResult(ResultSession rs,
			DateTime startDate, DateTime endDate) {
		EntityManager em = this.getEntityManager();
		List<Object[]> listresult = new ArrayList<Object[]>();
		try {
			listresult = em
					.createNamedQuery("getHANACpuMemoryResult", Object[].class)
					.setParameter(1, rs.getId())
					.setParameter(2, startDate.toString("yyyy-MM-dd HH:mm:ss"))
					.setParameter(3, endDate.toString("yyyy-MM-dd HH:mm:ss"))
					.setParameter(4, rs.getId()).getResultList();
			return listresult;
		} catch (Exception ex) {
			LogHelper.error(ex);
			return null;
		}
	}
	
	public List<Object[]> getMachineCpuMemoryResult(ResultSession rs,
			DateTime startDate, DateTime endDate) {
		EntityManager em = this.getEntityManager();
		String strSql = "SELECT t.hostName,(100-AVG(t.cpuIdle)),(100-Min(t.cpuIdle)),AVG(t.memoryUsed),Max(t.memoryUsed) from TopHeader t "
				+ "where t.resultSession=?1 and t.date>=?2 and t.date <?3 group by t.hostName";
		List<Object[]> listresult = new ArrayList<Object[]>();
		try {
			listresult = em.createQuery(strSql,Object[].class)
					.setParameter(1, rs).setParameter(2, startDate)
					.setParameter(3, endDate).getResultList();
			return listresult;
		} catch (Exception ex) {
			LogHelper.error(ex);
			return null;
		}
	}
	
	

	public List<AnalysisCpuMemory> getAnalysisCpuMemoryAllByAnalysisSession(AnalysisSession as) {
		EntityManager em = this.getEntityManager();
		String strSql = "SELECT a FROM AnalysisCpuMemory a where a.analysisSession=?1";
		List<AnalysisCpuMemory> listresult = new ArrayList<AnalysisCpuMemory>();
		try {
			listresult = em.createQuery(strSql, AnalysisCpuMemory.class)
					.setParameter(1, as).getResultList();
			return listresult;
		} catch (Exception ex) {
			LogHelper.error(ex);
			return null;
		}
	}
	
	public int deleteAnalysisCpuMemoryAllByAnalysisSession(AnalysisSession as){
		EntityManager em = this.getEntityManager();
		String strSql = "delete FROM AnalysisCpuMemory a where a.analysisSession=?1";
		int deletedCount=0;
		try{
			Query query = em.createQuery(strSql,AnalysisJmeter.class).setParameter(1, as);
			em.getTransaction().begin();
			deletedCount = query.executeUpdate();
			em.getTransaction().commit();
		}
		catch(Exception ex){
			LogHelper.error(ex);
		}
		return deletedCount;
	}
}


