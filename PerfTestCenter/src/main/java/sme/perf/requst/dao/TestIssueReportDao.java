package sme.perf.requst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import sme.perf.request.entity.StatusReportItem;
import sme.perf.request.entity.TestIssueReportItem;

public class TestIssueReportDao {
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";

	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

	//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	public List<TestIssueReportItem> getAllTestIssue(){
		EntityManager em = this.getEntityManager();
		TypedQuery<TestIssueReportItem> query = em.createNamedQuery("getAllTestIssue", TestIssueReportItem.class);
		return query.getResultList();
	}
}
