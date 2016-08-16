package sme.perf.ta.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import sme.perf.ta.enetity.TA_Category;

public class GetExecutionIds {
private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";
	
	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}

	public List<TA_Category> getExecutionIds(String category){
		
		EntityManager em = this.getEntityManager();
		
		String sql = "select t from TA_Category t where t.category = ?1";
		
		TypedQuery query = em.createQuery(sql, TA_Category.class);
		query.setParameter(1, category);
		
		return query.getResultList();
		
	}
}
