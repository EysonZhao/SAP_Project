package sme.perf.execution.entity;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.entity.Scenario;
import sme.perf.request.entity.TestRequest;
import sme.perf.utility.LogHelper;

public class ExecutionTaskInfoTest {

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
//		execInfo.setScenarioParameterJson(parameterTemplateJson);
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
//
//	@Test
//	public void test() {
//		try{
//			ExecutionTaskInfo taskInfo = new ExecutionTaskInfo();
//			taskInfo.setExecutionInfoId(execInfo.getId());
//			taskInfo.setTaskHostParameterJson(parameterTemplateJson);
//			taskInfo.setClassName("sme.perf.task.impl.InstallB1Client");
//			taskInfo.setTaskParameterJson(parameterTemplateJson);
//			
//			GenericDao<ExecutionTaskInfo> taskDao = new GenericDao<ExecutionTaskInfo>(ExecutionTaskInfo.class);
//			taskInfo = taskDao.add(taskInfo);
//			assertTrue(taskInfo.getId() > 0);
//			assertTrue(taskInfo.getTaskHostParameterJson().equals(parameterTemplateJson));
//			assertTrue(taskInfo.getTaskParameterJson().equals(parameterTemplateJson));
//			
//			taskDao.delete(taskInfo);
//		}
//		catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}
	
	@Test
	public void testSubTask(){
		try{
			ExecutionTaskInfo taskInfo = new ExecutionTaskInfo();
			
			taskInfo.setTaskHostParameterJson(parameterTemplateJson);
			taskInfo.setClassName("sme.perf.task.impl.InstallB1Client");
			taskInfo.setTaskParameterJson(parameterTemplateJson);
			taskInfo.setExecutionInfoId(execInfo.getId());
			
			ExecutionTaskInfo subTaskInfo = new ExecutionTaskInfo();

			subTaskInfo.setTaskHostParameterJson(parameterTemplateJson);
			subTaskInfo.setClassName("sme.perf.task.impl.InstallB1Client_Sub");
			subTaskInfo.setTaskParameterJson(parameterTemplateJson);
			//the sub task need to reference to execInfo too.
			subTaskInfo.setExecutionInfoId(execInfo.getId());
			
			taskInfo.setSubExecutionTaskInfoList(new ArrayList<ExecutionTaskInfo>());
			taskInfo.getSubExecutionTaskInfoList().add(subTaskInfo);
			subTaskInfo.setParent(taskInfo);
			
			List<ExecutionTaskInfo> taskList = new ArrayList<ExecutionTaskInfo>();
			taskList.add(taskInfo);
			
			execInfo.setTasks(taskList);
			execInfo = executionDao.update(execInfo);
			execInfo = executionDao.getByID(execInfo.getId());
			
			assertTrue(execInfo.getTasks().size() == 2);
			assertTrue(execInfo.getTasks().get(0).getSubExecutionTaskInfoList().size() == 1);
			assertTrue(execInfo.getTasks().get(0).getId() != 0);
			assertTrue(execInfo.getTasks().get(0).getSubExecutionTaskInfoList().get(0).getId() !=0);

//			GenericDao<ExecutionTaskInfo> execTaskInfoDao = new GenericDao<ExecutionTaskInfo>(ExecutionTaskInfo.class);
//			taskInfo = execInfo.getTasks().get(0);
//			subTaskInfo = taskInfo.getSubExecutionTaskInfoList().get(0);
//			subTaskInfo.setParentId(0);
//			taskInfo.getSubExecutionTaskInfoList().remove(subTaskInfo);
//			subTaskInfo.setParent(null);
//			execTaskInfoDao.delete(execTaskInfoDao.getByID(subTaskInfo.getId()));
//			execInfo.getTasks().remove(taskInfo);
//			execTaskInfoDao.delete(taskInfo);

		}
		catch(Exception ex){
			LogHelper.error(ex);
			fail(ex.getMessage());
		}
	}

}
