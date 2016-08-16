package sme.perf.execution.entity;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.entity.Scenario;
import sme.perf.execution.State;
import sme.perf.request.entity.TestRequest;

public class ExecutionInfoTest {

	Scenario scenario;
	Project project;
	TestRequest request;
	GenericDao<TestRequest> requestDao = new GenericDao<TestRequest>(TestRequest.class);
	GenericDao<Scenario> snoDao = new GenericDao<Scenario>(Scenario.class);
	GenericDao<Project> prjDao = new GenericDao<Project>(Project.class);
	
	String parameterTemplateJson = "{\"parameters\":{\"entries\":[{\"name\":\"csm\",\"value\":[{\"id\":0,\"hostName\":\"H001\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 1\",\"ip\":\"10.58.136.1\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"}],\"description\":\"host list\"},{\"name\":\"occ\",\"value\":[{\"id\":0,\"hostName\":\"H003\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 3\",\"ip\":\"10.58.136.3\"},{\"id\":0,\"hostName\":\"H004\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 4\",\"ip\":\"10.58.136.4\"}],\"description\":\"host list\"}]}}";
	@Before
	public void setUp() throws Exception {
		project = new Project();
		project.setDescription("Mock Project");
		project.setName("MockProject");
		project.setParameterTemplateJson(parameterTemplateJson);
		project = prjDao.add(project);
		
		scenario = new Scenario();
		scenario.setDescription("Mock");
		scenario.setName("Mock");
		scenario.setTaskListJson(parameterTemplateJson);
		scenario.setProjectId(project.getId());
		
		scenario = snoDao.add(scenario);
		
		request = new TestRequest();
		request.setProjectId(project.getId());
		request.setBuild("test");
		requestDao.add(request);
	}

	@After
	public void tearDown() throws Exception {
		snoDao.delete(scenario);
		requestDao.delete(request);
		prjDao.delete(project);
	}
	
	@Test
	public void test() {
		try{

			ExecutionInfo execInfo = new ExecutionInfo();
			execInfo.setProjectParameterJson(parameterTemplateJson);
//			execInfo.setScenarioParameterJson(parameterTemplateJson);
			execInfo.setScenarioId(scenario.getId());

			execInfo.setCreateDate(DateTime.now());
			execInfo.setStartDate(DateTime.now());
			execInfo.setEndDate(DateTime.now());
			
			execInfo.setTestRequestId(request.getId());
			execInfo.setState(State.Finished);
			GenericDao<ExecutionInfo> execDao = new GenericDao<ExecutionInfo>(ExecutionInfo.class);
			execInfo = execDao.add(execInfo);
			assertTrue(execInfo.getId()>0);
			assertTrue(execInfo.getProjectParameterJson().equals(parameterTemplateJson));
//			assertTrue(execInfo.getScenarioParameterJson().equals(parameterTemplateJson));

			execDao.delete(execInfo);
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
}
