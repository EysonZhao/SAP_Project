package sme.perf.task.impl.op.b1920;

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
import sme.perf.task.impl.anywhere.p1601.ShellScriptCopy;
import sme.perf.utility.LogHelper;
import sme.perf.utility.RunRemoteSSH;

public class UninstallB1Client extends Task {

	private static final String cmd_uninstall_b1_client = "C:\\Program Files%s\\InstallShield Installation Information\\%s\\Setup.exe /uninst";
	private static final String cmd_read_uninstall_b1_client_guid = "cmd /c reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\SAP\" /v \"SAP Business One Client\" %s";
	private static final String cmd_read_uninstall_b1_diapi_guid = "cmd /c reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\SAP\" /v \"SAP Business One DI API%s\" %s";
	private TaskParameterMap parameters;
	private TaskParameterMap hostList;
	private Status status = Status.New;
	
	@Override
	public Result execute() throws ParameterMissingException {
		List<Host> clients = (List<Host>) hostList.getValue("TestClientHosts");
		List<UninstallThread> uninstallThreadList = new ArrayList<UninstallThread>();
		Result result = Result.Pass;
		
		for(Host client: clients){
			UninstallThread newThread = new UninstallThread(client);
			uninstallThreadList.add(newThread);
			newThread.start();
		}
		for(UninstallThread thread: uninstallThreadList){
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
		for(UninstallThread thread: uninstallThreadList){
			if(thread.getResult() == Result.Fail){
				return Result.Fail;
			}
		}

		return Result.Pass;
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
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
		parameters.put("client_x86_or_x64", new TaskParameterEntry("x64","B1 client platform."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();
		clientHostList.add(new Host("CNPVG50819353", "10.58.136.43", "administrator",
				"Initial0", "B1 Client Host"));
		hostsParameter.put("TestClientHosts", new TaskParameterEntry(clientHostList,
				"The hosts for   clients."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return "Uninstall B1 client.";
	}

	class UninstallThread extends Thread{
		private Host client; 
		private Result result = Result.Unknown;
		
		public UninstallThread (Host client){
			this.client = client;
		}
		
		public Result getResult(){
			return result;
		}
		@Override 
		public void run(){
			RunRemoteSSH rrs = new RunRemoteSSH(client.getIP(), client.getUserName(), client.getUserPassword());
			rrs.setLogger(logger);
			try {
				String strCmdRet = "";
								
				//read value from registry
				if(parameters.getValue("client_x86_or_x64") == "x86"){
					strCmdRet = rrs.executeWithReturnString(String.format(cmd_read_uninstall_b1_diapi_guid, "", "/reg:32"), true);
				}
				else{
					strCmdRet = rrs.executeWithReturnString(String.format(cmd_read_uninstall_b1_diapi_guid, " (64-bit)", "/reg:64"), true);
				}
				if(strCmdRet.trim().startsWith("ERROR")){
					logger.info("B1 DIAPI is not installed. Finish uninstall B1 client.");
				}
				else{
					String strGuid = strCmdRet.substring(strCmdRet.indexOf('{'), strCmdRet.indexOf('}') +1 );
					logger.debug("Uninstall B1 DIAPI Guid: " + strGuid);
					
					int retCode = 0;
					logger.info("Uninstall B1 DIAPI Start.");
					if(System.getProperty("os.arch") == "x86"){
						retCode = rrs.execute(String.format(cmd_uninstall_b1_client, "", strGuid));
					}
					else{
						retCode = rrs.execute(String.format(cmd_uninstall_b1_client, " (x86)", strGuid), false, 0, 20 * 60 * 1000);
					}
					logger.debug(String.format("Uninstall B1 DIAPI End. retCode = (%d)", retCode));
				}
				
				//read value from registry
				if(parameters.getValue("client_x86_or_x64") == "x86"){
					strCmdRet = rrs.executeWithReturnString(String.format(cmd_read_uninstall_b1_client_guid, "/reg:32"), true);
				}
				else{
					strCmdRet = rrs.executeWithReturnString(String.format(cmd_read_uninstall_b1_client_guid, "/reg:64"), true);
				}
				
				if(strCmdRet.trim().startsWith("ERROR")){
					logger.info("B1 client is not installed. Finish uninstall B1 client.");
				}
				else{
					String strGuid = strCmdRet.substring(strCmdRet.indexOf('{'), strCmdRet.indexOf('}') +1 );
					logger.debug("Uninstall B1 Client Guid: " + strGuid);
					
					//kill b1 processes
					logger.info("Kill B1 process and UI process.");
					String killB1 = "cmd /c taskkill /IM \"SAP Business One.exe\" /F";
					String killB1UIAPI = "cmd /c taskkill /IM \"sapbouicom.exe\" /F";
					rrs.execute(killB1, true);
					rrs.execute(killB1UIAPI, true);
					
					//start to uninstall
					logger.info("Uninstall B1 Client Start.");
					int retCode = 0;
					if(System.getProperty("os.arch") == "x86"){
						retCode = rrs.execute(String.format(cmd_uninstall_b1_client, "", strGuid));
					}
					else{
						retCode = rrs.execute(String.format(cmd_uninstall_b1_client, " (x86)", strGuid), false, 0, 20 * 60 * 1000);
					}
					logger.debug(String.format("Uninstall B1 Client End. retCode = (%d)", retCode));
				}
				//-----------------------
				
				
				result = Result.Pass;
			} catch (Exception e) {
				logger.error(e);
				result = Result.Fail;
			}
		}
	}
}
