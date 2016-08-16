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

public class AddUser extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException{
		this.status=Status.Running;		
		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String csmMariaDBFull = parameters.getValue("csmMariaDBFull").toString();
		int startSchemaNumber = Integer.parseInt(parameters.getValue("startSchemaNumber").toString());
		int endSchemaNumber = Integer.parseInt(parameters.getValue("endSchemaNumber").toString());
		int startUserNumber=Integer.parseInt(parameters.getValue("startUserNumber").toString());
		int tenantNumber = endSchemaNumber-startSchemaNumber+1;
		int maxUserNumberPerTenant=Integer.parseInt(parameters.getValue("maxUserNumberPerTenant").toString());
		int addUserThreadNumber = Integer.parseInt(parameters.getValue("addUserThreadNumber").toString());
		int userPerThread=tenantNumber*(maxUserNumberPerTenant - startUserNumber + 1)/addUserThreadNumber;	
		
			
		String windowsJava = parameters.getValue("windowsJava").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);	
			
		String windowsFolderTool=windowsFolder+"\\Tool\\";
		logger.info("Start to Add User.");
		try {
			//ADD USER ONE BY ONE(OLD VERSION)
			/*
			for (int i = startSchemaNumber; i <= endSchemaNumber; i++) {
				for (int j = 2; j <= maxUserNumberPerTenant; j++) {
					String addUserCmd = "cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
							+ windowsJava
							+ "\" && cd "
							+ windowsFolderTool
							+ "\\JmeterScript\\Common && del /s /q log_addusers.jtl && call ..\\..\\Jmeter\\bin\\jmeter -n -t addUsers.jmx -l log_addusers.jtl -Jprotocol=https -Jsp_port=29900 -Jsp_server="
							+ csmServer.getHostName()
							+ " -Jdispatcher="
							+ csmServer.getHostName()
							+ " -Jdispatcher_port=443 -Jsld_server="
							+ csmServer.getHostName()
							+ " -Jsld_port=8443 -JV_DB_HOST="
							+ csmMariaDBFull
							+ " -JV_CompanyDB=CSM  -Jemail=test"
							+ i
							+ "_1@sap.com -JfirstName="
							+ j
							+ " -JlastName=test"
							+ i + " -Jnewemail=test" + i + "_" + j + "@sap.com";
					RunRemoteSSH rrs = new RunRemoteSSH(windowsServer.getIP(),
							windowsServer.getUserName(),
							windowsServer.getUserPassword());
					rrs.setLogger(logger);
					rrs.execute(addUserCmd);
				}
			}
			*/
			
			//ADD USER BY ONE SSH-CONNECTION & CONCURRENT (NEW VERSION)		
			String addUserCmd = "cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
					+ windowsJava
					+ "\" && cd "
					+ windowsFolderTool
					+ "\\JmeterScript\\Common && del /s /q log_addusers.jtl && call ..\\..\\Jmeter\\bin\\jmeter -n -t "
					+ "addUsers.jmx -l log_addusers.jtl -Jprotocol=https "
					+ "-Jsld_server="
					+ csmServer.getHostName()
					+ " -JV_DB_HOST="
					+ csmMariaDBFull
					+ " -JV_CompanyDB=CSM -Juser_per_company="
					+ (maxUserNumberPerTenant - startUserNumber + 1)
					+ " -Jstart_tenant_sn="
					+ startSchemaNumber
					+ " -Jthread_number="
					+ addUserThreadNumber
					+ " -Jstart_user_sn="
					+ startUserNumber
					+ " -Jlogin_user_index=1 -Juser_per_thread="
					+ userPerThread
					+ " -Jramp_up="
					+ (addUserThreadNumber * 10);
			
			RunRemoteSSH rrs = new RunRemoteSSH(windowsServer.getIP(),
					windowsServer.getUserName(),
					windowsServer.getUserPassword());
			rrs.setLogger(logger);
			rrs.execute(addUserCmd,false,0,userPerThread*addUserThreadNumber*20000);
			
			logger.info("Finish to Add User.");
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
			this.parameters = AddUser.getParameterTemplate();
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
			this.hostParameters = AddUser.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Add User.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\Program Files\\","Windows Main Folder"));
		//parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_45\\bin\\java.exe","Java exe Location"));
		
		parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.8.46:3306","Full CSM MariaDB URL"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
		parameters.put("endSchemaNumber", new TaskParameterEntry("10","End Schema Number"));
		parameters.put("maxUserNumberPerTenant", new TaskParameterEntry("10","Max User Number per Tenant"));
		parameters.put("addUserThreadNumber",new TaskParameterEntry("10", "Add User parralled Thread Number"));
		parameters.put("startUserNumber", new TaskParameterEntry("2","Start User Number"));

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
		List<Host> WindowshostList = new ArrayList<Host>();
		WindowshostList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
				"Initial0", "Windows Server"));
		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList,
				"Host List for WindowsMachine"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new AddUser().getDescription();
	}
}
