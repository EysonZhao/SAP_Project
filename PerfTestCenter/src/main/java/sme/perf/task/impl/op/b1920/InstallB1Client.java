package sme.perf.task.impl.op.b1920;

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

	//hosts
	private Host centralServer;
	private List<Host> b1ClientHostList;
	@Override
	public Result execute() throws ParameterMissingException {
		this.status = Status.Running;
		//get parameters value
		buildPath = (String)parameters.getValue("buildPath");
		clientPlatform = (String) parameters.getValue("client_x86_or_x64");
		
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
		// TODO Auto-generated method stub
		return null;
	}
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		
		parameters.put("client_x86_or_x64", new TaskParameterEntry("x64","x64/x86"));
		parameters.put("buildPath", new TaskParameterEntry("\\\\10.58.136.41\\e$\\B1Builds\\9.2_PL00_1440625\\Upgrade_920.003.00_CD_1440625_HANA.rar","The build which will be used to install."));
		
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
		return "Run this task to Install B1 client (9.2).";
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
				//extract b1 client Prerequisites
				String extractCmdTemplate = "%s\\7Zip\\7z x %s -o%s -y \"Prerequisites\" -r -aoa";
				String extractCmd = String.format(extractCmdTemplate, localEssentialPath, buildPath, localBuildPath);
				logger.info("Extract B1 client prerequistiest.");
				rrs.execute(extractCmd);
				
				//extract b1 diapi
				extractCmdTemplate = "%s\\7Zip\\7z x %s -o%s -y \"Packages%s\\DI API\" -r -aoa";
				extractCmd = String.format(extractCmdTemplate, localEssentialPath, buildPath, localBuildPath, clientPlatform.equals("x86")? "" :".x64");
				logger.info("Extract B1 DI API.");
				rrs.execute(extractCmd);
								
				//install DI API
				String issFileName = String.format("%s\\InstallShield\\InstallB1DIAPI_920_%s.iss", localEssentialPath, clientPlatform.equals("x86") ? "x86" : "x64");
				String setupFileName =  String.format("%s\\Packages%s\\DI API\\setup.exe", localBuildPath, clientPlatform.equals("x86") ? "" : ".x64");
				String installCmd = String.format("%s /s /f1\"%s\"", setupFileName, issFileName);
				logger.info("Install B1 DIAPI.");
				rrs.execute(installCmd, false, 0, 30 * 60 * 1000);

				//extract b1 client to local
				extractCmdTemplate = "%s\\7Zip\\7z x %s -o%s -y \"Packages%s\\Client\\*\" -r -aoa";
				extractCmd = clientPlatform.equals("x86") ? String.format(extractCmdTemplate, localEssentialPath, buildPath, localBuildPath, "") 
						:String.format(extractCmdTemplate, localEssentialPath, buildPath, localBuildPath, ".x64");
				logger.info("Extract B1 client.");
				rrs.execute(extractCmd);
				
				//run setup with iss file as parameter
				issFileName = clientPlatform.equals("x86") ? String.format("%s\\InstallShield\\InstallB1Client_920_x86.iss", localEssentialPath)
						: String.format("%s\\InstallShield\\InstallB1Client_920_x64.iss", localEssentialPath);
				setupFileName =  String.format("%s\\Packages%s\\Client\\setup.exe", localBuildPath, clientPlatform.equals("x86") ? "" : ".x64");
				installCmd = String.format("%s /s /f1\"%s\"", setupFileName, issFileName);
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
