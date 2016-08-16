package sme.perf.task.impl.op.b1920;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.task.impl.op.b1920.UninstallB1Client;
import sme.perf.utility.LogHelper;

public class UninstallB1ClientTest {

	@Test
	public void test() {

		Task uninstall = new UninstallB1Client();
		
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.110", "administrator",
				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.106", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.43", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.47", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.49", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.53", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.55", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.57", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.59", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.61", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.63", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.65", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.67", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.69", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.71", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.73", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.75", "administrator",
//				"Initial0", "B1 Client Host"));
//		clientHostList.add(new Host("CNPVG50819353", "10.58.136.77", "administrator",
//				"Initial0", "B1 Client Host"));
		
		hostsParameter.put("TestClientHosts", new TaskParameterEntry(clientHostList,
				"The hosts for   clients."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		
		uninstall.setHosts(template);
		
		TaskParameterMap parameters = UninstallB1Client.getParameterTemplate();
		//uninstall x86
//		try {
//			parameters.setValue("client_x86_or_x64", "x86");
//		} catch (ParameterMissingException e1) {
//			e1.printStackTrace();
//		}
//		uninstall.setParameters(parameters);
//		uninstall.setLogger(LogHelper.logger);
//		
//		try {
//			uninstall.execute();
//		} catch (ParameterMissingException e) {
//			fail(e.getMessage());
//		}
		//uninstall x64
//		try {
//			parameters.setValue("client_x86_or_x64", "x64");
//		} catch (ParameterMissingException e1) {
//			e1.printStackTrace();
//		}
//		uninstall.setParameters(parameters);
//		uninstall.setLogger(LogHelper.logger);
//		
//		try {
//			uninstall.execute();
//		} catch (ParameterMissingException e) {
//			fail(e.getMessage());
//		}

	}
}
