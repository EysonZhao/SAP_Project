package sme.perf.analysis.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;


import sme.perf.analysis.entity.Transactiontimeanalysis;

public class TransactionTimeAnalysisDao  extends AnalysisGenericDao<Transactiontimeanalysis>{
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenterResult";
	
	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


	//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	public List<Transactiontimeanalysis> getAllItems(){
		EntityManager em = this.getEntityManager();
		String sql = "SELECT t FROM Transactiontimeanalysis t";
		TypedQuery<Transactiontimeanalysis> query = em.createQuery(sql, Transactiontimeanalysis.class);
		return ( List<Transactiontimeanalysis>)query.getResultList();
	}
	
	public List<Transactiontimeanalysis> getBySessionId(int sesnId){
		EntityManager em = this.getEntityManager();
		String sql = "SELECT t FROM Transactiontimeanalysis t where t.resultSessionId = ?1 order by t.transactionName";
		TypedQuery<Transactiontimeanalysis> query = em.createQuery(sql,Transactiontimeanalysis.class);
		query.setParameter(1, sesnId);
		return query.getResultList();
	}
}
