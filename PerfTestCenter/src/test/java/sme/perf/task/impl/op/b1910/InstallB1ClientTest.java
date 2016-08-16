package sme.perf.task.impl.op.b1910;

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
public class InstallB1ClientTest extends InstallB1Client {

	@Test
	public void test() {
		
		Task installB1Client = new InstallB1Client();
		
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();

		clientHostList.add(new Host("CNPVG50819353", "10.58.136.110", "administrator",
				"Initial0", "B1 Client Host"));
		
		hostsParameter.put("TestClientHosts", new TaskParameterEntry(clientHostList,
				"The hosts for   clients."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		
		installB1Client.setHosts(template);
		TaskParameterMap paramMap = InstallB1Client.getParameterTemplate();
		
//		try {
//			paramMap.setValue("buildPath", "\\\\10.58.6.49\\builds_cn\\SBO\\9.1_COR\\910.230.13_CNPVG50882200DV_SBO_EMEA_9.1_COR_020616_161714_1467917.HANA\\Upgrade_910.230.13_CD_1467917_HANA.rar");
//			paramMap.setValue("client_x86_or_x64", "x86");
//			paramMap.setValue("licenseServer", "10.58.136.102:40000");
//		} catch (ParameterMissingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		installB1Client.setParameters(paramMap);
//		installB1Client.setLogger(LogHelper.logger);
//		try{
//			installB1Client.execute();
//		}
//		catch(Exception e){
//			fail(e.getMessage());
//		}
		
	}

}
