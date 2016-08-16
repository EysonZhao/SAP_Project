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

public class OCCSizingSingleCase extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);
		
		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String dispatcherName = parameters.getValue("dispatcherName").toString();
		String jmeterScriptName = parameters.getValue("jmeterScriptName").toString();
		String windowsJava = parameters.getValue("windowsJava").toString();		
		String remoteHostList = parameters.getValue("remoteHostList").toString();		
		int startSchemaNumber = Integer.parseInt(parameters.getValue("startSchemaNumber").toString());
		int rumpup = Integer.parseInt(parameters.getValue("rumpup").toString());
		int thinkTime = Integer.parseInt(parameters.getValue("thinkTime").toString())*1000;
		int duration = Integer.parseInt(parameters.getValue("duration").toString());
		int userNumberPerTenant = Integer.parseInt(parameters.getValue("userNumberPerTenant").toString());
		int threadNumber= Integer.parseInt(parameters.getValue("threadNumber").toString());
		int threadNumberPerMachine=threadNumber/(remoteHostList.split(",").length);
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		//TODO get SP/IDP Parameter for Script.
		logger.info("Start to Run OCC Sizing Single Case.");
		String windowsFolderTool=windowsFolder+"\\Tool\\";
		try {
			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(), csmServer.getUserPassword());
			rrsCSM.setLogger(logger);
			String getSLDurlCMD = "mysql -uroot -p"+mariaDBPasswd+" -e \"select T0.URL FROM CSM.SERVICE_CLUSTER T0, CSM.BASE_CLUSTER T1 WHERE T0.BASE_CLUSTER_ID=T1.ID AND T1.\\`TYPE\\`='IDP'\"";
			String getSPurlCMD = "mysql -uroot -p"+mariaDBPasswd+" -e \"select T0.URL FROM CSM.SERVICE_CLUSTER T0, CSM.BASE_CLUSTER T1 WHERE T0.BASE_CLUSTER_ID=T1.ID AND T1.\\`TYPE\\`='SOLUTIONPORTAL'\"";
			
			String SLDFullUrl=rrsCSM.executeWithReturnString(getSLDurlCMD).replace("\r", "").replace("\n","").replace("URL", "").trim();
			String SPFullUrl=rrsCSM.executeWithReturnString(getSPurlCMD).replace("\r", "").replace("\n","").replace("URL", "").trim();
			System.out.println(SLDFullUrl);
			System.out.println(SPFullUrl);
			
			String SLDUrl="";
			String SLDport="";
			String SPUrl="";
			String SPport="";
			
			if (SLDFullUrl.length()>0){
				SLDUrl=SLDFullUrl.substring(8,SLDFullUrl.lastIndexOf(":"));
				SLDport=SLDFullUrl.substring(SLDFullUrl.lastIndexOf(":")+1,SLDFullUrl.indexOf("/sld/"));
			}
			
			if (SPFullUrl.length()>0){
				if(SPFullUrl.endsWith("/sp/")){
					SPUrl=SPFullUrl.substring(8,SPFullUrl.lastIndexOf(":"));
					SPport=SPFullUrl.substring(SPFullUrl.lastIndexOf(":")+1,SPFullUrl.indexOf("/sp/"));
				}
				else{
					SPUrl=SPFullUrl.substring(8,SPFullUrl.lastIndexOf(":"));
					SPport=SPFullUrl.substring(SPFullUrl.lastIndexOf(":")+1,SPFullUrl.lastIndexOf("/"));
				}
			}
			
			
			String RunCaseCmd = "cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
					+ windowsJava
					+ "\" && cd "
					+ windowsFolderTool
					+ "\\JmeterScript\\TestCase && call ..\\..\\Jmeter\\bin\\jmeter -n -t "
					+ jmeterScriptName
					+ " -Gprotocol=https -Gdispatcher="
					+ dispatcherName
					+ " -Gdispatcher_port=0 -Gsld_server="
					+ SLDUrl
					+ " -Gsld_port="
					+ SLDport
					+ " -Gsp_server="
					+ SPUrl
					+ " -Gsp_port="
					+ SPport
					+ " -Grampup="
					+ rumpup
					+ " -Guser_per_company="
					+ userNumberPerTenant
					+ " -Gstart_tenant_sn="
					+ startSchemaNumber
					+ " -Gthread_number="
					+ threadNumberPerMachine
					+ " -Gstart_user_sn=1 -Ghostlist="
					+ remoteHostList
					+ " -Gduration="
					+ duration
					+ " -Gthinktime="
					+ thinkTime
					+ " -R " + remoteHostList;
			
			RunRemoteSSH rrs = new RunRemoteSSH(windowsServer.getIP(),
					windowsServer.getUserName(),
					windowsServer.getUserPassword());
			rrs.setLogger(logger);
			rrs.execute(RunCaseCmd, false, 0, duration * 1000 * 3);
			
			logger.info("Finish to Run OCC Sizing Single Case.");
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
		if (null == parameters) {
			parameters = OCCSizingSingleCase.getParameterTemplate();
		}
		return parameters;
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
		if (null == hostParameters) {
			hostParameters = OCCSizingSingleCase.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Run Single Case.";
	}	
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\","Windows Main Folder"));
		
		parameters.put("dispatcherName", new TaskParameterEntry("occ.pvgl.sap.corp","Dispatcher Name of OCC"));
		parameters.put("jmeterScriptName", new TaskParameterEntry("Jmeter.jmx","Jmeter Script Name"));
		parameters.put("remoteHostList", new TaskParameterEntry("192.168.0.1,192.168.0.2","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema # Run Test cases"));
		parameters.put("rumpup", new TaskParameterEntry("0","Rumpup Time(s)"));
		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
		parameters.put("userNumberPerTenant", new TaskParameterEntry("4","Thread # per Tenant"));
		parameters.put("threadNumber", new TaskParameterEntry("20","Total Thread #(all machine)"));
		parameters.put("duration", new TaskParameterEntry("4000","Test Duration(s)"));

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
		return new OCCSizingSingleCase().getDescription();
	}
	
}
