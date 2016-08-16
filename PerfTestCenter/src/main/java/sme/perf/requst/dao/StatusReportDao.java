package sme.perf.requst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import sme.perf.request.entity.ProjectStatusReport;
import sme.perf.request.entity.StatusReportItem;

public class StatusReportDao {
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";

	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

	//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	public List<StatusReportItem> getProjectActiveRequests(DateTime startDate, DateTime endDate, long projectId){
		EntityManager em = this.getEntityManager();
		TypedQuery<StatusReportItem> query = em.createNamedQuery("activeTestRequest", StatusReportItem.class);
		query.setParameter(1, startDate.toString("yyyy-MM-dd HH:mm:ss"));
		query.setParameter(2, endDate.toString("yyyy-MM-dd HH:mm:ss"));
		query.setParameter(3, projectId);
		return query.getResultList();
	}
	
	public List<StatusReportItem> getProjectPlanRequests(DateTime startDate, DateTime endDate, long projectId){
		EntityManager em = this.getEntityManager();
		TypedQuery<StatusReportItem> query = em.createNamedQuery("planTestRequest", StatusReportItem.class);
		query.setParameter(1, startDate.toString("yyyy-MM-dd HH:mm:ss"));
		query.setParameter(2, endDate.toString("yyyy-MM-dd HH:mm:ss"));
		query.setParameter(3, projectId);
		return query.getResultList();
	}
	
	public List<StatusReportItem> getProjectBlockedRequests(long projectId){
		EntityManager em = this.getEntityManager();
		TypedQuery<StatusReportItem> query = em.createNamedQuery("blockedTestRequest", StatusReportItem.class);
		query.setParameter(1, projectId);
		return query.getResultList();
	}
	
}
