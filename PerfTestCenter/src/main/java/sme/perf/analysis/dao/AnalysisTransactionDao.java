package sme.perf.analysis.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import sme.perf.analysis.entity.AnalysisCpuMemory;
import sme.perf.analysis.entity.AnalysisTemplate;
import sme.perf.analysis.entity.AnalysisTransaction;
import sme.perf.dao.GenericDao;
import sme.perf.utility.LogHelper;

public class AnalysisTransactionDao  extends AnalysisGenericDao<AnalysisTransaction>{

	
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";
	
	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


	//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	public List<AnalysisTransaction> selectTransactionResult(long id){
		EntityManager em = this.getEntityManager();
		TypedQuery<AnalysisTransaction> query =  em.createNamedQuery("SelectTransaction", AnalysisTransaction.class);
		query.setParameter(1, id);
		List<AnalysisTransaction> list = query.getResultList();
		return list;		
	}
	
	public List<AnalysisTransaction> getTransactionResult(long id) {
		EntityManager em = this.getEntityManager();		
		try {
			TypedQuery<AnalysisTransaction> query =  em.createNamedQuery("GetTransactionReport", AnalysisTransaction.class);
			query.setParameter(1, id);
			List<AnalysisTransaction> list = query.getResultList();
			return list;
		} catch (Exception ex) {
			LogHelper.error(ex);
			return null;
		}
	}
	
	public List<AnalysisTransaction> getAllResultSessionIds(){
		EntityManager em = this.getEntityManager();
		return em.createNamedQuery("GetSessionIds",AnalysisTransaction.class).getResultList();
	}

	
}
