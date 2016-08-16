package sme.perf.entity;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import sme.perf.dao.GenericDao;

public class ProjectTest {

	@Test
	public void test() {
		try{
			String parameterTemplateJson = "{\"parameters\":{\"entries\":[{\"name\":\"csm\",\"value\":[{\"id\":0,\"hostName\":\"H001\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 1\",\"ip\":\"10.58.136.1\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"}],\"description\":\"host list\"},{\"name\":\"occ\",\"value\":[{\"id\":0,\"hostName\":\"H003\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 3\",\"ip\":\"10.58.136.3\"},{\"id\":0,\"hostName\":\"H004\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 4\",\"ip\":\"10.58.136.4\"}],\"description\":\"host list\"}]}}";
			
			//create a 9.1 project
			Project b1901 = new Project();
			b1901.setDescription("SAP Business One 9.1");
			b1901.setName("B1 9.1");
			b1901.setParameterTemplateJson(parameterTemplateJson);
			
			GenericDao<Project> prjDao = new GenericDao<Project>(Project.class);
			b1901 = prjDao.add(b1901);
			assertTrue(b1901.getId() >0);
			assertTrue(b1901.getParameterTemplateJson().equals(parameterTemplateJson));
			
			//create 2 test scenario for 9.1 project
			GenericDao<Scenario> scenarioDao = new GenericDao<Scenario>(Scenario.class);
			Scenario[] b1901Scenarios = new Scenario[2]; 
			for(int i=0 ; i<b1901Scenarios.length ; i++){
				b1901Scenarios[i] = new Scenario();
				b1901Scenarios[i].setDescription("64 concurrent users add AR long document " + i);
				b1901Scenarios[i].setName("BenchmarkTest_" + i);
				b1901Scenarios[i].setProjectId(b1901.getId());
				b1901Scenarios[i].setTaskListJson(parameterTemplateJson);
				b1901Scenarios[i] = scenarioDao.add(b1901Scenarios[i]);
			}
			
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	@Test
	public void testStatusReport(){
		GenericDao<Project> prjDao = new GenericDao<Project>(Project.class);
		List<Project> prjList = prjDao.getAll();
		
	}
}
