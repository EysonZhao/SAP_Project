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

public class Debug extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException{
		this.status=Status.Running;

		String mainfolder = parameters.getValue("mainfolder").toString();
		//List<Host> csmServerList = (List<Host>) hostList.getValue("csmServer");	
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		//List<Host> eshopServerList = (List<Host>) hostList.getValue("eshopServer");

		List<Host> allMachine=new ArrayList<Host>();
		//allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		//allMachine.addAll(eshopServerList);
		
		int startSchemaNumber = Integer.parseInt(parameters.getValue("startSchemaNumber").toString());
		int endSchemaNumber = Integer.parseInt(parameters.getValue("endSchemaNumber").toString());
		
		
		//DEBUG
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		
		logger.info("Start to Debug.");
		try {
//			for (Host host : allMachine) {
//				logger.info("Current machine:" + host.getIP());
//				String DebugCmd="cd "+mainfolder+" && echo startSchemaNumber="+startSchemaNumber+" endSchemaNumber="+endSchemaNumber;
//				logger.info("DebugCmd is '"+DebugCmd+"'");
//				RunRemoteSSH rrs=new RunRemoteSSH(host.getIP(),host.getUserName(),host.getUserPassword());
//				rrs.setLogger(logger);
//				rrs.execute(DebugCmd);
//			}
			logger.info(startSchemaNumber);
			logger.info(endSchemaNumber);
//			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(), csmServer.getUserPassword());
//			rrsCSM.setLogger(logger);
//			String getIDPurlCMD = "mysql -uroot -p"+mariaDBPasswd+" -e \"select T0.URL FROM CSM.SERVICE_CLUSTER T0, CSM.BASE_CLUSTER T1 WHERE T0.BASE_CLUSTER_ID=T1.ID AND T1.\\`TYPE\\`='IDP'\"";
//			String getSPurlCMD = "mysql -uroot -p"+mariaDBPasswd+" -e \"select T0.URL FROM CSM.SERVICE_CLUSTER T0, CSM.BASE_CLUSTER T1 WHERE T0.BASE_CLUSTER_ID=T1.ID AND T1.\\`TYPE\\`='SOLUTIONPORTAL'\"";
//			
//			String IDPFullUrl=rrsCSM.executeWithReturnString(getIDPurlCMD).replace("\r", "").replace("\n","").replace("URL", "").trim();
//			String SPFullUrl=rrsCSM.executeWithReturnString(getSPurlCMD).replace("\r", "").replace("\n","").replace("URL", "").trim();
//			System.out.println(IDPFullUrl);
//			System.out.println(SPFullUrl);
//			
//			String IDPUrl,IDPport,SPUrl,SPport;
//			
//			if (IDPFullUrl.length()>0){
//				IDPUrl=IDPFullUrl.substring(8,IDPFullUrl.lastIndexOf(":"));
//				IDPport=IDPFullUrl.substring(IDPFullUrl.lastIndexOf(":")+1,IDPFullUrl.indexOf("/sld/"));
//				System.out.println(IDPUrl);
//				System.out.println(IDPport);
//				System.out.println();
//			}
//			if (SPFullUrl.length()>0){
//				if(SPFullUrl.endsWith("/sp/")){
//					SPUrl=SPFullUrl.substring(8,SPFullUrl.lastIndexOf(":"));
//					SPport=SPFullUrl.substring(SPFullUrl.lastIndexOf(":")+1,SPFullUrl.indexOf("/sp/"));
//				}
//				else{
//					SPUrl=SPFullUrl.substring(8,SPFullUrl.lastIndexOf(":"));
//					SPport=SPFullUrl.substring(SPFullUrl.lastIndexOf(":")+1,SPFullUrl.lastIndexOf("/"));
//				}
//				System.out.println(SPUrl);
//				System.out.println(SPport);
//				System.out.println();
//			}
			
			logger.info("Finish to Debug.");
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
			this.parameters = Debug.getParameterTemplate();
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
			this.hostParameters = Debug.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Start Monitor.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
		parameters.put("endSchemaNumber", new TaskParameterEntry("10","End Schema Number"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> occhostList = new ArrayList<Host>();
		occhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp", "10.58.108.25", "root",
				"Initial0", "OCC Server 1"));
		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,
				"Host List for OCC Machine"));
		List<Host> jobhostList = new ArrayList<Host>();
		jobhostList.add(new Host("cnpvgvb1pf026.pvgl.sap.corp", "10.58.108.26", "root",
				"Initial0", "Job Server 1"));
		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
				"Host List for Job Machine"));
		List<Host> WindowshostList = new ArrayList<Host>();
		WindowshostList.add(new Host("cnpvgvb1pf031.pvgl.sap.corp", "10.58.108.31", "admin",
				"Initial0", "Windows Server"));
		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList,
				"Host List for WindowsMachine"));
		List<Host> centralServerList = new ArrayList<Host>();
		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
				"Initial0", "Central Windows Server"));
		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList,
				"Host List for Central Windows"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new Debug().getDescription();
	}
	
}
