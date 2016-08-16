package sme.perf.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.dao.GenericRawDataDao;
import sme.perf.execution.State;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.request.entity.Central_Codeline;
import sme.perf.request.entity.StatusUpdate;
import sme.perf.request.entity.TestRequest;
import sme.perf.requst.dao.CodelineDao;
import sme.perf.ta.enetity.GeneratedRequestParams;

@RestController
@RequestMapping("/TestRequest")
public class TestRequestController {

	GenericDao<TestRequest> dao;
	public TestRequestController(){
		dao = new GenericDao<TestRequest>(TestRequest.class);
	}
	@RequestMapping("/GetCodeline")
	public @ResponseBody List getCodeline(){
		CodelineDao rdao = new CodelineDao();
		return rdao.getAll();
	}
	
	@RequestMapping("/List")
	public @ResponseBody List list(){
		return dao.getAll();
	}
	
	@RequestMapping("/Get/{id}")
	public @ResponseBody TestRequest get(@PathVariable long id){
		return dao.getByID(id);
	}
	
	@RequestMapping("/Add")
	public @ResponseBody TestRequest add(@RequestBody TestRequest testRequest ){
		testRequest.setLatestStatus("New");
		DateTime now = DateTime.now();
		testRequest.setLastUpdateDate(new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0));
		dao.add(testRequest);
		if(testRequest.getStatusUpdateList() == null ||
				testRequest.getStatusUpdateList().isEmpty()){
			//add a default status update for the new test request
			StatusUpdate statusUpdate = new StatusUpdate();
			statusUpdate.setDate(testRequest.getLastUpdateDate());
			statusUpdate.setStatus("New");
			statusUpdate.setTestRequestId(testRequest.getId());
//			statusUpdate.setDescription("");
			testRequest.getStatusUpdateList().add(statusUpdate);
		}
		return dao.update(testRequest);
	}
	
	@RequestMapping("/Update")
	public @ResponseBody TestRequest update(@RequestBody TestRequest testRequest){
		return dao.update(testRequest);
	}
	
	@RequestMapping("/Delete/{id}")
	public void delete(@PathVariable long id){
		dao.delete(dao.getByID(id));
	}
	
	@RequestMapping("/Duplicate/{executionId}")
	public TestRequest duplicate(@PathVariable long executionId){
		TestRequest template = dao.getByID(executionId);
		TestRequest newRequest = (TestRequest) template.clone();
		//after clone. reset the status to be 'New' and add a new status
		newRequest.setLatestStatus("New");
		newRequest = dao.add(newRequest);
		StatusUpdate statusUpdate = new StatusUpdate();
		statusUpdate.setDate(DateTime.now());
		statusUpdate.setStatus("New");
		statusUpdate.setTestRequestId(newRequest.getId());
		newRequest.getStatusUpdateList().add(statusUpdate);
		return dao.update(newRequest);
	}
	
}
