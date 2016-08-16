package sme.perf.requst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import sme.perf.analysis.dao.AnalysisGenericDao;
import sme.perf.analysis.entity.Transactiontimeanalysis;
import sme.perf.dao.GenericRawDataDao;
import sme.perf.request.entity.Central_Codeline;

public class CodelineDao  extends GenericRawDataDao<Central_Codeline>{
		private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenterResult";
		
		protected static EntityManagerFactory factory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


		//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
		protected EntityManager getEntityManager(){
			return factory.createEntityManager();
		}
		
		public List<Central_Codeline> getAllItems(){
			EntityManager em = this.getEntityManager();
			String sql = "SELECT t FROM Central_Codeline t";
			TypedQuery<Central_Codeline> query = em.createQuery(sql, Central_Codeline.class);
			return ( List<Central_Codeline>)query.getResultList();
		}
	
		
}
