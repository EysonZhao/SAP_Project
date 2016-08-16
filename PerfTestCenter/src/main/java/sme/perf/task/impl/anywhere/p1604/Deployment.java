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

public class Deployment extends Task {

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException{
		this.status = Status.Running;

		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String mainfolder = parameters.getValue("mainfolder").toString();
		String repository = parameters.getValue("repository").toString();
		String remoteSubvolume = parameters.getValue("remoteSubvolume").toString();
		String hanaInstanceFull = parameters.getValue("hanaInstanceFull").toString();
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		String isLocal = parameters.getValue("isLocal").toString();	
		String buildInfo = parameters.getValue("buildInfo").toString();
		
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer = csmServerList.get(0);

		List<Host> centralServerList = (List<Host>) hostParameters.getValue("centralServer");
		Host centralServer = centralServerList.get(0);
		List<Host> occServerList = (List<Host>) hostParameters.getValue("occServer");
		List<Host> jobServerList = (List<Host>) hostParameters.getValue("jobServer");
		List<Host> eshopServerList = (List<Host>) hostParameters.getValue("eshopServer");

		List<Host> shareFolderServerList = (List<Host>) hostParameters.getValue("shareFolderServer");
		Host shareFolderServer = shareFolderServerList.get(0);
		List<Host> etcdServerList = (List<Host>) hostParameters.getValue("etcdServer");
		Host etcdServer = etcdServerList.get(0);
		//TODO MariaDB should be integrated in INIT CSM OR NOT?
//		List<Host> mariadbServerList = (List<Host>) hostList.getValue("mariadbServer",true);
//		if (mariadbServerList!=null){
//			continue;
//		}
		
//		String occIpList = getStringfyHostIPList(occServerList);
//		String jobIpList = getStringfyHostIPList(jobServerList);
//		String eshopIpList = getStringfyHostIPList(eshopServerList);

		List<Host> otherServerList=new ArrayList<Host>();
		otherServerList.addAll(occServerList);
		otherServerList.addAll(jobServerList);
		otherServerList.addAll(eshopServerList);
		
		String windowsFolderTool = windowsFolder + "\\Tool\\";
		String mainfolderBuild = mainfolder + "/Build";
		logger.info("Start to Deploy");
		try {
			//TODO
			
			/* Step 1 Folder Prepare  Docker/Image/Certification
			 * Step 2 CSM Install
			 * Step 3 Add Package
			 * Step 4 Init CSM
			 */

			
			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(),
					csmServer.getUserPassword());
			String prepareFolderCmd = "if [ ! -d " + mainfolderBuild
					+ " ] ; then mkdir " + mainfolderBuild + " && chmod 777 "
					+ mainfolderBuild + " ; fi";
			rrsCSM.setLogger(logger);
			rrsCSM.execute(prepareFolderCmd);
			
			String copyBuildPreCheckCmd = "cd "+mainfolderBuild+" && rm -rf *";
			rrsCSM.execute(copyBuildPreCheckCmd, true);

			String prepareKeystoreFoldercmd="if [ ! -d /opt/sap ] ; then mkdir /opt/sap && chmod 777 /opt/sap ; fi && if [ ! -d /opt/sap/keystore ] ; then mkdir /opt/sap/keystore && chmod 777 /opt/sap/keystore ; fi";
			rrsCSM.execute(prepareKeystoreFoldercmd);
			String copyKeyStoreCmd = "cmd /c cd " + windowsFolderTool
					+ " && pscp -r -pw " + csmServer.getUserPassword()
					+ " -unsafe .\\keystore\\ " + csmServer.getUserName() + "@"
					+ csmServer.getIP() + ":/opt/sap/keystore";
			RunRemoteSSH rrsCentral=new RunRemoteSSH(centralServer.getIP(),
					centralServer.getUserName(),
					centralServer.getUserPassword());
			rrsCentral.setLogger(logger);
			rrsCentral.execute(copyKeyStoreCmd);

			//Installation
			//docker_img="192.168.12.11:5000/occ_patch/anw-extra:20160309025558"
			//registry_host="10.58.113.37:5000"
			//docker run --rm $docker_img csm gen-ini >> csm.ini
			String dockerimg=repository+"/occ_patch/anw-extra:"+buildInfo;
			String generateIniCmd = "cd " + mainfolderBuild
					+ " && docker run --rm " + dockerimg
					+ " csm gen-ini >> csm.ini";
			rrsCSM.execute(generateIniCmd);			

			String modifyiniCmd = "cd " + mainfolderBuild
					+ " && sed -i \"s/\\(^HOST_NAME=\\)\\(.*\\)/\\1`hostname`/g\" csm.ini"
					+ " && sed -i \"s/\\(^HOST_FQDN=\\)\\(.*\\)/\\1`hostname -f`/g\" csm.ini"
					+ " && sed -i \"s/\\(^HOST_IP=\\)\\(.*\\)/\\1`hostname -i`/g\" csm.ini"
					+ " && sed -i \"s/\\(^CSM_URL=\\)\\(.*\\)/\\1https:\\/\\/`hostname -f`:8444\\/sld\\//g\" csm.ini"
					+ " && sed -i \"s/\\(^IMAGE_REPOSITORY=\\)\\(.*\\)/\\1"+repository+"/g\" csm.ini";
			rrsCSM.execute(modifyiniCmd);	
			
			String installCmd = "cd "
					+ mainfolderBuild
					+ " && docker run --rm -v /opt/logs:/opt/logs -v /opt/sap:/opt/sap -v /var/run:/var/run/docker --env-file csm.ini "
					+ dockerimg + " csm install";
			rrsCSM.execute(installCmd);

			String packageCmd = "cd "
					+ mainfolderBuild
					+ " && docker run --rm -v /opt/logs:/opt/logs -v /opt/sap:/opt/sap --env-file csm.ini "
					+ dockerimg + " csm add-package";
			rrsCSM.execute(packageCmd);
			
			if(isLocal.equals("false")){
				//UPDATE PROXY ISSUE
				String updateProxyCmd = "mysql -h "+csmServer.getIP()+" -uroot -p"
						+ mariaDBPasswd
						+ " -e \"UPDATE CSM.GLOBALSETTING SET VALUE='' WHERE NAMESPACE='PROXY'\"";
				rrsCSM.execute(updateProxyCmd);
			}
			
			String addHostCmd = "cd "
					+ mainfolderBuild
					+ " && docker run --rm -v /opt/sap:/opt/sap -v /opt/logs:/opt/logs "
					+ dockerimg
					+ " /usr/local/apache-jmeter-2.12/bin/jmeter -n -t /root/buildResult/ta/jmx/dmz_add_hosts.jmx -Jhost "
					+ csmServer.getHostName()
					+ " -Jcsmusr operator -Jcsmpwd Initial0 -Jcsmurl https://"
					+ csmServer.getHostName()
					+ ":8444/sld/ -Jhostlist "+getStringfyHostNameListWithTag(otherServerList)+" -l /opt/logs/syslog/dmz_add_hosts.csv";
			rrsCSM.execute(addHostCmd);
			
			if(isLocal.equals("false")){
				String updateLandscapeUsageCmd = "mysql -h "+csmServer.getIP()+" -uroot -p"
						+ mariaDBPasswd
						+ " -e \"UPDATE CSM.GLOBALSETTING SET VALUE='PRODUCT' WHERE VALUE='CI'\"";
				rrsCSM.execute(updateLandscapeUsageCmd);
			}
		} catch (Exception e) {
			logger.info("Failed to Deploy.");
			logger.error(e.getMessage());
			this.status = Status.Failed;
			return Result.Fail;
		}
		logger.info("Finish to Deploy.");
		this.status = Status.Finished;
		return Result.Pass;
	}
	
	@Override
	public String getDescription() {
		return "Task for Deploy CSM and Run Init CSM.";
	}

	public static TaskParameterMap getParameterTemplate() {
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		// Common
		// parameters.put("mainfolder", new
		// TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		// parameters.put("windowsFolder", new
		// TaskParameterEntry("C:\\","Windows Main Folder"));
		
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115","Full Hana Instance URL"));
		parameters.put("buildInfo", new TaskParameterEntry("201605130256","buildInfo"));
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
		//TEMP
		//List<Host> mariadbhostList = new ArrayList<Host>();
		//mariadbhostList.add(new Host("cnpvgvb1pf012.pvgl.sap.corp", "10.58.8.46", "root",
		//		"Initial0", "Mariadb Server"));
		//hostsParameter.put("mariadbServer", new TaskParameterEntry(mariadbhostList,
		//		"Host List for MariaDB Machine"));
		List<Host> centralServerList = new ArrayList<Host>();
		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp", "10.58.8.44", "administrator",
				"Initial0", "Central Windows Server"));
		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList,
				"Host List for Central Windows"));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	private static String getStringfyHostIPList(List<Host> listHost) {
		String hostIpList = "";
		for (Host h : listHost) {
			if (listHost.indexOf(h) == listHost.size() - 1) {
				hostIpList = hostIpList + h.getHostName();
			} else {
				hostIpList = hostIpList + h.getHostName() + ",";
			}
		}
		return hostIpList;
	}
	
	private static String getStringfyHostNameListWithTag(List<Host> listHost) {
		String hostIpList = "";
		for (Host h : listHost) {
			if (listHost.indexOf(h) == listHost.size() - 1) {
				hostIpList = hostIpList + h.getHostName()+":external_nginx";
			} else {
				hostIpList = hostIpList + h.getHostName()+":external_nginx,";
			}
		}
		return hostIpList;
	}
	
	
	public static String getDescriptionTemplate() {
		return new Deployment().getDescription();
	}
}
