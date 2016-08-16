package sme.perf.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.request.entity.StatusUpdate;
import sme.perf.request.entity.TestRequest;
import sme.perf.requst.dao.TestRequestDao;

@RestController
@RequestMapping("/StatusUpdate")
public class StatusUpdateController {
	GenericDao<StatusUpdate> dao;
	TestRequestDao testRequestDao;

	public StatusUpdateController(){
		dao = new GenericDao<StatusUpdate>(StatusUpdate.class);
		testRequestDao = new TestRequestDao();
	}
	
	@RequestMapping("/List/{testRequestId}")
	public @ResponseBody List<StatusUpdate> list(@PathVariable long testRequestId){
		GenericDao<TestRequest> testRequestDao = new GenericDao<TestRequest>(TestRequest.class);
		TestRequest testRequest = testRequestDao.getByID(testRequestId);
		return testRequest.getStatusUpdateList();
	}
	
	@RequestMapping("/Get/{id}")
	public @ResponseBody StatusUpdate get(@PathVariable long id){
		return dao.getByID(id);
	}
	
	@RequestMapping("/Add")
	public @ResponseBody StatusUpdate add(@RequestBody StatusUpdate statusUpdate){
		TestRequest testRequest = testRequestDao.getByID(statusUpdate.getTestRequestId());
		statusUpdate.setTestRequest(testRequest);
		testRequest.getStatusUpdateList().add(statusUpdate);
		testRequestDao.update(testRequest);
		testRequestDao.updateLastestStatus(testRequest.getId());
		return testRequest.getStatusUpdateList().get(testRequest.getStatusUpdateList().size()-1);
	}
	
	@RequestMapping("/Update")
	public @ResponseBody StatusUpdate update(@RequestBody StatusUpdate statusUpdate){
		TestRequest testRequest = testRequestDao.getByID(statusUpdate.getTestRequestId());
		for(int i=0 ; i<testRequest.getStatusUpdateList().size(); i++){
			if(testRequest.getStatusUpdateList().get(i).getId() == statusUpdate.getId()){
				testRequest.getStatusUpdateList().remove(i);
				i--;
			}
		}
		testRequest.getStatusUpdateList().add(statusUpdate);
		testRequestDao.update(testRequest);
		testRequestDao.updateLastestStatus(testRequest.getId());
		return statusUpdate;
	}
}
