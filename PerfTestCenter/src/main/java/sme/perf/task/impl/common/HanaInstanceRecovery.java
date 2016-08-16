package sme.perf.task.impl.common;

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

public class HanaInstanceRecovery extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;

		String hanaInstanceFull = parameters.getValue("hanaInstanceFull").toString();
		String hanaPasswd = parameters.getValue("hanaPasswd").toString();
		String hanaUser = parameters.getValue("hanaUser").toString();
		String instanceBackupPath = parameters.getValue("instanceBackupPath").toString();
		List<Host> hanaServerList = (List<Host>) hostParameters.getValue("hanaServer");
		Host hanaServer=hanaServerList.get(0);
		
		try {
			RunRemoteSSH rrsHANAUser=new RunRemoteSSH(hanaServer.getIP(), hanaUser, hanaPasswd);
			rrsHANAUser.setLogger(logger);
			logger.info("Start to Recovery Hana Instance.");
			int n = hanaInstanceFull.indexOf(":");
			String instanceNumber = hanaInstanceFull.substring(n + 2, n + 4);
			String instanceID = hanaUser.toUpperCase().substring(0, 3);
			String removeLogCmd="rm /usr/sap/" + instanceID + "/HDB"
					+ instanceNumber + "/backup/log/*";
			rrsHANAUser.execute(removeLogCmd,true,0,600000);
			String instanceRecoveryCmd = "/usr/sap/" + instanceID + "/HDB"
					+ instanceNumber + "/HDB stop && sleep 15 && /bin/sh /usr/sap/"
					+ instanceID + "/HDB" + instanceNumber
					+ "/HDBSettings.sh recoverSys.py --wait --password "
					+ hanaPasswd + " --command=\"RECOVER DATA ALL USING FILE ('"
					+ instanceBackupPath
					+ "') CLEAR LOG\" & sleep 15";
			rrsHANAUser.execute(instanceRecoveryCmd,false,0,3600000);
			
			logger.info("Finish to Recovery Hana Instance.");
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}	
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		if (null == this.parameters) {
			this.parameters = HanaInstanceRecovery.getParameterTemplate();
		}
		return this.parameters;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public void setHosts(TaskParameterMap hostList) {
		this.hostParameters = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		if (null == this.hostParameters) {
			this.hostParameters = HanaInstanceRecovery.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Hana Instance Recovery.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();	
		//Common
		parameters.put("instanceBackupPath", new TaskParameterEntry("/root/perftest/hanatmpbackup/complete_backup","Linux Path with prefix  for Keep Instance Backup File"));
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115","Full Hana Instance URL"));
		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
		parameters.put("hanaUser", new TaskParameterEntry("cdbadm","Hana User Name"));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> hanaServerList = new ArrayList<Host>();
		hanaServerList.add(new Host("cnpvg50861384.pvgl.sap.corp", "10.58.120.235", "root",
				"Initial0", "HANA Server"));
		hostsParameter.put("hanaServer", new TaskParameterEntry(hanaServerList,
				"Host List for Hana Server."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new HanaInstanceRecovery().getDescription();
	}
}
