package sme.perf.task.impl.op.b1910;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
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
import sme.perf.utility.PropertyFile;
import sme.perf.utility.RunRemoteSSH;

public class InstallB1Client extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostList;
	private Status status = Status.New;
	private Result result = Result.Unknown;
	
	//parameters
	private String buildPath;
	private String clientPlatform;
	private String licenseServer;

	//hosts
	private Host centralServer;
	private List<Host> b1ClientHostList;
	@Override
	public Result execute() throws ParameterMissingException {
		this.status = Status.Running;
		//get parameters value
		buildPath = (String)parameters.getValue("buildPath");
		clientPlatform = (String) parameters.getValue("client_x86_or_x64");
		licenseServer = (String) parameters.getValue("licenseServer");
		
		//get hosts 
		b1ClientHostList = (List<Host>) hostList.getValue("TestClientHosts");
		List<InstallB1ClientThread> installThreadList = new ArrayList<InstallB1ClientThread>();
		for(Host client: b1ClientHostList){
			InstallB1ClientThread installThread = new InstallB1ClientThread(client);
			installThreadList.add(installThread);
			installThread.start();
		}
		for(InstallB1ClientThread thread : installThreadList){
			try{
			thread.join();
			}
			catch(Exception e){
				logger.error(e);
			}
		}
		
		result = Result.Pass;
		for(InstallB1ClientThread thread : installThreadList){
			if(thread.getResult() == Result.Fail){
				logger.error(String.format("B1 Client Install Failed @ %s", thread.getClient().getIP()));
				this.result = Result.Fail;
			}
		}

		status = Status.Finished;
		return result;
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		// TODO Auto-generated method stub
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		return this.parameters;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	@Override
	public void setHosts(TaskParameterMap hostList) {
		this.hostList = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		return this.hostList;
	}

	@Override
	public String getDescription() {
		return getDescriptionTemplate();
	}
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		
		parameters.put("client_x86_or_x64", new TaskParameterEntry("x64","x64/x86"));
		parameters.put("buildPath", new TaskParameterEntry("\\\\10.58.6.49\\builds_cn\\SBO\\9.1_COR\\910.230.13_CNPVG50882200DV_SBO_EMEA_9.1_COR_020616_161714_1467917.HANA\\Upgrade_910.230.13_CD_1467917_HANA.rar"
				,"The build which will be used to install."));
		parameters.put("licenseServer", new TaskParameterEntry("10.58.136.102:40000","The license server and port."));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}
	
	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.51", "administrator",
				"Initial0", "B1 Client Host"));
		hostsParameter.put("TestClientHosts", new TaskParameterEntry(clientHostList,
				"The hosts will be used in this test."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}
	
	public static String getDescriptionTemplate() {
		return "Run this task to B1 client (9.1).";
	}
	
	class InstallB1ClientThread extends Thread{
		private Host client; 
		private Result result = Result.Unknown;
		
		public Result getResult() {
			return result;
		}

		public Host getClient() {
			return client;
		}

		public InstallB1ClientThread(Host client){
			this.client = client;
		}
		
		@Override
		public void run(){
			try {
				RunRemoteSSH rrs = new RunRemoteSSH(client.getIP(), client.getUserName(), client.getUserPassword());
				rrs.setLogger(logger);
				
				//copy essential from remote to local
				String remoteEssentialPath = PropertyFile.getValue("RemoteEssential");
				String localEssentialPath = String.format("%s\\%s", PropertyFile.getValue("LocalRootPath"), PropertyFile.getValue("LocalEssentialPath"));
				String xCopyEssentialCmd = String.format("cmd /c xcopy /y /h /q /e /i  \"%s\" \"%s\"", remoteEssentialPath, localEssentialPath );
				
				logger.info("Copy Essential Files.");
				rrs.execute(xCopyEssentialCmd);

				String localBuildPath = String.format("%s\\Client", localEssentialPath);
				//extract the B1 Client zip file
				//x64
				String extractCmdTemplate = "%s\\7Zip\\7z e %s -o%s -y \"Wizard\\Upgrader Common\\Queries\\Blob\\SCABApplCab%sSCAB0\" -r -aoa";
				String extractCmd = String.format(extractCmdTemplate, localEssentialPath, buildPath, localBuildPath, clientPlatform.equals("x64")?"64":"");
				logger.info("Extract B1 client Blob.");
				rrs.execute(extractCmd);
				
				//extract b1 B1 client from blob zip file
				extractCmdTemplate = "%s\\7Zip\\7z x %s\\SCABApplCab%sSCAB0 -o%s -y -r -aoa";
				extractCmd = String.format(extractCmdTemplate, localEssentialPath, localBuildPath, clientPlatform.equals("x64")? "64" :"", localBuildPath);
				logger.info("Extract B1 Client.");
				rrs.execute(extractCmd);
				
				//run setup using silence mode
				String setupFileName =  String.format("%s\\setup.exe", localBuildPath);
				String installCmd = String.format("%s /S /z\"c:\\Program Files%s\\SAP\\SAP Business One Client\\*%s\"", 
						setupFileName,
						clientPlatform.equals("x86")? " (x86)": "",
						licenseServer);
				logger.info("Install B1 Client.");
				rrs.execute(installCmd, false, 0, 30 * 60 * 1000);
				
				result = Result.Pass;
			} catch (Exception e) {
				logger.error(e);
				result = Result.Fail;
			}
		}
	}
}
