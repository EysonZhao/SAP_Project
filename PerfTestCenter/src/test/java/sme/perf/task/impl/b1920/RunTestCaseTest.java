package sme.perf.task.impl.b1920;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterMap;
import sme.perf.task.impl.op.b1920.RunTestCase;
import sme.perf.utility.LogHelper;

public class RunTestCaseTest {

	@Test
	public void testExecute() {
		Task runTestCase = new RunTestCase();
		
		try {
			TaskParameterMap parameters = RunTestCase.getParameterTemplate();
			parameters.setValue("HostStartInterval", "20");
			LogHelper.debug("debug");
			parameters.setValue("LiteRunnerStartInterval", "20");
			parameters.setValue("LiteRunnerRunMode", "/t 2");
			parameters.setValue("TestSetName", "TS_201_PerfTestCenterExecution_Debug");
			parameters.setValue("EnvironmentConfiguration", "");
			parameters.setValue("TestCase", "");
			parameters.setValue("TotalUserNumber", "8");
//			parameters.setValue("RemoteTestCaseRepository", "\\\\10.58.120.189\\TestCaseRepository");
//			parameters.setValue("LocalTestCaseRepository", "C:\\TestCaseRepository");

			runTestCase.setParameters(parameters);
			
			TaskParameterMap hostParameters = RunTestCase.getHostsTemplate();
			List<Host> clientList = new ArrayList<Host>();
			clientList.add(new Host("CNPVG50817977", "10.58.136.104", "administrator", "Initial0", "B1 Client Host"));
			clientList.add(new Host("CNPVG50817980", "10.58.136.110", "administrator", "Initial0", "B1 Client Host"));
			hostParameters.setValue("TestClientHosts", clientList);
			
			runTestCase.setHosts(hostParameters);
			runTestCase.setLogger(LogHelper.getLogger());
			
//			Result result = runTestCase.execute();
		} catch (ParameterMissingException e) {
			fail();
		}
	}

}
