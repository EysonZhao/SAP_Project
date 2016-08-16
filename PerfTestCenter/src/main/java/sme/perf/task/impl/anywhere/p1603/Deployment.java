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
		
		String occIpList = getStringfyHostIPList(occServerList);
		String jobIpList = getStringfyHostIPList(jobServerList);
		String eshopIpList = getStringfyHostIPList(eshopServerList);

		String windowsFolderTool = windowsFolder + "\\Tool\\";
		String mainfolderBuild = mainfolder + "/Build";
		logger.info("Start to Deploy");
		try {
			// Old Version
			/*
			 * String prepareFolderCmd = "if [ ! -d " + mainfolderBuild +
			 * " ] ; then mkdir " + mainfolderBuild + " && chmod 777 " +
			 * mainfolderBuild + " ; fi";
			 * RunRemoteSSH.execute(csmServer.getIP(), csmServer.getUserName(),
			 * csmServer.getUserPassword(), prepareFolderCmd);
			 * 
			 * String copyBuildCmd = "cmd /c cd " + windowsFolderTool +
			 * " && pscp -r -pw " + csmServer.getUserPassword() +
			 * " -unsafe .\\Build\\ " + csmServer.getUserName() + "@" +
			 * csmServer.getIP() + ":" + mainfolderBuild;
			 * RunRemoteSSH.execute(centralServer.getIP(),
			 * centralServer.getUserName(), centralServer.getUserPassword(),
			 * copyBuildCmd);
			 * 
			 * String modifyCmd = "sed -i 's/localhost/" + csmServer.getIP() +
			 * ":3306/g' " + mainfolderBuild +
			 * "/ta/csm/install.ini && sed -i 's/SYSTEM/root/g' " +
			 * mainfolderBuild +
			 * "/ta/csm/install.ini && sed -i 's/manager/12345/g' " +
			 * mainfolderBuild +
			 * "/ta/csm/install.ini && sed -i 's/10.58.113.37:5000/" +
			 * repository + "/g' " + mainfolderBuild + "/ta/csm/install.ini";
			 * RunRemoteSSH.execute(csmServer.getIP(), csmServer.getUserName(),
			 * csmServer.getUserPassword(), modifyCmd); String installCmd =
			 * "cd " + mainfolder + "/Build/ta && python install.py";
			 * RunRemoteSSH.execute(csmServer.getIP(), csmServer.getUserName(),
			 * csmServer.getUserPassword(), installCmd);
			 */

			// New Version
			String prepareFolderCmd = "if [ ! -d " + mainfolderBuild
					+ " ] ; then mkdir " + mainfolderBuild + " && chmod 777 "
					+ mainfolderBuild + " ; fi";
			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(),
					csmServer.getUserPassword());
			rrsCSM.setLogger(logger);
			rrsCSM.execute(prepareFolderCmd);
			
			String copyBuildPreCheckCmd = "cd "+mainfolderBuild+" && rm -rf *";
			rrsCSM.execute(copyBuildPreCheckCmd, true);
			
			String copyBuildCmd = "cmd /c cd " + windowsFolderTool
					+ " && pscp -r -pw " + csmServer.getUserPassword()
					+ " -unsafe .\\Build\\ " + csmServer.getUserName() + "@"
					+ csmServer.getIP() + ":" + mainfolderBuild;
			RunRemoteSSH rrsCentral=new RunRemoteSSH(centralServer.getIP(),
					centralServer.getUserName(),
					centralServer.getUserPassword());
			rrsCentral.setLogger(logger);
			rrsCentral.execute(copyBuildCmd);

			String modifyCmd = "sed -i 's/localhost/" + csmServer.getIP()
					+ "/g' " + mainfolderBuild
					+ "/ta/csm/install.ini && sed -i 's/SYSTEM/root/g' "
					+ mainfolderBuild
					+ "/ta/csm/install.ini && sed -i '/DB_PASSWORD = /'d "
					+ mainfolderBuild
					+ "/ta/csm/install.ini && sed -i '/DB_USERNAME =/'a\\\\\"DB_PASSWORD = "+mariaDBPasswd+"\" "
					+ mainfolderBuild
					+ "/ta/csm/install.ini && sed -i 's/10.58.113.37:5000/"
					+ repository + "/g' " + mainfolderBuild
					+ "/ta/csm/install.ini";
			rrsCSM.execute(modifyCmd);
			
			String mariaDBInstallCmd="";
			if (isLocal.equals("false")){
				mariaDBInstallCmd = "docker pull "
						+ repository
						+ ":5000/base_patch/mariadb-galera:10.0.21 && "
						+ "docker run -d --name mariadb -p 3306:3306 -p 4567:4567 -p 4568:4568 -p 4444:4444 -p 9200:9200 "
						+ "-v /mnt/data:/var/lib/mysql -v /mnt/backup/all.mysql:/backup/all.mysql:ro -e MYSQL_ROOT_PASSWORD="+ mariaDBPasswd
						+ "-e MYSQL_CLUSTER_PEER=xxxx -e MYSQL_CLUSTER_IP=`hostname -i` -e MYSQL_CLUSTER_NAME=`hostname` "
						+ "-e MYSQL_BACKUP=/backup/all.mysql -e MYSQL_CLUSTER=new mariadb-galera:10.0.21 ";
			}else{
				mariaDBInstallCmd = "docker pull "
						+ repository
						+ ":5000/base_master/mariadb &&  docker run -e MYSQL_ROOT_PASSWORD="
						+ mariaDBPasswd + " -p 3306:3306 -d " + repository
						+ ":5000/base_master/mariadb:latest";
			}
			rrsCSM.execute(mariaDBInstallCmd);

			String mariaDBConfigCmd ="mysql -h "+csmServer.getIP()+" -uroot -p" + mariaDBPasswd
					+ " -e \"SET GLOBAL binlog_format = 'MIXED'\";"
					+ "mysql -h "+csmServer.getIP()+" -uroot -p" + mariaDBPasswd
					+ " -e \"set global wait_timeout=28800\"";
			rrsCSM.execute(mariaDBConfigCmd);
			
			if (isLocal.equals("false")){
				//Original Modify - Just Delete
				//String modifyGlobalSettingCmd = " sed -i '/^Proxy,/d' " + mainfolderBuild
				//		+ "/global_setting/infrastructure_csm.csv";
				
				String modifyGlobalSettingCmd = " sed -i 's/,proxy.sin.sap.corp/,/g' "
						+ mainfolderBuild
						+ "/global_setting/infrastructure_csm.csv "
						+ " && sed -i 's/\".sap.corp,//g' "
						+ mainfolderBuild
						+ "/global_setting/infrastructure_csm.csv "
						+ " && sed -i 's/*.sap.corp\"//g' "
						+ mainfolderBuild
						+ "/global_setting/infrastructure_csm.csv "
						+ " && sed -i 's/,|/,/g' "
						+ mainfolderBuild
						+ "/global_setting/infrastructure_csm.csv "
						+ " && sed -i 's/,8080/,/g' "
						+ mainfolderBuild
						+ "/global_setting/infrastructure_csm.csv "
						+ " && sed -i 's/Storage,aws_using_proxy,TRUE,BOOLEAN,,,TRUE,TRUE,,,TRUE/Storage,aws_using_proxy,FALSE,BOOLEAN,,,TRUE,TRUE,,,FALSE/g' "
						+ mainfolderBuild
						+ "/global_setting/thirdintegration_csm.csv ";
				rrsCSM.execute(modifyGlobalSettingCmd);
			}

			//Add Step "Put JVM to enable to run Jmeter"

			// Before Copy JVM 
			//Orignal
			// String copyJVMPreCheckCmd = "cd " + mainfolder
			// + " && rm -rf sapjvm_7_jre/ *.zip";
			// RunRemoteSSH.execute(csmServer.getIP(),
			// csmServer.getUserName(),csmServer.getUserPassword(),
			// copyJVMPreCheckCmd,true);
			// String
			// CsmCopyJVMCmd="cmd /c cd "+windowsFolderTool+" && pscp -r -pw "+csmServer.getUserPassword()+" -unsafe .\\SchemaBackup\\*.zip"+csmServer.getUserName()+"@"+csmServer.getIP()+":"+mainfolder;
			// RunRemoteSSH.execute(centralServer.getIP(),centralServer.getUserName(),centralServer.getUserPassword(),CsmCopyJVMCmd);
			//
			// String installCmd = "cd "
			// + mainfolder
			// +
			// " && unzip sapjvm_7_jre.zip && chmod -R 777 sapjvm_7_jre/* && cd "
			// + mainfolder
			// +
			// "/Build/ta && export PATH=$PATH:"+mainfolder+"/sapjvm_7_jre/bin/ && python deploy.py -DB_SERVER "
			// + hanaInstanceFull
			// +
			// " -TYPE PFTA -SERVICE_LIST CHANNELINTEGRATION,IDP,SOLUTIONPORTAL -SU_LIST OCC,JOB -REMOTE_FS "
			// + shareFolderServer.getIP() + " -ETCD_HOST "
			// + etcdServer.getIP() + " -SVC_COUNT OCC:"
			// + occServerList.size() + "\\\\\\;JOB:" + jobServerList.size()
			// + "\\\\\\;ESHOP:" + eshopServerList.size() +
			// "\\\\\\;BYDC:1  -OCC_HOST "
			// + occIpList + " -JOB_HOST " + jobIpList + " -ESHOP_HOST "
			// + eshopIpList+ " -REMOTE_SUBVOLUME "+remoteSubvolume;
			// RunRemoteSSH.execute(csmServer.getIP(), csmServer.getUserName(),
			// csmServer.getUserPassword(), installCmd, false, 0,1200000);
			
			//Fix Duplicated Copy
			String PreCheckExistCMD = "if [ -d "+mainfolder+"/sapjvm_7_jre/bin ] ; then echo 1 ; else echo 0; fi;";
			String Flag=rrsCSM.executeWithReturnString(PreCheckExistCMD).replace("\r", "").replace("\n", "");
			if(Integer.parseInt(Flag.trim())==0){
				String CsmCopyJVMCmd = "cmd /c cd " + windowsFolderTool
						+ " && pscp -r -pw " + csmServer.getUserPassword()
						+ " -unsafe .\\SchemaBackup\\*.zip "
						+ csmServer.getUserName() + "@" + csmServer.getIP() + ":"
						+ mainfolder;
				rrsCentral.execute(CsmCopyJVMCmd);
				String JVMCmd = "cd "
						+ mainfolder
						+ " && unzip sapjvm_7_jre.zip && chmod -R 777 sapjvm_7_jre/*";
				rrsCSM.execute(JVMCmd);
			}
			String installCmd = "cd "
					+ mainfolder
					+ "/Build/ta && export PATH=$PATH:"
					+ mainfolder
					+ "/sapjvm_7_jre/bin/ && python deploy.py -DB_SERVER "
					+ hanaInstanceFull
					+ " -TYPE PFTA -SERVICE_LIST "
					//+"CHANNELINTEGRATION,"
					+"IDP,SOLUTIONPORTAL -SU_LIST OCC,JOB -REMOTE_FS "
					+ shareFolderServer.getIP() + " -ETCD_HOST "
					+ etcdServer.getIP() + " -SVC_COUNT OCC:"
					+ occServerList.size() + "\\\\\\;JOB:"
					+ jobServerList.size() + "\\\\\\;ESHOP:"
					+ eshopServerList.size() + "\\\\\\;BYDC:1  -OCC_HOST "
					+ occIpList + " -JOB_HOST " + jobIpList + " -ESHOP_HOST "
					+ eshopIpList + " -REMOTE_SUBVOLUME " + remoteSubvolume;
			rrsCSM.execute(installCmd, false, 0, 1200000);
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
	
	public static String getDescriptionTemplate() {
		return new Deployment().getDescription();
	}
}
