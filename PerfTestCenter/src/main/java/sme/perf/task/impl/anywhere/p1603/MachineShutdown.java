package sme.perf.task.impl.anywhere.p1603;

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

public class MachineShutdown extends Task {
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		
		String scenario = parameters.getValue("scenario").toString();
		logger.info("Start to Shutdown Useless Machine.");
		try {
			int machineNumber = 0;
			Host shutdownMachine = null;
			switch (scenario) {
			case "OCCSizing":
				List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
				machineNumber = occServerList.size();
				if (machineNumber > 1) {
					shutdownMachine = occServerList.get(machineNumber - 1);
					occServerList.remove(machineNumber - 1);
					logger.info("Machine " + shutdownMachine.getIP()
							+ " has been removed from occ server list");
				}else{
					logger.info("No machine needs to be removed.");
				}
				break;
			case "EshopSizing":
				List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");
				machineNumber = eshopServerList.size();
				if (machineNumber > 1) {
					shutdownMachine = eshopServerList.get(machineNumber - 1);
					eshopServerList.remove(machineNumber - 1);
					logger.info("Machine " + shutdownMachine.getIP()
							+ " has been removed from eshop server list");
				}else{
					logger.info("No machine needs to be removed.");
				}
				break;
			default:
				logger.info("No machine needs to be removed.");
				break;
			}

			if (shutdownMachine!=null) {
				//String shutdownCmd = "docker stop $(docker ps -a -q)";
				String shutdownCmd = "halt";
				RunRemoteSSH rrs=new RunRemoteSSH(shutdownMachine.getIP(), shutdownMachine.getUserName(), shutdownMachine.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(shutdownCmd);
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info("Machine " + shutdownMachine + " has been shutdown.");
			}
			
			logger.info("Finish to Shutdown Useless Machine.");
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
		return "Task for Start Monitor.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\","Windows Main Folder"));
		//parameters.put("scenario", new TaskParameterEntry("OCCSizing","scenario Name"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new MachineShutdown().getDescription();
	}
}
