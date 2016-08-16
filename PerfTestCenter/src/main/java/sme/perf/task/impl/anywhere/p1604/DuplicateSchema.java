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
import sme.perf.utility.RunRemoteSSH;

public class DuplicateSchema extends Task {
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String hanaInstanceFull = parameters.getValue("hanaInstanceFull").toString();
		int duplicateThreadNumber = Integer.parseInt(parameters.getValue("duplicateThreadNumber").toString());
		int schemaNumberPerDuplicateThread = Integer.parseInt(parameters.getValue("schemaNumberPerDuplicateThread").toString());
		int startSchemaNumber = Integer.parseInt(parameters.getValue("startSchemaNumber").toString());
		String sourceSchemaName = parameters.getValue("sourceSchemaName").toString();
		String schemaBackupFolder = parameters.getValue("schemaBackupFolder").toString();
		
		List<Host> centralServerList = (List<Host>) hostParameters.getValue("centralServer");
		Host centralServer=centralServerList.get(0);

		logger.info("Start to Duplicate Schema.");
		try {
			String windowsFolderTool=windowsFolder+"\\Tool\\";
			String duplicateShellCmd = "cmd /c cd " + windowsFolderTool
					+ " && .\\DuplicateSchema\\DuplicateSchema.exe HDBODBC "
					+ hanaInstanceFull + " SYSTEM manager " + sourceSchemaName
					+ " PERFDB " + schemaBackupFolder + " " + duplicateThreadNumber
					+ " " + schemaNumberPerDuplicateThread + " "
					+ startSchemaNumber + " ";
			RunRemoteSSH rrsCentral=new RunRemoteSSH(centralServer.getIP(),
					centralServer.getUserName(),
					centralServer.getUserPassword());
			rrsCentral.setLogger(logger);
			rrsCentral.execute(duplicateShellCmd);
			logger.info("Finish to Duplicate Schema.");
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
		return "Task for Duplicate Schema.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common??
		//parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
		//parameters.put("schemaBackupFolder", new TaskParameterEntry("/root/perftest/hanatmpbackup/PERFDB1","Linux Main Folder"));
		
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115","Full Hana Instance URL"));
		parameters.put("duplicateThreadNumber", new TaskParameterEntry("5","Parrally Duplicate DBTemplate Thread Number"));
		parameters.put("schemaNumberPerDuplicateThread", new TaskParameterEntry("2","Schema Number Per Duplicate thread"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","StartSchemaNumber"));

		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> WindowshostList = new ArrayList<Host>();
		WindowshostList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
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
		return new DuplicateSchema().getDescription();
	}
}
