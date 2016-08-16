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

public class PackagePrepare extends Task {
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute()  throws ParameterMissingException {
		this.status=Status.Running;
		String isLocal = parameters.getValue("isLocal").toString();
		String mainfolder = parameters.getValue("mainfolder").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");	
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");
		
		List<Host> allMachine=new ArrayList<Host>();
		allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		allMachine.addAll(eshopServerList);

		logger.info("Start to Package Prepare.");
		try {
			for (Host host:allMachine){
				logger.info("Current machine:"+host.getIP());
				String packagePrepareCmd="";
				if (isLocal.equals("true")){
					packagePrepareCmd="cd "+mainfolder+" && ./PackagePrepare.sh && ./AnywhereRestart.sh ";
				}else{
					packagePrepareCmd="cd "+mainfolder+" && ./PackagePrepare.sh no && ./AnywhereRestart.sh ";
				}
				RunRemoteSSH rrs=new RunRemoteSSH(host.getIP(),host.getUserName(),host.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(packagePrepareCmd);
			}
			logger.info("Finish to Package Prepare.");
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
		return "Task for Package Prepare.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		
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
				"Initial0", "Job Server"));
		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
				"Host List for Job Machine 1"));
		List<Host> eshophostList = new ArrayList<Host>();
		eshophostList.add(new Host("cnpvgvb1pf011.pvgl.sap.corp", "10.58.8.45", "root",
				"Initial0", "Eshop Server"));
		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,
				"Host List for Eshop Machine 1"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new PackagePrepare().getDescription();
	}
	
}
