package sme.perf.execution.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sme.perf.controller.ExecutionGroupController;
import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.entity.Scenario;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.request.entity.TestRequest;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.SameTaskParams;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;

public class ExecutionInfoDaoTest {

	ExecutionInfoDao dao;
	GenericDao<Scenario> scenarioDao;
	GenericDao<Project> projectDao;
	GenericDao<TestRequest> requestDao;
	Scenario scenario;
	Project project;
	TestRequest request;
	ExecutionInfo execInfo;
	String parameterTemplateJson = "{\"parameters\":{\"entries\":[]}}";

	@Before
	public void setUp() {
//		project = new Project();
//		project.setName("MockProject");
//
//		projectDao = new GenericDao<Project>(Project.class);
//		project = projectDao.add(project);
//
//		scenario = new Scenario();
//		scenario.setName("MockScenario");
//		scenario.setProjectId(project.getId());
//
//		scenarioDao = new GenericDao<Scenario>(Scenario.class);
//		scenario = scenarioDao.add(scenario);
//
//		request = new TestRequest();
//		request.setProjectId(project.getId());
//		request.setCategory("Benchmark");
//		request.setName("Mock");
//		requestDao = new GenericDao<TestRequest>(TestRequest.class);
//		request = requestDao.add(request);
//
//		dao = new ExecutionInfoDao();
	}

	@After
	public void tearDown() {
		dao = new ExecutionInfoDao();
//		requestDao.delete(request);
//		scenarioDao.delete(scenario);
//		projectDao.delete(project);
	}

	@Test
	public void testGetFinished(){
//		try{
//			List<ExecutionInfo> retList = dao.listFinished();
//		}catch (Exception ex){
//			fail();
//		}
	}
	@Test
	public void testAddExecutionInfoWithSubTaskTree() {
//		try {
//			// create a tree with 3 level and each level has 3 nodes
//			ExecutionInfo execInfo = new ExecutionInfo();
//			execInfo.setName("test Execution");
//			execInfo.setScenarioId(scenario.getId());
//			execInfo.setTestRequestId(request.getId());
//			execInfo = dao.add(execInfo);
//			
//			assertTrue(execInfo.getId() != 0);
//			
//			List<ExecutionTaskInfo> taskList = new ArrayList<ExecutionTaskInfo>();
//			execInfo.setTasks(taskList);
//			for (int x = 0; x < 3; x++) {
//				ExecutionTaskInfo subTask1 = new ExecutionTaskInfo();
//				subTask1.setExecutionInfoId(execInfo.getId());
//				subTask1.setSn(x);
//				subTask1.setClassName(String.format("class_%d", x));
//				execInfo.getTasks().add(subTask1);
//				
//				List<ExecutionTaskInfo> subTaskList1 = new ArrayList<ExecutionTaskInfo>();
//				subTask1.setSubExecutionTaskInfoList(subTaskList1);
//
//				for (int y = 0; y < 3; y++) {
//					ExecutionTaskInfo subTask2 = new ExecutionTaskInfo();
//					subTask2.setParent(subTask1);
////					subTask2.setExecutionInfoId(execInfo.getId());
//					subTask2.setSn(y);
//					subTask2.setClassName(String.format("class_%d_%d", x, y));
//
//					subTaskList1.add(subTask2);
//				}				
//			}
//			execInfo = dao.update(execInfo);
//			execInfo = dao.getByID(execInfo.getId());
//			assertTrue(execInfo.getTasks().size() == 3);
//			for(int x=0 ; x<2 ; x++){
//				assertTrue(execInfo.getTasks().get(x).getSn() <= execInfo.getTasks().get(x+1).getSn());
//				assertTrue(execInfo.getTasks().get(x).getSubExecutionTaskInfoList().size() == 3);
//				assertTrue(execInfo.getTasks().get(x+1).getSubExecutionTaskInfoList().size() == 3);
//				for(int y=0 ; y<2 ; y++){
//					assertTrue(execInfo.getTasks().get(x).getSubExecutionTaskInfoList().get(y).getSn() <= 
//							execInfo.getTasks().get(x).getSubExecutionTaskInfoList().get(y + 1).getSn());
//					}
//			}
//			dao.delete(execInfo);
//		} catch (Exception ex) {
//			LogHelper.error(ex);
//			fail("Exception is caught");
//		}
	}
	@Test
	public void getParas(){
//		ExecutionInfoDao mydao = new ExecutionInfoDao();
//		
//		long ids[]={172,173,174};
//		Map<String , Object> same = new HashMap<String, Object>();
//		
//		for(int i=0;i<ids.length;i++){
//			ExecutionInfo exe = mydao.getByID(ids[i]);
//			
//			JsonHelper<TaskParameterMap> jsonHelper = new JsonHelper<TaskParameterMap>();
//			TaskParameterMap taskParams = jsonHelper.deserialObject(exe.getTasksParameterJson(), TaskParameterMap.class);		
//			
//			Iterator it = taskParams.getParameters().entrySet().iterator();
//			if(i==0){
//				while(it.hasNext()){
//					Map.Entry me = (Map.Entry)it.next();
//					String key = (String) me.getKey();
//					Object value = ((TaskParameterEntry)me.getValue()).getValue();	
//					
//					same.put(key, value);
//				}
//			}
//			else{
//				while(it.hasNext()){
//					Map.Entry me = (Map.Entry)it.next();
//					String key = (String) me.getKey();
//					Object value = ((TaskParameterEntry)me.getValue()).getValue();	
//					if(same.containsKey(key)&&!(same.get(key).equals(value))){
//							same.remove(key);
//					}
//				}
//			}
//		}
//		
//		System.out.println(same);
//		
//		List <SameTaskParams> samelist = new ArrayList <SameTaskParams>();
//		Iterator it = same.entrySet().iterator();
//		while(it.hasNext()){
//			Map.Entry me = (Map.Entry)it.next();
//			String key = (String)me.getKey();
//			Object value = me.getValue();	
//			SameTaskParams param = new SameTaskParams(key,value);
//			samelist.add(param);
//		}
//		
//		System.out.println(samelist);
//		
//		ExecutionGroupController egc = new ExecutionGroupController();
//		
//		Object p1= "{name=TotalUserNumber, value=1024}";
//		Object p2= "{name=hanaPasswd, value=12345678}";
//
//		
//		List <Object> samelist = new ArrayList <Object>();
//		samelist.add(p1);
//		samelist.add(p2);
//		
//		egc.modifyParams(15,samelist);


		
		
	}
	
	

}
