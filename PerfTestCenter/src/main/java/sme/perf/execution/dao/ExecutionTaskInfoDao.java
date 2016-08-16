package sme.perf.execution.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import sme.perf.dao.GenericDao;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.utility.LogHelper;

public class ExecutionTaskInfoDao extends GenericDao<ExecutionTaskInfo> {
	
	public List<ExecutionTaskInfo> getListByExecutionInfoId(long execInfoId){
		String strSql = "SELECT e FROM ExecutionTaskInfo e WHERE e.executionInfoId = ?1";
		EntityManager em = this.getEntityManager();
		try{
			TypedQuery<ExecutionTaskInfo> query = em.createQuery(strSql, ExecutionTaskInfo.class);
			query.setParameter(1, execInfoId);
			return (List<ExecutionTaskInfo>) query.getResultList();
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public int deleteList(List<ExecutionTaskInfo> execTaskInfoList){
		if(null == execTaskInfoList || execTaskInfoList.size() < 1){
			return 0;
		}
		
		List<Long> idList = new ArrayList<Long>();
		for(ExecutionTaskInfo execTaskInfo : execTaskInfoList){
			idList.add(execTaskInfo.getId());
		}
		String strSql = "DELETE FROM ExecutionTaskInfo e where e in ?1";
		EntityManager em = this.getEntityManager();
		int deletedCount = 0;
		try{
			Query query = em.createQuery(strSql);
			query.setParameter(1, idList);
			em.getTransaction().begin();
			deletedCount = query.executeUpdate();
			em.getTransaction().commit();
		}catch(Exception ex){
			em.getTransaction().rollback();
			LogHelper.error(ex);
		}

		return deletedCount;
	}
}
