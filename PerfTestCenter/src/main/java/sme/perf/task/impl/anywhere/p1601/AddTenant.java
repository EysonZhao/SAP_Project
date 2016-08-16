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

public class AddTenant extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException{
		this.status=Status.Running;
		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		int startSchemaNumber = Integer.parseInt(parameters.getValue("startSchemaNumber").toString());
		int endSchemaNumber = Integer.parseInt(parameters.getValue("endSchemaNumber").toString());
		int tenantNumber = endSchemaNumber-startSchemaNumber+1;
		String windowsJava = parameters.getValue("windowsJava").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);
		
		String windowsFolderTool=windowsFolder+"\\Tool\\";
		
		logger.info("Start to Add Tenant.");
		try {
			//ADD TANANT ONE BY ONE(OLD VERSION)
			/*
			for (int i = startSchemaNumber; i <= endSchemaNumber; i++) {
				String addTenantCmd = "cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
						+ windowsJava
						+ "\" && cd "
						+ windowsFolderTool
						+ "\\JmeterScript\\Common && del /s /q log_addtenant.jtl && call ..\\..\\Jmeter\\bin\\jmeter -n -t addTenant.jmx -l log_addtenant.jtl -Jprotocol=https -Jsp_port=29900 -Jsp_server="
						+ csmServer.getHostName()
						+ " -Jdispatcher="
						+ csmServer.getHostName()
						+ " -Jdispatcher_port=443 -Jsld_server="
						+ csmServer.getHostName()
						+ " -Jsld_port=8443 -JV_DB_HOST=1 -JV_CompanyDB=CSM -Jcompany=test"
						+ i
						+ " -JfirstName=1 -JlastName=test"
						+ i
						+ " -Jemail=test" + i + "_1@sap.com";
				RunRemoteSSH rrs=new RunRemoteSSH(windowsServer.getIP(), windowsServer.getUserName(), windowsServer.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(addTenantCmd);		
			}
			*/
			
			//ADD TANANT BY ONE SSH-CONNECTION (NEW VERSION)
			/**/
			String addTenantCmd = "cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
					+ windowsJava
					+ "\" && cd "
					+ windowsFolderTool
					+ "\\JmeterScript\\Common && del /s /q log_addtenant.jtl && call ..\\..\\Jmeter\\bin\\jmeter -n -t "
					+ "addTenant.jmx -l log_addtenant.jtl -Jprotocol=https -Jsld_server="
					+ csmServer.getHostName()
					+ " -Jcompany=test -Jstart_tenant_sn="
					+ startSchemaNumber
					+ " -Jstart_user_sn=1 -Jtenant_number="
					+ tenantNumber;
			RunRemoteSSH rrs=new RunRemoteSSH(windowsServer.getIP(), windowsServer.getUserName(), windowsServer.getUserPassword());
			rrs.setLogger(logger);
			rrs.execute(addTenantCmd,false,0,tenantNumber*60000);
			
			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(),csmServer.getUserPassword());
			rrsCSM.setLogger(logger);
			String updateTenantsCmd="mysql -uroot -p"+mariaDBPasswd+" -e \"UPDATE CSM.TENANTS SET PURPOSE='PRODUCT'\"\n"
					+ "mysql -uroot -p"+mariaDBPasswd+" -e \"UPDATE CSM.GLOBALSETTING SET VALUE=9999 WHERE \\`KEY\\` = 'MaxUsersPerTrialCustomer'\"";
			rrsCSM.execute(updateTenantsCmd);
			
			logger.info("Finish to Add Tenant.");
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
			this.parameters = AddTenant.getParameterTemplate();
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
			this.hostParameters = AddTenant.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Add Tenant.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\Program Files\\","Windows Main Folder"));
		//parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_45\\bin\\java.exe","Java exe Location"));	
		//parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.8.46:3306","Full CSM MariaDB URL"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
		parameters.put("endSchemaNumber", new TaskParameterEntry("10","End Schema Number"));
		//parameters.put("dispatcherName", new TaskParameterEntry("occ.pvgl.sap.corp","Dispatcher Name for OCC"));
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
		return new AddTenant().getDescription();
	}

}
