package sme.perf.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.requst.dao.StatusReportDao;
import sme.perf.requst.dao.TestIssueDao;
import sme.perf.requst.dao.TestIssueReportDao;
import sme.perf.requst.dao.ZIssueFinalReportDao;
import sme.perf.utility.LogHelper;
import sme.perf.utility.PropertyFile;
import sme.perf.utility.ZGenerateReport;
import sme.perf.utility.ZGetLocalServer;
import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.issue.SyncIssue;
import sme.perf.request.entity.ProjectStatusReport;
import sme.perf.request.entity.StatusReport;
import sme.perf.request.entity.StatusReport.*;
import sme.perf.request.entity.StatusReportItem;
import sme.perf.request.entity.StatusReportQueryParameter;
import sme.perf.request.entity.TestIssue;
import sme.perf.request.entity.TestIssueReportItem;
import sme.perf.request.entity.ZIssueFinalReport;

@RestController
@RequestMapping("/Report")
public class ReportController {

	@RequestMapping("/GetAllTestIssue")
	public List<TestIssueReportItem> getAllTestIssue(){
		return new TestIssueReportDao().getAllTestIssue();
	}
	
	@RequestMapping("/IssueReport")


	public List<ZIssueFinalReport> getAllReportIssue(@RequestBody StatusReportQueryParameter param){

		
		GenericDao<Project> projectDao = new GenericDao<Project>(Project.class);
		List<Project> projectList = projectDao.getAll();
		List<ZIssueFinalReport> issueReportList=new ArrayList<ZIssueFinalReport>();
		for(Project project:projectList){
		ZIssueFinalReport issueReport=new ZIssueFinalReport();
		DateTime endDate = param.getEndDate();
		endDate = new DateTime(endDate.getYear(),
				endDate.getMonthOfYear(),
				endDate.getDayOfMonth(),
				23,
				59,
				59);
		param.setEndDate(endDate);
		DateTime startDate = param.getStartDate();
			issueReport.setNearlySubmittedList(new ZIssueFinalReportDao().getLatestReportIssue(project.getId(),startDate,endDate,"%"));

			issueReport.setOlderSubmittedList(new ZIssueFinalReportDao().getOlderReportIssue(project.getId(),param.getPeriod(),15));
			issueReport.setProjectName(project.getName());
			issueReportList.add(issueReport);
		}
		return issueReportList;
		
	}

    @RequestMapping(value="/GenerateReport")
    public void onGenerateReportClicked(HttpServletRequest request,@RequestBody StatusReportQueryParameter param) throws IOException {
    	DateTime now=new DateTime();

    	String fullFileName =PropertyFile.getValue("ReportPath") 
    			+ File.separator + "CW" + now.getWeekOfWeekyear()+".html";
    	ZGetLocalServer.setAddr(request.getLocalAddr());
    	ZGetLocalServer.setPort(request.getLocalPort());
    			
       ZGenerateReport.generateReport(fullFileName,param.getStartDate(),param.getEndDate());
    }
	
	@RequestMapping("/StatusReport")
	public StatusReport getStatusReport(@RequestBody StatusReportQueryParameter param){
		//the end date 
		DateTime endDate = param.getEndDate();
		endDate = new DateTime(endDate.getYear(),
				endDate.getMonthOfYear(),
				endDate.getDayOfMonth(),
				23,
				59,
				59);
		param.setEndDate(endDate);
		
		StatusReport report = new StatusReport();
		report.setEndDate(endDate);
		report.setStartDate(param.getStartDate());
		report.setReportDate(DateTime.now());
		report.setProjectStatusReportList(new ArrayList<ProjectStatusReport>());
		StatusReportDao reportDao = new StatusReportDao();
		GenericDao<Project> projectDao = new GenericDao<Project>(Project.class);
		List<Project> projectList = projectDao.getAll();
		TestIssueDao issueDao = new TestIssueDao();
		
		SyncIssue syncIssue = new SyncIssue(); 
		for(Project project: projectList){
			ProjectStatusReport projectReport = new ProjectStatusReport();
			projectReport.setActiveTestRequests(reportDao.getProjectActiveRequests(param.getStartDate(), param.getEndDate(), project.getId()));
			projectReport.setBlockedTestRequests(reportDao.getProjectBlockedRequests(project.getId()));
			projectReport.setPlanTestRequests(reportDao.getProjectPlanRequests(param.getStartDate(), param.getEndDate(), project.getId()));
			projectReport.setProjectName(project.getName());
			projectReport.setIssues(issueDao.getProjectIssuesByCreateDate(project.getId(), param.getStartDate(), param.getEndDate()));
			for(TestIssue issue: projectReport.getIssues()){
				try {
					syncIssue.sync(issue);
				} catch (IOException e) {
					LogHelper.error(e);;
				}
			}
			if((null != projectReport.getActiveTestRequests() && projectReport.getActiveTestRequests().size() > 0) ||
				(null != projectReport.getBlockedTestRequests() && projectReport.getBlockedTestRequests().size() > 0) ||
				(null != projectReport.getIssues() && projectReport.getIssues().size() > 0) ||
				(null != projectReport.getPlanTestRequests() && projectReport.getPlanTestRequests().size() > 0)){
				report.getProjectStatusReportList().add(projectReport);
			}
		}
		return report;
	}
}
