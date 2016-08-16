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

public class ShellScriptCopy extends Task {

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;

		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String mainfolder = parameters.getValue("mainfolder").toString();
		String isLocal = parameters.getValue("isLocal").toString();
		List<Host> centralServerList = (List<Host>) hostParameters.getValue("centralServer");
		Host centralServer=centralServerList.get(0);
		String remoteCenterServerMainPath= windowsFolder.replace("C:", "\\\\"+centralServer.getIP());
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");	
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");
		List<Host> mariadbServerList = (List<Host>) hostParameters.getValue("mariadbServer",true);
		List<Host> shareFolderServerList = (List<Host>) hostParameters.getValue("shareFolderServer");
		List<Host> etcdServerList = (List<Host>) hostParameters.getValue("etcdServer");
		
		List<Host> windowsServerList = (List<Host>) hostParameters.getValue("windowsServer");
		Host windowsServer=windowsServerList.get(0);
		List<Host> hanaServerList = (List<Host>) hostParameters.getValue("hanaServer");
		Host hanaServer=hanaServerList.get(0);
				
		List<Host> allMachine=new ArrayList<Host>();
		allMachine.addAll(csmServerList);
		allMachine.addAll(occServerList);
		allMachine.addAll(jobServerList);
		allMachine.addAll(eshopServerList);
		allMachine.addAll(etcdServerList);
		if (isLocal.equals("false")){
			allMachine.addAll(shareFolderServerList);
		}
		if (mariadbServerList!=null){
			allMachine.addAll(mariadbServerList);
		}
		logger.info("Start to Copy Shell Scripts");
		try {
			RunRemoteSSH rrs;
			String windowsFolderTool=windowsFolder+"\\Tool\\";
			String prepareFolderCmd="if [ ! -d "+mainfolder+" ] ; then mkdir "+mainfolder+" && chmod 777 "+mainfolder+" ; fi && cd "+mainfolder+" && rm -rf *.sh && apt-get install -y tofrodos sysstat";
			//TODO COPY Jmeter/JmeterScriptsOnly
			String copyAlltoWindowsCmd="cmd /c xcopy /s /y /h /d /q \""+remoteCenterServerMainPath+"\\Tool\\Jmeter\\*.*\" \""+windowsFolder+"\\Tool\\Jmeter\\\" && xcopy /s /y /q \""+remoteCenterServerMainPath+"\\Tool\\JmeterScript\\*.*\" \""+windowsFolder+"\\Tool\\JmeterScript\\\"";
			RunRemoteSSH rrsWindows=new RunRemoteSSH(windowsServer.getIP(),windowsServer.getUserName(),windowsServer.getUserPassword());
			rrsWindows.setLogger(logger);
			rrsWindows.execute(copyAlltoWindowsCmd);
			
			RunRemoteSSH rrsCentral=new RunRemoteSSH(centralServer.getIP(),
					centralServer.getUserName(),
					centralServer.getUserPassword());
			rrsCentral.setLogger(logger);
			
			for (Host host:allMachine){
				logger.info("Current machine:"+host.getIP());
				rrs=new RunRemoteSSH(host.getIP(),host.getUserName(),host.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(prepareFolderCmd);
				String copyShellCmd="cmd /c cd "+windowsFolderTool+" && pscp -r -pw "+host.getUserPassword()+" -unsafe .\\ShellScript\\NotHANA\\*.sh "+host.getUserName()+"@"+host.getIP()+":"+mainfolder;
				if (!csmServerList.contains(host)&&!occServerList.contains(host)&&!jobServerList.contains(host)&&!eshopServerList.contains(host)){
					copyShellCmd="cmd /c cd "+windowsFolderTool+" && pscp -r -pw "+host.getUserPassword()+" -unsafe .\\ShellScript\\HANA\\*.sh "+host.getUserName()+"@"+host.getIP()+":"+mainfolder;
				}
				rrsCentral.execute(copyShellCmd);
				String afterCopyFolderCmd="cd "+mainfolder+" && fromdos *.sh && chmod 777 *.sh";
//				if (csmServerList.contains(host)){
//					//Fix Duplicated Copy
//					String prepareJmeterFolderCmd="if [ ! -d /opt/apache-jmeter-2.9/ ] ; then mkdir /opt/apache-jmeter-2.9/ && chmod 777 /opt/apache-jmeter-2.9/; fi";
//
//					rrs.execute(prepareJmeterFolderCmd);
//					String PreCheckExistCMD = "if [ -d /opt/apache-jmeter-2.9/bin ] ; then echo 1 ; else echo 0; fi;";
//					String Flag=rrs.executeWithReturnString(PreCheckExistCMD).replace("\r", "").replace("\n", "");
//					if(Integer.parseInt(Flag.trim())==0){
//						String csmJmeterCmd="cmd /c cd "+windowsFolderTool+" && pscp -r -pw "+host.getUserPassword()+" -unsafe .\\apache-jmeter-2.9\\*.* "+host.getUserName()+"@"+host.getIP()+":/opt/apache-jmeter-2.9/";
//						rrsCentral.execute(csmJmeterCmd);
//						afterCopyFolderCmd="cd "+mainfolder+" && fromdos *.sh && chmod 777 *.sh && chmod -R 777 /opt/apache-jmeter-2.9/ && fromdos /opt/apache-jmeter-2.9/bin/*.sh";
//					}	
//				}
				rrs.execute(afterCopyFolderCmd,true);
			}
			//For Hana Server
			logger.info("Current machine:"+hanaServer.getIP());
			RunRemoteSSH rrsHANA=new RunRemoteSSH(hanaServer.getIP(),hanaServer.getUserName(),hanaServer.getUserPassword());
			rrsHANA.setLogger(logger);
			rrsWindows.execute(copyAlltoWindowsCmd);
			prepareFolderCmd="if [ ! -d "+mainfolder+" ] ; then mkdir "+mainfolder+" && chmod 777 "+mainfolder+" ; fi && cd "+mainfolder+" && rm -rf *.sh";
			rrsHANA.execute(prepareFolderCmd);
			String copyHanaShellCmd="cmd /c cd "+windowsFolderTool+" && pscp -scp -r -pw "+hanaServer.getUserPassword()+" -unsafe .\\ShellScript\\HANA\\*.* "+hanaServer.getUserName()+"@"+hanaServer.getIP()+":"+mainfolder;
			rrsCentral.execute(copyHanaShellCmd,true,2,120000);
			String afterCopyHanaShellCmd="cd "+mainfolder+" && rpm -ivh dos2unix*.rpm\nrpm -ivh sysstat*.rpm\ndos2unix *.sh && chmod 777 *.sh";
			rrsHANA.execute(afterCopyHanaShellCmd);
			
			logger.info("Finish to Copy Shell Scripts.");
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
		return "Task for Copy Shell Scripts.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common
		//parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		//parameters.put("windowsFolder", new TaskParameterEntry("C:\\","Windows Main Folder"));
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
		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
				"Initial0", "Central Windows Server"));
		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList,
				"Host List for Central Windows"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new ShellScriptCopy().getDescription();
	}
	
}
