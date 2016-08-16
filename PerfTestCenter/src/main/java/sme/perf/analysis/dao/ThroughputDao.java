package sme.perf.analysis.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import sme.perf.analysis.entity.Throughput;
import sme.perf.analysis.entity.ThroughputParameters;
import sme.perf.analysis.entity.UserNumbers;

public class ThroughputDao extends AnalysisGenericDao<Throughput>{
private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";
	
	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	public List<Throughput> getThrouphput(ThroughputParameters param){
		EntityManager em = this.getEntityManager();
		TypedQuery<Throughput> query = em.createNamedQuery("ThroughputSQL", Throughput.class);
		query.setParameter(1, param.getResultSessionId());
		query.setParameter(2, param.getGranularity());
		query.setParameter(3, param.getTransactionName());
		return query.getResultList();
	}
}
