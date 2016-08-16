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

public class UploadCertification extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String scenario = parameters.getValue("scenario").toString();
		String windowsJava = parameters.getValue("windowsJava").toString();
		String domainName=parameters.getValue("domainName").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);	
		
		String windowsFolderTool=windowsFolder+"\\Tool\\";
		String certtype=null;
		switch (scenario) {
		case "OCCSizing":
			certtype = "OCC";
			break;
		case "EshopSizing":
			certtype = "NESHOP";
			break;
		default:
			certtype = null;
			break;
		}
		logger.info("Start to Upload Certification.");
		try {
			if(certtype != null){
				String uploadCertificationCmd = "cmd /c set JVM_ARGS=-Xms512m -Xmx2048m && set JM_LAUNCH=\""
						+ windowsJava
						+ "\" && cd "
						+ windowsFolderTool
						+ "\\JmeterScript\\Common && del /s /q log_uploadCertification.jtl && "
						+ "call ..\\..\\Jmeter\\bin\\jmeter -n -t UploadCertification.jmx -l log_uploadCertification.jtl -Jhost="
						+ csmServer.getHostName()
						+ " -Jcsmurl=https://"
						+ csmServer.getHostName()
						+ ":8444/sld/ -Jdomainname="
						+ domainName + " -Jtype="
						+ certtype;
				RunRemoteSSH rrs=new RunRemoteSSH(windowsServer.getIP(),windowsServer.getUserName(),windowsServer.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(uploadCertificationCmd);
				logger.info("Finish to Upload Certification.");
			}else{
				logger.info("No need to Upload Certification.");
			}
			// logger.info("Finish to Upload Certification.");
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
			this.parameters = UploadCertification.getParameterTemplate();
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
			this.hostParameters = UploadCertification.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Upload Certification and set dispatcher.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\Program Files\\","Windows Main Folder"));
		//parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_45\\bin\\java.exe","Java exe Location"));
		parameters.put("domainName", new TaskParameterEntry("pvgl.sap.corp","Domain Name"));
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
		return new UploadCertification().getDescription();
	}
	
}
