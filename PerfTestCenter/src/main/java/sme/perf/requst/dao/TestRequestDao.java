package sme.perf.requst.dao;

import javax.persistence.EntityManager;

import sme.perf.dao.GenericDao;
import sme.perf.request.entity.StatusUpdate;
import sme.perf.request.entity.TestRequest;

public class TestRequestDao extends GenericDao <TestRequest>{
	public TestRequest updateLastestStatus(long testRequestId){
		StatusUpdateDao statusUpdateDao = new StatusUpdateDao();
		StatusUpdate latestUpdate = statusUpdateDao.getLatestStatus(testRequestId);
		TestRequest testRequest = this.getByID(testRequestId);
		if(null != latestUpdate){
			testRequest.setLatestStatus(latestUpdate.getStatus());
			testRequest.setLastUpdateDate(latestUpdate.getDate());
			this.update(testRequest);
		}
		return testRequest;
	}
}
