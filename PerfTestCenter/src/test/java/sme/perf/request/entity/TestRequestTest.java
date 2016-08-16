package sme.perf.request.entity;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.utility.JsonHelper;

public class TestRequestTest {

	@Test
	public void test() {
		try{
			GenericDao<Project> prjDao = new GenericDao<Project>(Project.class);
			List<Project> prjList = prjDao.getAll();
			assertTrue(prjList.size() > 0);
			
			TestRequest request = new TestRequest();
			request.setBackground("Patch Release");
			request.setBuild("910234_CL234123");
			request.setCategory("Benchmark");
			request.setProjectId(prjList.get(0).getId());
			
			GenericDao<TestRequest> requestDao = new GenericDao<TestRequest>(TestRequest.class);
			request = requestDao.add(request);
			assertTrue(request.getId() > 0);
			
			JsonHelper<TestRequest> jsonHelper= new JsonHelper<TestRequest>();
			String jsonString = jsonHelper.serializeObject(request);
			
			assertTrue(jsonString.isEmpty() == false);
			
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}

}
