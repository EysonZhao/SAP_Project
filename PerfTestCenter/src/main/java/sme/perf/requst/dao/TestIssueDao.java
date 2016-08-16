package sme.perf.requst.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import sme.perf.dao.GenericDao;
import sme.perf.request.entity.TestIssue;

public class TestIssueDao extends GenericDao<TestIssue>{
	public List<TestIssue> getIssueListByTestRequestList(List<Long> requestIdList){
		String strSql = "SELECT issue FROM TestIssue issue WHERE issue.testRequestId in ?1";
		EntityManager em  = this.getEntityManager();
		
		TypedQuery<TestIssue> query = em.createQuery(strSql, TestIssue.class);
		query.setParameter(1, requestIdList);
		return query.getResultList();
	}
	
	public List<TestIssue> getProjectIssuesByCreateDate(long projectId, DateTime startDate, DateTime endDate){
		String strSql = "SELECT issue FROM TestIssue issue, TestRequest request WHERE issue.testRequestId = request.id "
				+ " AND request.projectId = ?1 AND issue.createDate >= ?2 AND issue.createDate <= ?3";
		EntityManager em = this.getEntityManager();
		TypedQuery<TestIssue> query = em.createQuery(strSql, TestIssue.class);
		query.setParameter(1, projectId);
		query.setParameter(2, startDate);
		query.setParameter(3, endDate);
		return query.getResultList();
	}
}
