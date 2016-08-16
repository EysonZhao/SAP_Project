package sme.perf.task.impl.anywhere.p1603;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.entity.Host;
import sme.perf.result.entity.ResultDescription;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.RunRemoteSSH;

public class LogCollection extends Task {
	
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;

		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String mainfolder = parameters.getValue("mainfolder").toString();
		String mainResultFolder=parameters.getValue("mainResultFolder").toString();
		String scenario = parameters.getValue("scenario").toString();
		String isLocal = parameters.getValue("isLocal").toString();
		int threadNumber= Integer.parseInt(parameters.getValue("threadNumber").toString());
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy_MM_dd_HH_mm_ss");

		List<Host> centralServerList = (List<Host>) hostParameters.getValue("centralServer");
		Host centralServer=centralServerList.get(0);
		
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");	
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");
		List<Host> mariadbServerList = (List<Host>) hostParameters.getValue("mariadbServer",true);
		List<Host> shareFolderServerList = (List<Host>) hostParameters.getValue("shareFolderServer");
		List<Host> etcdServerList = (List<Host>)hostParameters.getValue("etcdServer");
		List<Host> hanaServerList = (List<Host>) hostParameters.getValue("hanaServer");
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);
		
		List<Host> allMachine=new ArrayList<Host>();
		allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		allMachine.addAll(eshopServerList);
		allMachine.addAll(etcdServerList);
		if (isLocal.equals("false")){
			allMachine.addAll(shareFolderServerList);
		}
		allMachine.addAll(hanaServerList);
		if (mariadbServerList!=null){
			allMachine.addAll(mariadbServerList);
		}
		

		String resultFolder="";
		int machineCount=0;
		switch (scenario) {
		case "OCCSizing":
			machineCount=occServerList.size();
			resultFolder=mainResultFolder+"\\"+threadNumber+"Thread_"+machineCount+"OCC_"+new DateTime().now().toString(dateTimeFormatter);
			break;
		case "EshopSizing":
			machineCount=occServerList.size();
			resultFolder=mainResultFolder+"\\"+threadNumber+"Thread_"+machineCount+"Eshop_"+new DateTime().now().toString(dateTimeFormatter);
			break;
		default:
			resultFolder=mainResultFolder+"\\"+threadNumber+"Thread_"+new DateTime().now().toString(dateTimeFormatter);
			break;
		}		
		
		String remoteResultFolder= resultFolder.replace("C:", "\\\\"+centralServer.getIP());
		
		logger.info("Start to Collect Log Files.");
		String windowsFolderTool=windowsFolder+"\\Tool\\";
		try {
			RunRemoteSSH rrsCentral=new RunRemoteSSH(centralServer.getIP(),
					centralServer.getUserName(),
					centralServer.getUserPassword());
			rrsCentral.setLogger(logger);
			RunRemoteSSH rrsWindows = new RunRemoteSSH(windowsServer.getIP(),
					windowsServer.getUserName(),
					windowsServer.getUserPassword());
			rrsWindows.setLogger(logger);

			String resultName=scenario+"_"+threadNumber+"Ths";
			ResultDescription rd=new ResultDescription();
			rd.setThreadNumber(threadNumber);
			rd.setResultName(resultName);
			String jsonString = new JsonHelper().serializeObject(rd);
			
			String createResultFolderCmd = "cmd /c mkdir " + resultFolder
					+ "\\io && mkdir " + resultFolder + "\\top && mkdir "
					+ resultFolder + "\\machine && mkdir " + resultFolder
					+ "\\dockerstats && mkdir " + resultFolder + "\\jmeter";
			rrsCentral.execute(createResultFolderCmd);
			String copyJmeterCmd = "cmd /c xcopy C:\\B1pj\\ShareService\\OCC\\JmeterScript\\*.csv  "
					+ remoteResultFolder + "\\jmeter /s/d"
					// add jsonFile.txt
					+" && echo '"+jsonString+"' > "+remoteResultFolder+"\\json.txt";
			rrsWindows.execute(copyJmeterCmd,false,2,240000);
			
			for (Host host : allMachine) {
				//Fix -unsafe -scp for SUSE 12?? need To TEST
				logger.info("Current machine:" + host.getIP());
				String copyIOCmd = "cmd /c cd " + windowsFolderTool
						+ " && pscp -scp -unsafe -r -pw " + host.getUserPassword() + " " +  host.getUserName() + "@"
						+ host.getIP() + ":" + mainfolder + "/*_IO_*.txt "
						+ resultFolder + "\\io";
				rrsCentral.execute(copyIOCmd,false,2,120000);
				String copyOSCmd = "cmd /c cd " + windowsFolderTool
						+ " && pscp -scp -unsafe -r -pw " + host.getUserPassword() + " " + host.getUserName()  + "@"
						+ host.getIP() + ":" + mainfolder + "/*SysSummary* "
						+ resultFolder + "\\machine";
				rrsCentral.execute(copyOSCmd,false,2,120000);
				String copyTOPCmd = "cmd /c cd " + windowsFolderTool
						+ " && pscp -scp -unsafe -r -pw " + host.getUserPassword() + " " + host.getUserName()  + "@"
						+ host.getIP() + ":" + mainfolder + "/*_TOP_*.txt "
						+ resultFolder + "\\dockerstats";
				if ((mariadbServerList != null && mariadbServerList.contains(host))
						|| hanaServerList.contains(host)
						|| shareFolderServerList.contains(host)
						|| etcdServerList.contains(host)) {
					copyTOPCmd = "cmd /c cd " + windowsFolderTool
							+ " && pscp -scp -unsafe -r -pw " + host.getUserPassword() + " "
							+ host.getUserName() + "@" + host.getIP() + ":"
							+ mainfolder + "/*_TOP_*.txt " + resultFolder
							+ "\\top";
				}
				rrsCentral.execute(copyTOPCmd, false, 2, 120000);
				if (csmServerList.contains(host)
						|| occServerList.contains(host)
						|| jobServerList.contains(host)
						|| eshopServerList.contains(host)) {
					String copyMachineTopCmd = "cmd /c cd " + windowsFolderTool
							+ " && pscp -scp -unsafe -r -pw " + host.getUserPassword() + " "
							+ host.getUserName() + "@" + host.getIP() + ":"
							+ mainfolder + "/*_MACHINETOP_*.txt "
							+ resultFolder + "\\top";
					rrsCentral.execute(copyMachineTopCmd, false, 2, 120000);
				}
			}
			logger.info("Finish to Collect Log Files.");
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
		return "Task for Collect Log Files.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		parameters.put("threadNumber", new TaskParameterEntry("20","Total Thread #"));
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
				"Initial0", "Job Server 1"));
		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
				"Host List for Job Machine"));
		List<Host> eshophostList = new ArrayList<Host>();
		eshophostList.add(new Host("cnpvgvb1pf011.pvgl.sap.corp", "10.58.8.45", "root",
				"Initial0", "Eshop Server"));
		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,
				"Host List for Eshop Machine"));
		List<Host> WindowshostList = new ArrayList<Host>();
		WindowshostList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
				"Initial0", "Windows Server"));
		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList,
				"Host List for WindowsMachine"));
		List<Host> etcdhostList = new ArrayList<Host>();
		etcdhostList.add(new Host("cnpvgvb1pf024.pvgl.sap.corp", "10.58.108.24", "root",
				"Initial0", "ETCD Server"));
		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,
				"Host List for ETCD Machine "));
		List<Host> sharefolderhostList = new ArrayList<Host>();
		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp", "10.58.108.25", "root",
				"Initial0", "Share Folder Server"));
		hostsParameter.put("shareFolderServer", new TaskParameterEntry(sharefolderhostList,
				"Host List for Share Folder Machine"));
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
		return new LogCollection().getDescription();
	}
	
}
