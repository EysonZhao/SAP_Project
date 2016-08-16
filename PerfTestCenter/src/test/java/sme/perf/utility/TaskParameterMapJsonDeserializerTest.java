package sme.perf.utility;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import sme.perf.task.TaskParameterMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskParameterMapJsonDeserializerTest {

	@Test
	public void test() {
		try{
			String json = "{\"parameters\":{\"parameters\":{\"entries\":[{\"name\":\"BasePath\",\"value\":\"C:\\\\TA\",\"description\":\"C:\\\\TA\"},{\"name\":\"BuildPath\",\"value\":\"C:\\\\TA\\\\Build\",\"description\":\"C:\\\\TA\\\\Build\"},{\"name\":\"WinrarPath\",\"value\":\"Essential\\\\Winrar\\\\Winrar\",\"description\":\"Essential\\\\Winrar\\\\Winrar\"}]}}}";
			ObjectMapper om = new ObjectMapper();
			
			json = " {\"parameters\":{\"entries\":[{\"name\":\"csm\",\"value\":[{\"id\":0,\"hostName\":\"H001\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 1\",\"ip\":\"10.58.136.1\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"}],\"description\":\"host list\"},{\"name\":\"occ\",\"value\":[{\"id\":0,\"hostName\":\"H003\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 3\",\"ip\":\"10.58.136.3\"},{\"id\":0,\"hostName\":\"H004\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 4\",\"ip\":\"10.58.136.4\"}],\"description\":\"host list\"}]}}";
			TaskParameterMap hostParameters = om.readValue(json, TaskParameterMap.class);
			assertTrue(hostParameters.getParameters().size() == 2);
			assertTrue(((List)hostParameters.getParameters().get("occ").getValue()).size() == 2);
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}

}
