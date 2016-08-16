package sme.perf.task.impl.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.LogHelper;

public class HanaInstanceRecoveryTest {

	@Test
	public void test() {
//		HanaInstanceRecovery task = new HanaInstanceRecovery();
//		TaskParameterMap param = HanaInstanceRecovery.getParameterTemplate();
//		TaskParameterMap hostParam = HanaInstanceRecovery.getHostsTemplate();
//		
//		try {
//			param.setValue("hanaInstanceFull", "10.58.136.102:35015");
//			param.setValue("hanaUser", "pdbadm");
//			param.setValue("hanaPasswd", "12345678");
//			param.setValue("instanceBackupPath", "/hana/102_dbbackup/CRDB_New_rev102.5_9.2_PL03_1464066/rev102.5");
//			
//			Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
//			List<Host> hanaServerList = new ArrayList<Host>();
//			hanaServerList.add(new Host("cnpvg50861384", "10.58.136.103", "root",
//					"Initial0", "HANA Server"));
//			
//			hostParam.setValue("hanaServer", hanaServerList);
//			
//			task.setHosts(hostParam);
//			task.setLogger(LogHelper.getLogger());
//			task.setParameters(param);
////			task.execute();
//		} catch (ParameterMissingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
////		fail("Not yet implemented");
	}

}
