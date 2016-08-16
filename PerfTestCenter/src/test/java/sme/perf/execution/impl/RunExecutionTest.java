package sme.perf.execution.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import sme.perf.execution.State;
import sme.perf.execution.entity.ExecutionTaskInfo;

import org.junit.Before;
import org.junit.Test;

import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.task.Result;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;

public class RunExecutionTest {
	
	ExecutionInfo execInfo;
	String taskParamJson = "{\"parameters\":{\"entries\":[{\"name\":\"BasePath\",\"value\":\"C:\\\\TA\",\"description\":\"C:\\\\TA\"},{\"name\":\"BuildPath\",\"value\":\"C:\\\\TA\\\\Build\",\"description\":\"C:\\\\TA\\\\Build\"},{\"name\":\"WinrarPath\",\"value\":\"Essential\\\\Winrar\\\\Winrar\",\"description\":\"Essential\\\\Winrar\\\\Winrar\"}]}}";
	String hostParamJson = "{\"parameters\":{\"entries\":[{\"name\":\"b1clients\",\"value\":[{\"id\":0,\"hostName\":\"H001\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 1\",\"ip\":\"10.58.136.1\"},{\"id\":0,\"hostName\":\"H002\",\"userName\":\"administrator\",\"userPassword\":\"Initial0\",\"description\":\"host 2\",\"ip\":\"10.58.136.2\"}],\"description\":\"b1 client list\"}]}}";
	@Before
	public void setUp() throws Exception {
		execInfo = new ExecutionInfo();
		execInfo.setId(0);
		
		ExecutionTaskInfo taskInfo = new ExecutionTaskInfo();
		taskInfo.setExecutionInfoId(execInfo.getId());
		taskInfo.setClassName("sme.perf.task.impl.mock.InstallB1ClientMock");
		taskInfo.setTaskParameterJson(taskParamJson);
		taskInfo.setTaskHostParameterJson(hostParamJson);
		
		List<ExecutionTaskInfo> taskList = new ArrayList<ExecutionTaskInfo>();
		taskList.add(taskInfo);
		execInfo.setTasks(taskList);
	}
	
}
