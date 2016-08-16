package sme.perf.task.impl.anywhere.p1604;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import sme.perf.entity.Host;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.task.impl.common.HanaInstanceRecovery;
import sme.perf.utility.JsonHelper;

public class OCCSizingTest extends Task {

	private List<ExecutionTaskInfo> mainExecutionTaskInfoList= new ArrayList<ExecutionTaskInfo>(); 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		String threadNumberListStr = parameters.getValue("threadNumberList").toString();
		try {
			logger.info("Start to Run OCC Sizing Test.");
			JsonHelper jsonHelper = new JsonHelper();
			List<List<String>> threadNumberList = (List<List<String>>) jsonHelper.deserialObject(threadNumberListStr, List.class);
			Result r=Result.Pass;
			Boolean isPass=true;
			for (int i = threadNumberList.size(); i >= 1; i--) {
				//FOR SINGLE ExecutionTaskInfo SUBLIST
				List<ExecutionTaskInfo> subExcutionTaskInfoList=new ArrayList<ExecutionTaskInfo>();
				ExecutionTaskInfo maintaskInfo=new ExecutionTaskInfo();
				maintaskInfo.setStartTime(DateTime.now());
				maintaskInfo.setClassName(this.getClass().getName());
				maintaskInfo.setPackageName(this.getClass().getPackage().getName());
				for (String threadNumber : threadNumberList.get(i-1)) {
					if (parameters.getValue("threadNumber",true)==null){
						parameters.getParameters().put("threadNumber", new TaskParameterEntry(threadNumber,"Total Thread #"));
					}else{
						parameters.getParameters().get("threadNumber").setValue(threadNumber);
					}
					List<Task> taskList=new ArrayList<Task>();
					taskList.add(new HanaInstanceRecovery());
					taskList.add(new KillMonitor());
					taskList.add(new RemoveMonitorFile());
					taskList.add(new RestartService());
					taskList.add(new StartMonitor());
					taskList.add(new OCCSizingSingleCase());
					taskList.add(new KillMonitor());
					taskList.add(new LogCollection());
					for(Task t:taskList){
						if(r==Result.Pass){
							t.setHosts(hostParameters);
							t.setParameters(parameters);
							t.setLogger(logger);
							ExecutionTaskInfo taskInfo=new ExecutionTaskInfo();	
							taskInfo.setSn(taskList.indexOf(t)+(threadNumberList.get(i-1).indexOf(threadNumber))*taskList.size());
							taskInfo.setClassName(t.getClass().getName());
							taskInfo.setPackageName(t.getClass().getPackage().getName());
							taskInfo.setTaskParameterJson(new JsonHelper().serializeObject(parameters));
							taskInfo.setTaskHostParameterJson(new JsonHelper().serializeObject(hostParameters));
							taskInfo.setStartTime(DateTime.now());
							r=t.execute();
							taskInfo.setResult(r);
							taskInfo.setEndTime(DateTime.now());
							subExcutionTaskInfoList.add(taskInfo);
						} else {
							logger.error(String
									.format("Run OCC Sizing Test will not be finished because step %s is failed.",
											t.getClass().getName()));
							isPass = false;
							break;
						}
					}
					if (!isPass){
						break;
					}
					
				}
				if (isPass){
					Task machineTask = new MachineShutdown();
					machineTask.setHosts(hostParameters);
					machineTask.setParameters(parameters);
					machineTask.setLogger(logger);
					String fullClassName = machineTask.getClass().toString();
					ExecutionTaskInfo taskInfo = new ExecutionTaskInfo();
					taskInfo.setSn(subExcutionTaskInfoList.size());
					taskInfo.setClassName(fullClassName.substring(fullClassName
							.lastIndexOf(".") + 1));
					taskInfo.setPackageName(fullClassName.substring(6,
							fullClassName.lastIndexOf(".")));
					taskInfo.setTaskParameterJson(new JsonHelper()
							.serializeObject(parameters));
					taskInfo.setTaskHostParameterJson(new JsonHelper()
							.serializeObject(hostParameters));
					taskInfo.setStartTime(DateTime.now());
					r = machineTask.execute();
					taskInfo.setResult(r);
					taskInfo.setEndTime(DateTime.now());
					subExcutionTaskInfoList.add(taskInfo);
					// FINISH SINGLE ExecutionTaskInfo SUBLIST
				}
				// ADD TO Main ExecutionTaskInfo List
				maintaskInfo.setSubExecutionTaskInfoList(subExcutionTaskInfoList);
				maintaskInfo.setSn(threadNumberList.size() - i);
				maintaskInfo.setEndTime(DateTime.now());
				maintaskInfo.setResult(r);
				mainExecutionTaskInfoList.add(maintaskInfo);
				if (!isPass){
					break;
				}
			}
			logger.info("Finish to Run OCC Sizing Test.");
			if (isPass){
				this.status=Status.Finished;
				return Result.Pass;
			}else{
				this.status=Status.Failed;
				return Result.Pass;
			}
		} catch (Exception e) {
			logger.error("Excpetion:" +e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}
	}

	@Override
	public String getDescription() {
		return "Task for Run OCC Sizing Test.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common??
		//parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
		//parameters.put("schemaBackupFolder", new TaskParameterEntry("/root/perftest/hanatmpbackup/PERFDB1","Linux Main Folder"));
		parameters.put("mainResultFolder", new TaskParameterEntry("C:\\WindowsMainFolder\\ResultCollection","Main Result Folder"));
		parameters.put("threadNumberList", new TaskParameterEntry("[[\"1\",\"16\",\"32\",\"48\",\"60\"],[\"60\",\"120\"],[\"120\",\"156\"]]","Run Cases Thread Number"));
		parameters.put("hanaUser", new TaskParameterEntry("cdbadm","HANA administrator username"));
		parameters.put("dispatcherName", new TaskParameterEntry("occ.pvgl.sap.corp","Dispatcher Name of OCC"));
		parameters.put("jmeterScriptName", new TaskParameterEntry("Jmeter.jmx","Jmeter Script Name"));
		parameters.put("remoteHostList", new TaskParameterEntry("192.168.0.1,192.168.0.2","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema # Run Test cases"));
		parameters.put("rumpup", new TaskParameterEntry("0","Rumpup Time(s)"));
		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
		parameters.put("userNumberPerTenant", new TaskParameterEntry("4","Thread # per Tenant"));
		parameters.put("duration", new TaskParameterEntry("4000","Test Duration(s)"));
		parameters.put("scenairo", new TaskParameterEntry("OCCSizing","Senario Name"));
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115","Full Hana Instance URL"));
		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
		
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
		
		List<Host> windowsHostList = new ArrayList<Host>();
		windowsHostList.add(new Host("cnpvgvb1pf031.pvgl.sap.corp", "10.58.108.31", "admin",
				"Initial0", "windows Server"));
		hostsParameter.put("windowsServer", new TaskParameterEntry(windowsHostList,
				"Windows Server"));
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
//		List<Host> etcdhostList = new ArrayList<Host>();
//		etcdhostList.add(new Host("cnpvgvb1pf024.pvgl.sap.corp", "10.58.108.24", "root",
//				"Initial0", "ETCD Server"));
//		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,
//				"Host List for ETCD Machine "));
//		List<Host> sharefolderhostList = new ArrayList<Host>();
//		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp", "10.58.108.25", "root",
//				"Initial0", "Share Folder Server"));
//		hostsParameter.put("shareFolderServer", new TaskParameterEntry(sharefolderhostList,
//				"Host List for Share Folder Machine"));
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
		List<Host> centralServerList = new ArrayList<Host>();
		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.46", "administrator",
				"Initial0", "Central Windows Server"));
		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList,
				"Host List for Central Windows"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	
	public static String getDescriptionTemplate() {
		return new OCCSizingTest().getDescription();
	}
	
	@Override
	public List<ExecutionTaskInfo> getSubExecutionTaskInfoList(){
		return mainExecutionTaskInfoList;
	}
}
