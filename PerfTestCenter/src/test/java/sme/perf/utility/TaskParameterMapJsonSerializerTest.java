package sme.perf.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sme.perf.entity.Host;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.TaskParameterMapJsonSerializer;
import sme.perf.utility.LogHelper;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class TaskParameterMapJsonSerializerTest {


	
	@Test
	public void test() {
		try{
			
			Host newHost = new Host("H001", "10.58.136.1", "administrator", "Initial0", "host 1");
			LogHelper.debug(new JsonHelper().serializeObject(newHost));
			ObjectMapper om = new ObjectMapper();

			TaskParameterMap hostsParameter = new TaskParameterMap();
			List<Host> hostList = new ArrayList<Host>();
			hostList.add(new Host("H001", "10.58.136.1", "administrator", "Initial0", "host 1"));
			hostList.add(new Host("H002", "10.58.136.2", "administrator", "Initial0", "host 2"));
			hostsParameter.getParameters().put("csm", new TaskParameterEntry(hostList, "host list"));
			hostList = new ArrayList<Host>();
			hostList.add(new Host("H003", "10.58.136.3", "administrator", "Initial0", "host 3"));
			hostList.add(new Host("H004", "10.58.136.4", "administrator", "Initial0", "host 4"));
			hostsParameter.getParameters().put("occ", new TaskParameterEntry(hostList, "host list"));
			LogHelper.debug(new JsonHelper().serializeObject(hostsParameter));
			Assert.assertTrue(true);
		}
		catch(Exception ex){
			Assert.fail();
		}
	}

}
