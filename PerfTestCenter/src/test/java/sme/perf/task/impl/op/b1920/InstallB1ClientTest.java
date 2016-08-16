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
import sme.perf.utility.LogHelper;

public class InstallB1ClientTest {

	@Test
	public void test() {
		/*
		Task installB1Client = new InstallB1Client();
		
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();

		clientHostList.add(new Host("CNPVG50819353", "10.58.136.43", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.47", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.49", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.53", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.55", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.57", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.59", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.61", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.63", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.65", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.67", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.69", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.71", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.73", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.75", "administrator",
				"Initial0", "B1 Client Host"));
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.77", "administrator",
				"Initial0", "B1 Client Host"));
		
		hostsParameter.put("TestClientHosts", new TaskParameterEntry(clientHostList,
				"The hosts for   clients."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		
		installB1Client.setHosts(template);
		TaskParameterMap paramMap = InstallB1Client.getParameterTemplate();
		
		try {
			paramMap.setValue("buildPath", "\\\\10.58.136.41\\e$\\B1Builds\\9.2_PL03_HANA_1462052\\Upgrade_920.130.03_CD_1462052_HANA.rar");
			paramMap.setValue("client_x86_or_x64", "x64");
		} catch (ParameterMissingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		installB1Client.setParameters(paramMap);
		installB1Client.setLogger(LogHelper.logger);
		try{
			installB1Client.execute();
		}
		catch(Exception e){
			fail(e.getMessage());
		}
		*/
	}

}
