package sme.perf.task.impl.mock.Anywhere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sme.perf.entity.Host;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.ITask;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.LogHelper;
import sme.perf.utility.RunRemoteSSH;

public class AddTenant extends Task  {

	private TaskParameterMap parameters;
	private TaskParameterMap hostList;
	private Status status=Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() {

		return Result.Pass;
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
		this.hostList = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		if (null == this.hostList) {
			this.hostList = AddTenant.getHostsTemplate();
		}
		return hostList;
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
		
		parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.8.46:3306","Full CSM MariaDB URL"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
		parameters.put("endSchemaNumber", new TaskParameterEntry("10","End Schema Number"));
		parameters.put("dispatcherName", new TaskParameterEntry("occ.pvgl.sap.corp","Dispatcher Name for OCC"));
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
	
	public static String getDescriptionTemplate(){
		return "Add Tenant";
	}

}
