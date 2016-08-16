package sme.perf.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.issue.ISyncIssue;
import sme.perf.issue.SyncIssue;
import sme.perf.request.entity.TestIssue;
import sme.perf.request.entity.TestRequest;
import sme.perf.requst.dao.TestRequestDao;
import sme.perf.utility.LogHelper;

@RestController
@RequestMapping("/TestIssue")
public class TestIssueController {
	TestRequestDao requestDao = new TestRequestDao();
	
	@RequestMapping("/List/{requestId}")
	public @ResponseBody TestRequest listByTestRequestId(@PathVariable long requestId){
		TestRequest testRequest = requestDao.getByID(requestId);
		ISyncIssue syncIssue = new SyncIssue(); 
		for(TestIssue issue : testRequest.getIssueList()){
			try {
				syncIssue.sync(issue);
			} catch (IOException e) {
				LogHelper.error(e);
			}
		}
		return testRequest;
	}
	
	@RequestMapping("/Update")
	public @ResponseBody TestRequest update(@RequestBody TestRequest request){
		TestRequest oldRequest = requestDao.getByID(request.getId());
		ISyncIssue syncIssue = new SyncIssue(); 
		for(TestIssue issue: request.getIssueList()){
			issue.setTestRequestId(request.getId());
			try {
				syncIssue.sync(issue);
			} catch (IOException e) {
				LogHelper.error(e);
			}
		}
		oldRequest.setIssueList(request.getIssueList());
		requestDao.update(oldRequest);
		return requestDao.getByID(request.getId());
	}
	
	@RequestMapping("/Sync")
	public @ResponseBody List<TestIssue> sync(@RequestBody List<Long> idList){
		List<TestIssue> syncedList = new ArrayList<TestIssue>();
		ISyncIssue syncIssue = new SyncIssue();
		GenericDao<TestIssue> issueDao = new GenericDao<TestIssue>(TestIssue.class);
		for(Long id : idList){
			TestIssue issue = issueDao.getByID(id);
			try {
				syncIssue.sync(issue);
			} catch (IOException e) {
				LogHelper.error(e);
			}
			issue = issueDao.update(issue);
			syncedList.add(issue);
		}
		return syncedList;
	}
	
}
