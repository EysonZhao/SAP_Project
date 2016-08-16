package sme.perf.execution.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.entity.Scenario;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.request.entity.TestRequest;

public class ExecutionTaskInfoDaoTest {


	GenericDao<Scenario> scenarioDao;
	GenericDao<Project> projectDao;
	GenericDao<ExecutionInfo> executionDao ;
	GenericDao<TestRequest> requestDao;
	Scenario scenario;
	Project project;
	TestRequest request;
	ExecutionInfo execInfo;
	String parameterTemplateJson = "{\"parameters\":{\"entries\":[]}}";
	
	@Before
	public void setUp() throws Exception {
		project = new Project();
		
		project.setDescription("Mock Project");
		project.setName("MockProject");
		project.setParameterTemplateJson(parameterTemplateJson);
		
		projectDao = new GenericDao<Project>(Project.class);
		project = projectDao.add(project);
		
		scenario = new Scenario();
		scenario.setDescription("Mock");
		scenario.setName("Mock");
		scenario.setTaskListJson(parameterTemplateJson);
		scenario.setProjectId(project.getId());
		
		scenarioDao = new GenericDao<Scenario>(Scenario.class);
		scenario = scenarioDao.add(scenario);
		
		request = new TestRequest();
		request.setProjectId(project.getId());
		request.setCategory("Benchmark");
		request.setName("Mock");
		requestDao = new GenericDao<TestRequest>(TestRequest.class);
		request = requestDao.add(request);
		
		execInfo = new ExecutionInfo();
		execInfo.setProjectParameterJson(parameterTemplateJson);
		execInfo.setScenarioId(scenario.getId());
		execInfo.setTestRequestId(request.getId());
		
		executionDao = new GenericDao<ExecutionInfo>(ExecutionInfo.class);
		execInfo = executionDao.add(execInfo);
	}

	@After
	public void tearDown() throws Exception {
		executionDao.delete(executionDao.getByID(execInfo.getId()));
		scenarioDao.delete(scenario);
		requestDao.delete(request);
		projectDao.delete(project);
	}
	
	@Test
	public void test() {
		try{
			ExecutionTaskInfo taskInfo = new ExecutionTaskInfo();
			
			taskInfo.setTaskHostParameterJson(parameterTemplateJson);
			taskInfo.setClassName("sme.perf.task.impl.InstallB1Client");
			taskInfo.setTaskParameterJson(parameterTemplateJson);
			taskInfo.setExecutionInfo(execInfo);
			taskInfo.setExecutionInfoId(execInfo.getId());
			
			ExecutionTaskInfo subTaskInfo = new ExecutionTaskInfo();

			subTaskInfo.setTaskHostParameterJson(parameterTemplateJson);
			subTaskInfo.setClassName("sme.perf.task.impl.InstallB1Client_Sub");
			subTaskInfo.setTaskParameterJson(parameterTemplateJson);
			subTaskInfo.setExecutionInfo(execInfo);
			subTaskInfo.setExecutionInfoId(execInfo.getId());
			
			taskInfo.setSubExecutionTaskInfoList(new ArrayList<ExecutionTaskInfo>());
			taskInfo.getSubExecutionTaskInfoList().add(subTaskInfo);
			subTaskInfo.setParent(taskInfo);
			
			List<ExecutionTaskInfo> taskList = new ArrayList<ExecutionTaskInfo>();
			taskList.add(taskInfo);
			taskList.add(subTaskInfo);
			
			execInfo.setTasks(taskList);
			execInfo = executionDao.update(execInfo);
			
			ExecutionTaskInfoDao execTaskInfoDao = new ExecutionTaskInfoDao();
			List<ExecutionTaskInfo> execTaskInfoList = execTaskInfoDao.getListByExecutionInfoId(execInfo.getId());
			assertTrue(execTaskInfoList.size() == 2);
			
			int deletedCount = execTaskInfoDao.deleteList(execTaskInfoList);
			assertTrue(deletedCount == 2);
			
			execTaskInfoList = execTaskInfoDao.getListByExecutionInfoId(execInfo.getId());
			assertTrue(execTaskInfoList.size() == 0);
			
		}catch(Exception ex){
			fail(ex.getMessage());
		}
	}

}
