package sme.perf.task.impl.anywhere.p1601;

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

public class RemoveMonitorFile extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;

		String mainfolder = parameters.getValue("mainfolder").toString();
		String isLocal = parameters.getValue("isLocal").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");	
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");
		List<Host> mariadbServerList = (List<Host>) hostParameters.getValue("mariadbServer",true);
		List<Host> shareFolderServerList = (List<Host>) hostParameters.getValue("shareFolderServer");
		List<Host> etcdServerList = (List<Host>) hostParameters.getValue("etcdServer");
		List<Host> hanaServerList = (List<Host>) hostParameters.getValue("hanaServer");
		
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);
		
		List<Host> allMachine=new ArrayList<Host>();
		allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		allMachine.addAll(eshopServerList);
		allMachine.addAll(etcdServerList);
		if (isLocal.equals("false")){
			allMachine.addAll(shareFolderServerList);
		}
		allMachine.addAll(hanaServerList);
		if (mariadbServerList!=null){
			allMachine.addAll(mariadbServerList);
		}

		logger.info("Start to Remove Monitor File.");
		try {
			String RemoveMonitorCmd = "cd " + mainfolder + " && rm *.txt";
			for (Host host : allMachine) {
				logger.info("Current machine:" + host.getIP());
				RunRemoteSSH rrs=new RunRemoteSSH(host.getIP(), host.getUserName(), host.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(RemoveMonitorCmd,true);
			}
			RemoveMonitorCmd = "cmd /c del /f /q C:\\B1pj\\ShareService\\OCC\\JmeterScript\\*.csv";
			RunRemoteSSH rrsWindows=new RunRemoteSSH(windowsServer.getIP(), windowsServer.getUserName(), windowsServer.getUserPassword());
			rrsWindows.setLogger(logger);
			rrsWindows.execute(RemoveMonitorCmd,true);
			
			logger.info("Finish to Remove Monitor File.");
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
			this.parameters = RemoveMonitorFile.getParameterTemplate();
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
			this.hostParameters = RemoveMonitorFile.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Remove Monitor File.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\","Windows Main Folder"));
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
		List<Host> etcdhostList = new ArrayList<Host>();
		etcdhostList.add(new Host("cnpvgvb1pf024.pvgl.sap.corp",
				"10.58.108.24", "root", "Initial0", "ETCD Server"));
		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,
				"Host List for ETCD Machine "));
		List<Host> sharefolderhostList = new ArrayList<Host>();
		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp",
				"10.58.108.25", "root", "Initial0", "Share Folder Server"));
		hostsParameter.put("shareFolderServer", new TaskParameterEntry(
				sharefolderhostList, "Host List for Share Folder Machine"));
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
		return new RemoveMonitorFile().getDescription();
	}
	
}
