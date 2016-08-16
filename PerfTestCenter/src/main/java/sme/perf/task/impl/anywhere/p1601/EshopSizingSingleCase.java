package sme.perf.task.impl.anywhere.p1601;

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

public class EshopSizingSingleCase extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	
	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException{
		this.status=Status.Running;
//		List<Host> windowsServerList = (List<Host>) hostList.getParameters().get("windowsServer").getValue();
//		Host windowsServer=windowsServerList.get(0);
//		
//		String windowsFolder = parameters.getParameters().get("windowsFolder").getValue().toString();
//		String eshopDispatcherName = parameters.getParameters().get("eshopDispatcherName").getValue().toString();
//		String jmeterScriptName = parameters.getParameters().get("jmeterScriptName").getValue().toString();
//		String windowsJava = parameters.getParameters().get("windowsJava").getValue().toString();		
//		int rumpup = Integer.parseInt(parameters.getParameters().get("rumpup").getValue().toString());
//		int duration = Integer.parseInt(parameters.getParameters().get("duration").getValue().toString());
//		int threadNumber= Integer.parseInt(parameters.getParameters().get("threadNumber").getValue().toString());
//		
//		String windowsFolderTool=windowsFolder+"\\Tool\\";
//		logger.info("Start to Run Single Case.");
		try {
//			String RunCaseCmd ="cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
//					+ windowsJava
//					+ "\" && cd "
//					+ windowsFolderTool
//					+ "\\JmeterScript\\TestCase && call ..\\..\\Jmeter\\bin\\jmeter -n -t "
//					+ jmeterScriptName
//					+ " -Gprotocol=https -Gurl="
//					+ eshopDispatcherName
//					+ "-Grampup="
//					+ rumpup
//					+ " -Gthread_number="
//					+ threadNumber
//					+ " -Gduration="
//					+ duration;
//			RunRemoteSSH rrs=new RunRemoteSSH(windowsServer.getIP(), windowsServer.getUserName(), windowsServer.getUserPassword());
//			rrs.setLogger(logger);
//			rrs.execute(RunCaseCmd);
			
			logger.info("Finish to Run Single Case.");
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
			parameters = EshopSizingSingleCase.getParameterTemplate();
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
			hostParameters = EshopSizingSingleCase.getHostsTemplate();
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
		
//		parameters.put("eshopDispatcherName", new TaskParameterEntry("eshop.pvgl.sap.corp","Dispatcher Name of Eshop"));
//		parameters.put("jmeterScriptName", new TaskParameterEntry("Jmeter.jmx","Jmeter Script Name"));
//		parameters.put("rumpup", new TaskParameterEntry("0","Rumpup Time(s)"));
//		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
//		parameters.put("threadNumber", new TaskParameterEntry("20","Total Thread #"));
//		parameters.put("duration", new TaskParameterEntry("4000","Test Duration(s)"));
		
		TaskParameterMap template=new TaskParameterMap();
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
		return new EshopSizingSingleCase().getDescription();
	}
	
}
