package sme.perf.analysis.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import sme.perf.analysis.entity.*;
import sme.perf.dao.GenericDao;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.result.entity.ResultSession;
import sme.perf.utility.LogHelper;

public class AnalysisIODao extends AnalysisGenericDao<AnalysisIO> {
	
	public List<Object[]> getAllIOResult(ResultSession rs, DateTime startDate,DateTime endDate){
		String strSql = "SELECT r.hostName,AVG(o.writePerSecond),MAX(o.writePerSecond),AVG(o.averageQueueSize),MAX(o.averageQueueSize),AVG(o.utility),MAX(o.utility),AVG(o.await),MAX(o.await),o.device "
				+ "FROM IOSubLine o, IOHeader r where r.resultSession=?1 and o.ioHeader=r and r.date>=?2 and r.date <?3 group by r.hostName,o.device";
		EntityManager em = this.getEntityManager();
		try{
			List<Object[]> ioList = em
					.createQuery(strSql, Object[].class).setParameter(1, rs)
					.setParameter(2, startDate)
					.setParameter(3, endDate).getResultList();
			return ioList;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public List<AnalysisIO> getAnalysisIOAllByAnalysisSession(AnalysisSession as){
		EntityManager em = this.getEntityManager();
		String strSql = "SELECT a FROM AnalysisIO a where a.analysisSession=?1";
		List<AnalysisIO> listresult = new ArrayList<AnalysisIO>();
		try{
			listresult=em.createQuery(strSql,AnalysisIO.class).setParameter(1, as).getResultList();
			return listresult;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public int deleteAnalysisIOAllByAnalysisSession(AnalysisSession as){
		EntityManager em = this.getEntityManager();
		String strSql = "delete FROM AnalysisIO a where a.analysisSession=?1";
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
