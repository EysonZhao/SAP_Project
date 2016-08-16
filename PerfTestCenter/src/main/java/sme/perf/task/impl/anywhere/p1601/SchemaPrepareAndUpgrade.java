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

public class SchemaPrepareAndUpgrade extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status = Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status = Status.Running;

		String windowsFolder = parameters.getValue("windowsFolder").toString();
		String mainfolder = parameters.getValue("mainfolder").toString();
		String hanaInstanceFull = parameters.getValue("hanaInstanceFull").toString();
		String hanaPasswd = parameters.getValue("hanaPasswd").toString();
		String hanaUser = parameters.getValue("hanaUser").toString();
		String schemaBackupFolder = parameters.getValue("schemaBackupFolder").toString();
		String version = parameters.getValue("version").toString();
		String sourceSchemaName = parameters.getValue("sourceSchemaName").toString();

		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer = csmServerList.get(0);
		List<Host> hanaServerList = (List<Host>) hostParameters.getValue("hanaServer");
		Host hanaServer = hanaServerList.get(0);
		List<Host> centralServerList = (List<Host>) hostParameters.getValue("centralServer");
		Host centralServer=centralServerList.get(0);

		String windowsFolderTool=windowsFolder+"\\Tool\\";
		logger.info("Start to Prepare Schema and Upgrade.");
		try {
			logger.info("Start to Prepare Schema backup on HANA machine.");
			// Prepare DB
			String prepareBackupCmd = "if [ ! -d " + schemaBackupFolder
					+ " ] ; then mkdir " + schemaBackupFolder
					+ " ; fi && chmod -R 777 " + schemaBackupFolder + " && cd "
					+ schemaBackupFolder + " && rm -rf export index *.tgz";
			RunRemoteSSH rrsHANA=new RunRemoteSSH(hanaServer.getIP(),hanaServer.getUserName(),hanaServer.getUserPassword());
			rrsHANA.setLogger(logger);
			rrsHANA.execute(prepareBackupCmd);
			String copyBackupCmd = "cmd /c cd " + windowsFolderTool
					+ " && pscp -scp -r -pw " + hanaServer.getUserPassword()
					+ " -unsafe .\\SchemaBackup\\*.tgz "
					+ hanaServer.getUserName() + "@" + hanaServer.getIP() + ":"
					+ schemaBackupFolder;
			RunRemoteSSH rrsCentral=new RunRemoteSSH(centralServer.getIP(),
					centralServer.getUserName(),
					centralServer.getUserPassword());
			rrsCentral.setLogger(logger);
			rrsCentral.execute(copyBackupCmd);
			String afterBackupCmd = "cd "
					+ schemaBackupFolder
					+ " && if [ -f *.tgz ] ; then tar -zxvf *.tgz ; fi && chmod -R 777 export index";
			rrsHANA.execute(afterBackupCmd);
			logger.info("Finish to Prepare Schema backup on HANA machine.");

			logger.info("Start to Upgrade PERFDB.");
			
			// import TemplateDB
			String importBackupCmd = "source ~/.bashrc && hdbsql -n "
					+ hanaInstanceFull + " -u SYSTEM -p manager \"import \\\""
					+ sourceSchemaName + "\\\".\\\"*\\\" as binary from '"
					+ schemaBackupFolder + "' with replace threads 10\"";
			RunRemoteSSH rrsHANAUser=new RunRemoteSSH(hanaServer.getIP(), hanaUser, hanaPasswd);
			rrsHANAUser.setLogger(logger);
			rrsHANAUser.execute(importBackupCmd);
			
			// Before Upgrade
			// Original
			// String upgradePreCheckCmd = "cd " + mainfolder
			// + " && rm -rf sapjvm_7_jre/ lib/ *.jar *.zip";
			// RunRemoteSSH.execute(csmServer.getIP(),
			// csmServer.getUserName(),csmServer.getUserPassword(),
			// upgradePreCheckCmd,true);
			//
			// // Upgrade
			// String
			// csmCopyShellCmd="cmd /c cd "+windowsFolderTool+" && pscp -r -pw "+csmServer.getUserPassword()+" -unsafe .\\SchemaBackup\\*.jar .\\SchemaBackup\\*.zip .\\SchemaBackup\\schema-upgrade-sfa\\lib .\\SchemaBackup\\SAP_HANA_CLIENT "+csmServer.getUserName()+"@"+csmServer.getIP()+":"+mainfolder;
			// RunRemoteSSH.execute(centralServer.getIP(),centralServer.getUserName(),centralServer.getUserPassword(),csmCopyShellCmd);
			//
			// String upgradeCmd = "cd "
			// + mainfolder
			// + "/SAP_HANA_CLIENT && chmod -R 777 * && ./hdbinst -b ;"
			// +
			// "cd .. && unzip sapjvm_7_jre.zip && chmod -R 777 lib/* sapjvm_7_jre/* ;"
			// +
			// "./sapjvm_7_jre/bin/java -cp lib/*:anw-upgrade.jar:/usr/sap/hdbclient/ngdbc.jar com.sap.sbo.dbtools.patches.UpgradeMain component=sfa command=update to="
			// + version + " db=hana schema=" + sourceSchemaName + " url="
			// + hanaInstanceFull
			// + " username=SYSTEM password=manager locale=zh_CN";
			// RunRemoteSSH.execute(csmServer.getIP(), csmServer.getUserName(),
			// csmServer.getUserPassword(), upgradeCmd);
			// logger.info("Finish to Upgrade PERFDB.");

			//Fix Duplicated Copy
			RunRemoteSSH rrsCSM=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(),
					csmServer.getUserPassword());
			rrsCSM.setLogger(logger);
			String PreCheckExistCMD = "if [ -d "+mainfolder+"/SAP_HANA_CLIENT/ ] ; then echo 1 ; else echo 0; fi;";
			String FlagHDBClient = rrsCSM.executeWithReturnString(PreCheckExistCMD).replace("\r", "").replace("\n", "");

			if (Integer.parseInt(FlagHDBClient.trim()) == 0) {
				String CsmCopyJVMCmd = "cmd /c cd " + windowsFolderTool
						+ " && pscp -r -pw " + csmServer.getUserPassword()
						+ " -unsafe .\\SchemaBackup\\SAP_HANA_CLIENT "
						+ csmServer.getUserName() + "@" + csmServer.getIP() + ":"
						+ mainfolder;
				rrsCentral.execute(CsmCopyJVMCmd);
				String installHanaClientCmd = "cd "
				+ mainfolder
				+ "/SAP_HANA_CLIENT && chmod -R 777 * && ./hdbinst -b";
				rrsCSM.execute(installHanaClientCmd);
			}
			
			String PreCheckJavaExistCMD = "if [ -d "+mainfolder+"/sapjvm_7_jre/bin ] ; then echo 1 ; else echo 0; fi;";
			String FlagJava=rrsCSM.executeWithReturnString(PreCheckJavaExistCMD).replace("\r", "").replace("\n", "");
			if(Integer.parseInt(FlagJava.trim())==0){
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
			
			String upgradePreCheckCmd = "cd " + mainfolder
					+ " && rm -rf lib/ *.jar";
			rrsCSM.execute(upgradePreCheckCmd, true);

			// Upgrade
			String copyUpgradePackageCmd = "cmd /c cd "
					+ windowsFolderTool
					+ " && pscp -r -pw "
					+ csmServer.getUserPassword()
					+ " -unsafe .\\SchemaBackup\\*.jar .\\SchemaBackup\\schema-upgrade-sfa\\lib "
					+ csmServer.getUserName() + "@" + csmServer.getIP() + ":"
					+ mainfolder;
			rrsCentral.execute(copyUpgradePackageCmd);

			String upgradeCmd = "cd "
					+ mainfolder+ " && chmod -R 777 lib/*;"
					+ "./sapjvm_7_jre/bin/java -cp lib/*:anw-upgrade.jar:/usr/sap/hdbclient/ngdbc.jar com.sap.sbo.dbtools.patches.UpgradeMain component=sfa command=update to="
					+ version + " db=hana schema=" + sourceSchemaName + " url="
					+ hanaInstanceFull
					+ " username=SYSTEM password=manager locale=zh_CN";
			rrsCSM.execute(upgradeCmd);
			logger.info("Finish to Upgrade PERFDB.");
			
			
			logger.info("Start to export to DBtemplate.");
			// export TemplateDB
			String exportBackupCmd = "source ~/.bashrc && hdbsql -n "
					+ hanaInstanceFull + " -u SYSTEM -p manager \"export \\\""
					+ sourceSchemaName + "\\\".\\\"*\\\" as binary into '"
					+ schemaBackupFolder + "' with replace threads 10\"";
			rrsHANAUser.execute(exportBackupCmd);

			logger.info("Finish to export to DBtemplate.");
			logger.info("Finish to Prepare Schema and Upgrade.");
			this.status = Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.status = Status.Failed;
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
			this.parameters = SchemaPrepareAndUpgrade.getParameterTemplate();
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
			this.hostParameters = SchemaPrepareAndUpgrade.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Schema Prepare and Upgrade";
	}

	public static TaskParameterMap getParameterTemplate() {
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		//Common??
		//parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
		parameters.put("schemaBackupFolder", new TaskParameterEntry("/root/perftest/hanatmpbackup/PERFDB1","Linux Main Folder"));
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115","Full Hana Instance URL"));
		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
		parameters.put("hanaUser", new TaskParameterEntry("cdbadm","Hana User Name"));
		parameters.put("version", new TaskParameterEntry("7.0.0","OCC version"));
		
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> CSMhostList = new ArrayList<Host>();
		CSMhostList.add(new Host("cnpvgvb1pf009.pvgl.sap.corp", "10.58.8.43",
				"root", "Initial0", "CSM Server"));
		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,
				"Host List for CSM,SP,IDP,etc."));
//		List<Host> WindowshostList = new ArrayList<Host>();
//		WindowshostList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp",
//				"10.58.8.44", "administrator", "Initial0", "Windows Server"));
//		hostsParameter.put("windowsServer", new TaskParameterEntry(
//				WindowshostList, "Host List for WindowsMachine"));
		List<Host> hanahostList = new ArrayList<Host>();
		hanahostList.add(new Host("CNPVG50819783.pvgl.sap.corp",
				"10.58.120.235", "root", "Initial0", "HANA Server"));
		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,
				"Host List for HANA Machine"));
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
		return new SchemaPrepareAndUpgrade().getDescription();
	}
	
}
