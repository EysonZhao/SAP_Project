package sme.perf.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import sme.perf.utility.LogHelper;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public class GenericDao <T> {

	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenter";

	protected static EntityManagerFactory factory = Persistence
			.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

	private Class entityBeanClass; 

	//Typically a separate EntityManager should be created for each transaction or request. - See more at: http://www.eclipse.org/eclipselink/documentation/2.6/concepts/cache001.htm#CHECCJJD
	protected EntityManager getEntityManager(){
		return factory.createEntityManager();
	}
	
	@SuppressWarnings("rawtypes")
	///The constructor is for derived class
	public GenericDao(){
		entityBeanClass = (Class) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}
	
	///remove the abstract definition. so that the GenericDao could be used anywhere.
	public GenericDao(Class<T> cls){
		entityBeanClass = cls;
	}
	
	public T add(T o) {
		EntityManager em = this.getEntityManager();
		try{
			em.getTransaction().begin();
			em.persist(o);
			em.getTransaction().commit();
		}
		catch(Exception ex){
			em.getTransaction().rollback();
			LogHelper.error(ex);
		}
		return o;
	}
	
	public T update(T o){
		EntityManager em = this.getEntityManager();
		try{
			em.getTransaction().begin();
			em.merge(o);
			em.getTransaction().commit();
		}
		catch(Exception ex){
			em.getTransaction().rollback();
			LogHelper.error(ex);
		}
		return o;
	}
	
	public void delete(T o){
		EntityManager em = this.getEntityManager();
		try{
			em.getTransaction().begin();
			em.remove(em.merge(o));
//			em.remove(o);
			em.getTransaction().commit();
		}
		catch(Exception ex){
			em.getTransaction().rollback();
			LogHelper.error(ex);
		}
	}
	
	public T getByID(long id){
		EntityManager em = this.getEntityManager();
		return (T) em.find(entityBeanClass, id);
	}
	
	public List<T> getAll(){
		EntityManager em = this.getEntityManager();
		List<T> ret = em.createQuery(String.format("from %s t", entityBeanClass.getSimpleName())).getResultList();
		return ret;
	}
}
