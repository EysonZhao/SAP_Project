package sme.perf.analysis.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import sme.perf.analysis.entity.AnalysisTransaction;
import sme.perf.analysis.entity.UserNumbers;

public class UserNumbersDao  extends AnalysisGenericDao<UserNumbers>{
private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";
	
	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	public List<UserNumbers> getUserNumbers(int sessionId){
		EntityManager em = this.getEntityManager();
		return em.createNamedQuery("SelectUsersNumber",UserNumbers.class).setParameter(1, sessionId).getResultList();
	}
	
}
