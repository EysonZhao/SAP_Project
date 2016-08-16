package sme.perf.task.impl.anywhere.p1604;

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
import sme.perf.utility.LogHelper;
import sme.perf.utility.RunRemoteSSH;

public class CleanEnvironment extends Task {
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;

		String mainfolder = parameters.getValue("mainfolder").toString();
		String hanaInstanceFull = parameters.getValue("hanaInstanceFull").toString();
		String hanaPasswd = parameters.getValue("hanaPasswd").toString();
		String hanaUser = parameters.getValue("hanaUser").toString();
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		String initInstanceBackupPath = parameters.getValue("initInstanceBackupPath").toString();
		
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");
		List<Host> mariadbServerList = (List<Host>) hostParameters.getValue("mariadbServer",true);
		List<Host> etcdServerList = (List<Host>) hostParameters.getValue("etcdServer");
		
		List<Host> hanaServerList = (List<Host>) hostParameters.getValue("hanaServer");
		Host hanaServer=hanaServerList.get(0);
		
		List<Host> allMachine=new ArrayList<Host>();
		allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		allMachine.addAll(eshopServerList);

		logger.info("Start to Clean Environment");
		try {
			RunRemoteSSH rrs;
			for (Host host:allMachine){
				logger.info("Current machine:"+host.getIP());
				String deleteBuildCmd = "cd "
						+ mainfolder
						+ " && docker stop $(docker ps -a -q)\ndocker rm $(docker ps -a -q)\ndocker rmi -f $(docker images -q)\n"
						+ "rm -rf /opt/sap /opt/logs\nkillall -9 python";
				if(host.equals(csmServer)){
					deleteBuildCmd=deleteBuildCmd+" && killall -9 java";
				}
				rrs=new RunRemoteSSH(host.getIP(),host.getUserName(),host.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(deleteBuildCmd,true);
			}
			for (Host host:etcdServerList){
				logger.info("Current machine:"+host.getIP());
				String deleteETCDCmd="stop etcd && rm -rf /opt/etcd/data.etcd && start etcd";
				rrs=new RunRemoteSSH(host.getIP(),host.getUserName(),host.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(deleteETCDCmd);
			}
			
			logger.info("Current machine:"+hanaServer.getIP());
			int n = hanaInstanceFull.indexOf(":");
			String instanceNumber = hanaInstanceFull.substring(n + 2, n + 4);
			String instanceID = hanaUser.toUpperCase().substring(0, 3);
			String instanceRecoveryCmd = "/usr/sap/" + instanceID + "/HDB"
					+ instanceNumber + "/exe/sapcontrol -nr " + instanceNumber
					+ " -function Stop && sleep 15 && /bin/sh /usr/sap/"
					+ instanceID + "/HDB" + instanceNumber
					+ "/HDBSettings.sh recoverSys.py --wait --password "
					+ hanaPasswd + " --command=\"RECOVER DATA ALL USING FILE ('"
					+ initInstanceBackupPath
					+ "/COMPLETE_DATA_BACKUP') CLEAR LOG\" & sleep 15";
			rrs=new RunRemoteSSH(hanaServer.getIP(), hanaUser, hanaPasswd);
			rrs.setLogger(logger);
			rrs.execute(instanceRecoveryCmd);

			logger.info("Finish to Clean Environment.");
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			LogHelper.error(e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}
	}

	@Override
	public String getDescription() {
		return "Task for Clean Environment.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115","Full Hana Instance URL"));
		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
		parameters.put("hanaUser", new TaskParameterEntry("cdbadm","Hana User Name"));
		parameters.put("initInstanceBackupPath", new TaskParameterEntry("/usr/sap/hc/dbbackup/InitStatus","Linux Path for Keep Init Instance Backup File"));
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
		List<Host> occhostList = new ArrayList<Host>();
		occhostList.add(new Host("cnpvgvb1pf007.pvgl.sap.corp", "10.58.8.41", "root",
				"Initial0", "OCC Server 1"));
		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,
				"Host List for OCC Machine"));
		List<Host> jobhostList = new ArrayList<Host>();
		jobhostList.add(new Host("cnpvgvb1pf008.pvgl.sap.corp", "10.58.8.42", "root",
				"Initial0", "Job Server 1"));
		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
				"Host List for Job Machine"));
		List<Host> eshophostList = new ArrayList<Host>();
		eshophostList.add(new Host("cnpvgvb1pf011.pvgl.sap.corp", "10.58.8.45", "root",
				"Initial0", "Eshop Server"));
		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,
				"Host List for Eshop Machine"));
		List<Host> WindowshostList = new ArrayList<Host>();
		WindowshostList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
				"Initial0", "Windows Server"));
		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList,
				"Host List for WindowsMachine"));
		List<Host> etcdhostList = new ArrayList<Host>();
		etcdhostList.add(new Host("cnpvgvb1pf024.pvgl.sap.corp", "10.58.108.24", "root",
				"Initial0", "ETCD Server"));
		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,
				"Host List for ETCD Machine "));
		List<Host> hanahostList = new ArrayList<Host>();
		hanahostList.add(new Host("CNPVG50819783.pvgl.sap.corp", "10.58.120.235", "root",
				"Initial0", "HANA Server"));
		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,
				"Host List for HANA Machine"));
		List<Host> mariadbhostList = new ArrayList<Host>();
		mariadbhostList.add(new Host("cnpvgvb1pf012.pvgl.sap.corp", "10.58.8.46", "root",
				"Initial0", "Mariadb Server"));
		hostsParameter.put("mariadbServer", new TaskParameterEntry(mariadbhostList,
				"Host List for MariaDB Machine"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new CleanEnvironment().getDescription();
	}
}
