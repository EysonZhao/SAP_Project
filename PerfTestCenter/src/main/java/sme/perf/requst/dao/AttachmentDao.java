package sme.perf.requst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import sme.perf.dao.GenericDao;
import sme.perf.request.entity.Attachment;
import sme.perf.utility.LogHelper;

public class AttachmentDao extends GenericDao<Attachment> {
	public Attachment getByNotId(long testRequestId, String fileName){
		String strSql = "SELECT a FROM Attachment a WHERE a.testRequestId=?1 and a.fileName=?2";
		EntityManager em = this.getEntityManager();
		try{
			Query query = em.createQuery(strSql);
			query.setParameter(1, testRequestId);
			query.setParameter(2, fileName);
			if(! query.getResultList().isEmpty()){
				return (Attachment) query.getResultList().get(query.getFirstResult());
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
	
	public List<Attachment> getByTestRequestId(long testRequestId){
		String strSql = "SELECT a FROM Attachment a WHERE a.testRequestId = ?1";
		EntityManager em = this.getEntityManager();
		try{
			Query query = em.createQuery(strSql);
			query.setParameter(1, testRequestId);
			return (List<Attachment>) query.getResultList();
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
}
