package sme.perf.task.impl.anywhere.p1603;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.RunRemoteSSH;

public class UpdateGlobalSetting extends Task {

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		
		logger.info("Start to Update GlobalSetting.");
		try {
			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(),csmServer.getUserPassword());
			rrsCSM.setLogger(logger);
			//add Update Tenants DB for "PURPOSE"
			String updateSettingCommonCmd="mysql -h "+csmServer.getIP()+" -uroot -p"+mariaDBPasswd+" -e \"UPDATE CSM.GLOBALSETTING SET VALUE=9999 WHERE \\`KEY\\` = 'MaxUsersPerTrialCustomer'\"";
			rrsCSM.execute(updateSettingCommonCmd);
			logger.info("Finish to Update GlobalSetting.");
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}
	}

	@Override
	public String getDescription() {
		return "Task for UpdateGlobalSetting.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> CSMhostList = new ArrayList<Host>();
		CSMhostList.add(new Host("cnpvgvb1pf009.pvgl.sap.corp", "10.58.8.43", "root",
				"Initial0", "CSM Server"));
		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,
				"Host List for CSM,SP,IDP,etc."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new UpdateGlobalSetting().getDescription();
	}
}
