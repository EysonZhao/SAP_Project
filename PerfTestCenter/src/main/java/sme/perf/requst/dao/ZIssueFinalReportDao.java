package sme.perf.requst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import sme.perf.request.entity.ZIssueFinalReportItem;

public class ZIssueFinalReportDao {
		private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";

		protected static EntityManagerFactory factory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

		//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
		protected EntityManager getEntityManager(){
			return factory.createEntityManager();
		}

		public List<ZIssueFinalReportItem> getLatestReportIssue(long projectid,DateTime startDate, DateTime endDate, String priority){

			EntityManager em = this.getEntityManager();		
			TypedQuery<ZIssueFinalReportItem> query = em.createNamedQuery("ZgetLatestReportIssue", ZIssueFinalReportItem.class);
			query.setParameter(1, projectid);
			query.setParameter(2, startDate.toString("yyyy-MM-dd HH:mm:ss"));
			query.setParameter(3, endDate.toString("yyyy-MM-dd HH:mm:ss"));
			query.setParameter(4, priority);
			return query.getResultList();
		}


		public List<ZIssueFinalReportItem> getOlderReportIssue(long projectid,int period, int earlyday){

			EntityManager em = this.getEntityManager();		
			TypedQuery<ZIssueFinalReportItem> query = em.createNamedQuery("ZgetOlderReportIssue", ZIssueFinalReportItem.class);
			query.setParameter(1, projectid);
			query.setParameter(2, period);
			query.setParameter(3, earlyday);
			return query.getResultList();
		}
}
