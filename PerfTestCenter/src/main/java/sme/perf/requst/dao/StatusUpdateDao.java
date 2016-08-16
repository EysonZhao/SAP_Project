package sme.perf.requst.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import sme.perf.dao.GenericDao;
import sme.perf.request.entity.StatusUpdate;
import sme.perf.utility.LogHelper;

public class StatusUpdateDao extends GenericDao<StatusUpdate> {
	
	public StatusUpdate getLatestStatus(long testRequstId){
		String strSql = "SELECT su FROM StatusUpdate su WHERE su.date = "
				+ "(SELECT MAX(su2.date) FROM StatusUpdate su2 WHERE su2.testRequestId = ?1) AND su.testRequestId = ?1 "
				+ "Order by su.id desc";
		EntityManager em = this.getEntityManager();
		try{
			Query query = em.createQuery(strSql);
			query.setParameter(1, testRequstId);
			if(! query.getResultList().isEmpty()){
				return (StatusUpdate) query.getResultList().get(query.getFirstResult());
			}
			else {
				return null;
			}
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
}
