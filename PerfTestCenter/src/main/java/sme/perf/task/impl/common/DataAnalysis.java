package sme.perf.task.impl.common;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.analysis.dao.AnalysisJmeterDao;
import sme.perf.result.entity.*;
import sme.perf.analysis.impl.*;
import sme.perf.analysis.entity.*;
import sme.perf.analysis.dao.*;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;

public class DataAnalysis extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private boolean isCreated;
	
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		
		int resultSessionId=-1;
		if(parameters.getValue("resultSessionId",true)!=null){
			resultSessionId=Integer.parseInt(parameters.getValue("resultSessionId").toString());
		}	
		DateTime startDate=null;
		DateTime endDate=null;
		String jmeterRequestListStr=null;
		if (parameters.getValue("startDate", true) != null) {
			startDate = DateTime.parse(parameters.getValue("startDate").toString(),dateTimeFormatter);
		}

		if (parameters.getValue("endDate", true) != null) {
			endDate = DateTime.parse(parameters.getValue("endDate").toString(),dateTimeFormatter);
		}
		
		if (parameters.getValue("jmeterRequestList", true) != null) {
			jmeterRequestListStr = parameters.getValue("jmeterRequestList").toString();
		}

		logger.info("Start to Analysis Data.");
		try {
			ResultSession rs=new AnalysisGenericDao<ResultSession>(ResultSession.class).getByID(resultSessionId);
			if (rs != null) {
				isCreated=true;
				AnalysisSession analysisSession = null;
				AnalysisGenericDao<AnalysisSession> asDao= new AnalysisGenericDao<AnalysisSession>(AnalysisSession.class);
				analysisSession=asDao.getByID(rs.getId());
				if(analysisSession==null){
					analysisSession = CreateAnalysisSession(rs,startDate,endDate);
				}
				else {
					analysisSession = UpdateAnalysisSession(rs,startDate,endDate);
					isCreated = false;
				}
				if (analysisSession!=null){
					long start = System.currentTimeMillis();
					new AnalysisJmeterImplement(analysisSession, logger, rs,jmeterRequestListStr,isCreated).analysisJmeterLog();
					new AnalysisIOImplement(analysisSession, logger, rs, isCreated).analysisIO();
					new AnalysisCpuMemoryImplement(analysisSession, logger, rs, isCreated).analysisCpuMemory();
					logger.info("Analysis is finish. Response time (ms) = " + (System.currentTimeMillis() - start));
					logger.info("Finish to Analysis Data.");
				}else{
					logger.info("No AnalsisSession. Please check the parameters.");
				}
			} else {
				logger.info("No Result Session. Please check the parameters.");
			}
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		if (null == this.parameters) {
			this.parameters = DataAnalysis.getParameterTemplate();
		}
		return this.parameters;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public void setHosts(TaskParameterMap hostList) {
		this.hostParameters = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		if (null == this.hostParameters) {
			this.hostParameters = DataAnalysis.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Analysis Data.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();	
		//Common
		parameters.put("resultSessionId", new TaskParameterEntry("26","Result Session ID"));	
		parameters.put("startDate", new TaskParameterEntry("2016-01-01 00:00:00","Start DateTime (Optional)"));
		parameters.put("endDate", new TaskParameterEntry("2016-09-09 00:00:00","End DateTime (Optional)"));
		parameters.put("jmeterRequestList", new TaskParameterEntry("StateGroup,UserProperty,LightChannelService@getPOS","Jmeter Request List (Optional)"));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new DataAnalysis().getDescription();
	}
	
	private AnalysisSession CreateAnalysisSession(ResultSession rs, DateTime dateStartdate, DateTime dateEnddate) {
		AnalysisSession analysisSession = null;
		AnalysisGenericDao<AnalysisSession> asDao= new AnalysisGenericDao<AnalysisSession>(AnalysisSession.class);
		analysisSession=asDao.getByID(rs.getId());
		if(analysisSession==null){
			AnalysisJmeterDao analysisJmeterDao=new AnalysisJmeterDao();
			analysisSession = new AnalysisSession();
			analysisSession.setId(rs.getId());
			analysisSession.setName(rs.getName());
			analysisSession.setCreateDate(rs.getCreateDate());
			analysisSession.setUserNumber(rs.getUserNumber());
			analysisSession.setScenario(rs.getScenario());
			analysisSession.setBranch(rs.getBranch());
			analysisSession.setBuildInfo(rs.getBuildInfo());

			//TODO IF OP CASE NEED TO DEFINED ANOTHER WAY ABOUT STARTDATE/END DATE
			if (dateStartdate == null) {
				dateStartdate = analysisJmeterDao.getStartDateTimeByResultSession(rs);
			}
			if (dateEnddate == null) {
				dateEnddate = analysisJmeterDao.getEndDateTimeByResultSession(rs);
			}
			
			analysisSession.setTestStartDate(dateStartdate);
			analysisSession.setTestEndDate(dateEnddate);
			asDao.update(analysisSession);
			logger.info(String.format("New Request Summary is created."));
			return analysisSession;
		}else{
			return null;
		}
	}

	private AnalysisSession UpdateAnalysisSession(ResultSession rs, DateTime dateStartdate, DateTime dateEnddate) {
		AnalysisSession analysisSession = null;
		AnalysisGenericDao<AnalysisSession> asDao= new AnalysisGenericDao<AnalysisSession>(AnalysisSession.class);
		analysisSession=asDao.getByID(rs.getId());
		
		AnalysisJmeterDao analysisJmeterDao=new AnalysisJmeterDao();
		if (dateStartdate == null) {
			dateStartdate = analysisJmeterDao.getStartDateTimeByResultSession(rs);
		}
		if (dateEnddate == null) {
			dateEnddate = analysisJmeterDao.getEndDateTimeByResultSession(rs);
		}
		analysisSession.setTestStartDate(dateStartdate);
		analysisSession.setTestEndDate(dateEnddate);
		asDao.update(analysisSession);
		
		return analysisSession;
	}
}
