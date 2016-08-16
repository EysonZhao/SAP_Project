package sme.perf.analysis.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.analysis.entity.*;
import sme.perf.request.entity.StatusReportItem;
import sme.perf.result.entity.*;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;

public class AnalysisJmeterDao extends AnalysisGenericDao<AnalysisJmeter> {
	//TODO  TAGNAME SHOULD BE DEFINED AS PRICIPLE;
	private static final String JmeterStartTagRequest="javaScript TestStartTag";
	private static final String JmeterEndTagRequest="javaScript TestEndTag";

	// debug
	// private static final String JmeterStartTagRequest="CreatePosChannel";
	// private static final String JmeterEndTagRequest="/sbo/saml2/sp/logout";
	
	private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	public DateTime getStartDateTimeByResultSession(ResultSession rs){
		DateTime dateStartdate = this.getDefaultStartDateTimeByResultSession(rs);
		String strSql = "SELECT Max(r.date) FROM JmeterLog r where r.resultSession=?1 and r.request=?2";
		EntityManager em = this.getEntityManager();
		try{
 			Object thisResult=em.createQuery(strSql).setParameter(1, rs).setParameter(2, JmeterStartTagRequest).getSingleResult();
			if(thisResult!=null){
				String strDateStartdate=thisResult.toString();
				dateStartdate = DateTime.parse(strDateStartdate.substring(0,strDateStartdate.indexOf(".")), dateTimeFormatter);
			}
			return dateStartdate;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public DateTime getEndDateTimeByResultSession(ResultSession rs){
		DateTime dateEnddate = this.getDefaultEndDateTimeByResultSession(rs);
		String strSql = "SELECT Min(r.date) FROM JmeterLog r where r.resultSession=?1 and r.request=?2";
		EntityManager em = this.getEntityManager();
		try{
			Object thisResult=em.createQuery(strSql).setParameter(1, rs).setParameter(2, JmeterEndTagRequest).getSingleResult();
			if(thisResult!=null){
				String strDateEnddate=thisResult.toString();
				dateEnddate = DateTime.parse(strDateEnddate.substring(0,strDateEnddate.indexOf(".")), dateTimeFormatter);
			}
			return dateEnddate;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}

	public DateTime getDefaultStartDateTimeByResultSession(ResultSession rs){
		DateTime dateStartdate = new DateTime(1970, 1, 1, 0, 0, 0);
		String strSql = "SELECT Min(r.date) FROM JmeterLog r where r.resultSession=?1";
		EntityManager em = this.getEntityManager();
		try{
 			Object thisResult=em.createQuery(strSql).setParameter(1, rs).getSingleResult();
			if(thisResult!=null){
				String strDateStartdate=thisResult.toString();
				dateStartdate = DateTime.parse(strDateStartdate.substring(0,strDateStartdate.indexOf(".")), dateTimeFormatter);
			}
			return dateStartdate;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public DateTime getDefaultEndDateTimeByResultSession(ResultSession rs){
		DateTime dateEnddate = new DateTime(2999, 1, 1, 0, 0, 0);
		String strSql = "SELECT Max(r.date) FROM JmeterLog r where r.resultSession=?1";
		EntityManager em = this.getEntityManager();
		try{
			Object thisResult=em.createQuery(strSql).setParameter(1, rs).getSingleResult();
			if(thisResult!=null){
				String strDateEnddate=thisResult.toString();
				dateEnddate = DateTime.parse(strDateEnddate.substring(0,strDateEnddate.indexOf(".")), dateTimeFormatter);
			}
			return dateEnddate;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public List<AnalysisJmeter> getAnalysisJmeterAllByAnalysisSession(AnalysisSession as){
		EntityManager em = this.getEntityManager();
		String strSql = "SELECT a FROM AnalysisJmeter a where a.analysisSession=?1";
		List<AnalysisJmeter> listresult = new ArrayList<AnalysisJmeter>();
		try{
			listresult=em.createQuery(strSql,AnalysisJmeter.class).setParameter(1, as).getResultList();
			return listresult;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}
	
	public int deleteAnalysisJmeterAllByAnalysisSession(AnalysisSession as){
		EntityManager em = this.getEntityManager();
		String strSql = "delete FROM AnalysisJmeter a where a.analysisSession=?1";
		int deletedCount=0;
		try{
			Query query = em.createQuery(strSql).setParameter(1, as);
			em.getTransaction().begin();
			deletedCount = query.executeUpdate();
			em.getTransaction().commit();
		}
		catch(Exception ex){
			LogHelper.error(ex);
		}
		return deletedCount;
	}
	
	//Named Query
	public List<Object[]> getAnalysisResult(ResultSession rs, DateTime startDate,DateTime endDate, String jmeterRequeststr){
		EntityManager em = this.getEntityManager();
		List<Object[]> listresult = new ArrayList<Object[]>();
		try{
			if (jmeterRequeststr!=null){
				TypedQuery<Object[]> query=em.createNamedQuery("getAnalysisResultNewWithRequest", Object[].class).
						setParameter(1, rs.getId()).setParameter(2, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(3, endDate.toString("yyyy-MM-dd HH:mm:ss")).setParameter(4, rs.getId())
						.setParameter(5, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(6, endDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(7, rs.getId()).setParameter(8, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(9, endDate.toString("yyyy-MM-dd HH:mm:ss")).setParameter(10, rs.getId())
						.setParameter(11, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(12, endDate.toString("yyyy-MM-dd HH:mm:ss"));
				String[] requestArray=jmeterRequeststr.split(",");
				for(int i=0; i<=requestArray.length;i++){
					if (i<requestArray.length){
						query.setParameter((i+13),requestArray[i]);
					}else{
						for(int j=13+i;j<=32;j++){
							query.setParameter(j,"null");
						}
					}	
					if (i>=20){
						break;
					}
				}
				listresult=query.getResultList();
			}else{
				listresult=em.createNamedQuery("getAnalysisResultNew", Object[].class).
						setParameter(1, rs.getId()).setParameter(2, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(3, endDate.toString("yyyy-MM-dd HH:mm:ss")).setParameter(4, rs.getId())
						.setParameter(5, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(6, endDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(7, rs.getId()).setParameter(8, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(9, endDate.toString("yyyy-MM-dd HH:mm:ss")).setParameter(10, rs.getId())
						.setParameter(11, startDate.toString("yyyy-MM-dd HH:mm:ss"))
						.setParameter(12, endDate.toString("yyyy-MM-dd HH:mm:ss")).getResultList();
			}
			return listresult;
		}
		catch(Exception ex){
			LogHelper.error(ex);
			return null;
		}
	}	
	

}
