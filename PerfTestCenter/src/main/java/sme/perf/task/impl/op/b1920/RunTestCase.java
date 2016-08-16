package sme.perf.task.impl.op.b1920;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.PropertyFile;
import sme.perf.utility.RunRemoteSSH;

public class RunTestCase extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status = Status.New;
	private Result result = Result.Unknown;
	
	private int nHostInterval;
	private int nLiteRunnerInterval;
	private int nTotalUsers;
	
	private String testSetName;
	private String environmentConfiguration;
	private String testCase;
//	private String resultName;
	private String remoteTestCaseRepository;
	private String localTestCaseRepository;
	private String liteRunnerRunMode;
	
	private List<Host> clientHosts ;
	@Override
	public Result execute() throws ParameterMissingException {
		initialParameters(this.parameters);

		List<RunRemoteSSH> runRemoteSSHList = new ArrayList<RunRemoteSSH>();
		
		//copy test case to all b1 client hosts 
		//kill all LiteController & LiteRunner & B1 Client & B1 UIAPI processes
		String createFolder = String.format("cmd /c if not exist \"%s\" mkdir \"%s\"", this.localTestCaseRepository, this.localTestCaseRepository);
		String sourceFolder = String.format("%s\\%s",  this.remoteTestCaseRepository, this.testSetName).trim();
		String targetFolder = String.format("%s\\%s", this.localTestCaseRepository, this.testSetName).trim();
		String xCopy = String.format("cmd /c xcopy /y /h /q /e /i  \"%s\" \"%s\"", sourceFolder, targetFolder );
		String killLiteController = "cmd /c taskkill /IM LiteController.exe /F";
		String killLiteRunners = "cmd /c taskkill /IM LiteRunner.exe /F";
		String killB1 = "cmd /c taskkill /IM \"SAP Business One.exe\" /F";
		String killB1UIAPI = "cmd /c taskkill /IM \"sapbouicom.exe\" /F";
		
		StringBuilder copyScript = new StringBuilder("cmd /c ");
		copyScript.append(createFolder).append(" && ").append(xCopy);
		
		StringBuilder cleanProcesses = new StringBuilder("cmd /c ");
		cleanProcesses.append(killLiteController).append(" && ").append(killLiteRunners).append(" && ").append(killB1).append(" && ").append(killB1UIAPI);
		
		logger.info("Clear Processes.");
		for(Host host: clientHosts){
			RunRemoteSSH remoteSSH = new RunRemoteSSH(host.getIP(), host.getUserName(), host.getUserPassword());
			remoteSSH.setLogger(logger);
			runRemoteSSHList.add(remoteSSH);
			try {
				remoteSSH.execute(killLiteController, true);
				remoteSSH.execute(killLiteRunners, true);
				remoteSSH.execute(killB1, true);
				remoteSSH.execute(killB1UIAPI, true);
//				
//				remoteSSH.execute(createFolder);
//				remoteSSH.execute(xCopy);
			} catch (Exception e) {
				result = result.Fail;
				logger.error(e);
				return result;
			}
		}
		
		logger.info("Copy test case to all client host.");
		for(RunRemoteSSH remoteSSH: runRemoteSSHList){
			try {			
				remoteSSH.execute(createFolder);
				remoteSSH.execute(xCopy);
			} catch (Exception e) {
				result = result.Fail;
				logger.error(e);
				return result;
			}
		}
		
		
		//schedule all host to start LiteController
		int nUserPerHost = nTotalUsers / clientHosts.size();
		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss");
		logger.info("Start LiteController to run test");
		for(int i=0; i< clientHosts.size() ; i++){
			int nUser = nUserPerHost;
			if(i< nTotalUsers % clientHosts.size()){
				nUser++;
			}
			String setWorkingFolder = String.format("cd \"%s\"", targetFolder);
			DateTime now = DateTime.now();
			
			String liteControllerCmd;
			//use start command could return directly. and check liteController process later.
			liteControllerCmd = String.format("start LiteController.exe /u %d /i %d /c LiteRunner.exe /p \"%s %s %s\"", 
					nUser,
					nLiteRunnerInterval,
					liteRunnerRunMode,
					environmentConfiguration.isEmpty() ? "": String.format("/e %s", environmentConfiguration),
					testCase.isEmpty() ? "": String.format("/c %s", testCase)
//			?		resultName.isEmpty() ? "" : String.format("/o %dThread_%s_%s", nTotalUsers, resultName, dateFormat.print(now)));
//				    resultName.isEmpty() ? "" : String.format("/o %s", resultName)
				    		);
			StringBuilder cmdBuilder = new StringBuilder("cmd /c ");
			cmdBuilder.append(setWorkingFolder).append(" && ").append(liteControllerCmd);
			if(nUser > 0){
				try {
					runRemoteSSHList.get(i).execute(cmdBuilder.toString());
				} catch (Exception e) {
					result = result.Fail;
					logger.error(e);
					return result;
				}
			}
			try {
				Thread.sleep(nHostInterval * 1000);
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
		
		boolean isStillRunning = true;
		//Wait until liteController is finished.
		while(isStillRunning){
			//check for every 30 seconds
			try {
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e1) {
				logger.info(e1);
			}
			isStillRunning = false;
			String checkLiteController = "cmd /c tasklist /FI \"imageName eq LiteController.exe\"";
			for(RunRemoteSSH remoteSSH: runRemoteSSHList){
				try {
					String returnString = remoteSSH.executeWithReturnString(checkLiteController, true);
					if(returnString.toLowerCase().contains("LiteController.exe".toLowerCase())){
						isStillRunning = true;
						logger.info("Check LiteController.exe is still running.");
						break;
					}
					
					//Retry for four times all 15 seconds~Added by Yansong Aug 4
					Thread.sleep(1*1000);
					returnString = remoteSSH.executeWithReturnString(checkLiteController, true);
					logger.info("**1st** time Retry!");
					if(returnString.toLowerCase().contains("LiteController.exe".toLowerCase())){
						isStillRunning = true;
						logger.info("Check LiteController.exe is still running. First retry is confirmed!");
						break;
					}
					Thread.sleep(2*1000);
					returnString = remoteSSH.executeWithReturnString(checkLiteController, true);
					logger.info("**2nd** time Retry!");
					if(returnString.toLowerCase().contains("LiteController.exe".toLowerCase())){
						isStillRunning = true;
						logger.info("Check LiteController.exe is still running. Second retry is confirmed!");
						break;
					}
					Thread.sleep(4*1000);
					returnString = remoteSSH.executeWithReturnString(checkLiteController, true);
					logger.info("**3rd** time Retry!");
					if(returnString.toLowerCase().contains("LiteController.exe".toLowerCase())){
						isStillRunning = true;
						logger.info("Check LiteController.exe is still running. Third retry is confirmed!");
						break;
					}
					Thread.sleep(8*1000);
					returnString = remoteSSH.executeWithReturnString(checkLiteController, true);
					logger.info("Last time Retry!");
					if(returnString.toLowerCase().contains("LiteController.exe".toLowerCase())){
						isStillRunning = true;
						logger.info("Check LiteController.exe is still running. Last retry is confirmed!");
						break;
					}
				} catch (Exception e) {
					result = result.Fail;
					logger.error(e);
					return result;
				}
			}
		}
		logger.info(String.format("Run %s is finished.", this.testSetName));
		result = Result.Pass;
		return result;
	}
	
	private void initialParameters(TaskParameterMap parameters) throws ParameterMissingException{
		String hostStartInterval = parameters.getValue("HostStartInterval").toString();
		nHostInterval = hostStartInterval.trim().isEmpty() ? 10 : Integer.parseInt(hostStartInterval);
		
		String liteRunnerInterval = parameters.getValue("LiteRunnerStartInterval").toString();
		nLiteRunnerInterval = liteRunnerInterval.trim().isEmpty() ? 10 : Integer.parseInt(liteRunnerInterval);
		
		String totalUserNum = parameters.getValue("TotalUserNumber").toString();
		nTotalUsers = totalUserNum.trim().isEmpty() ? 1 : Integer.parseInt(totalUserNum);
		
		liteRunnerRunMode = parameters.getValue("LiteRunnerRunMode").toString();
		testSetName = parameters.getValue("TestSetName").toString();
		environmentConfiguration = parameters.getValue("EnvironmentConfiguration").toString();
		testCase = parameters.getValue("TestCase").toString();
//		resultName = parameters.getValue("ResultName").toString();
		remoteTestCaseRepository = PropertyFile.getValue("RemoteTestCaseRepository");
		localTestCaseRepository = PropertyFile.getValue("LocalTestCaseRepository");
		
		clientHosts = (List<Host>) hostParameters.getValue("TestClientHosts");
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
		this.hostParameters = hostList;
	}

	@Override
	public TaskParameterMap getHosts() {
		return this.hostParameters;
	}

	@Override
	public String getDescription() {
		return getDescriptionTemplate();
	}
	
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		
		parameters.put("HostStartInterval", new TaskParameterEntry("10","The test will be started from each host. The interval (seconds) of start test between each Host."));
		parameters.put("LiteRunnerStartInterval", new TaskParameterEntry("15","The interval (seconds) of start a LiteRunner in each Host."));
		parameters.put("LiteRunnerRunMode", new TaskParameterEntry("/t 5","Specific the run mode for LiteRunner. '/d xxxx' -- run by duration in seconds; '/t xxx' -- run by iteration counts."));
		parameters.put("TestSetName", new TaskParameterEntry("","The folder name in the test case repository."));
		parameters.put("EnvironmentConfiguration", new TaskParameterEntry("EnvironmentConfiguration.xml","The xml file of environment configuration. The default value is EnvironmentConfiguration.xml."));
		parameters.put("TestCase", new TaskParameterEntry("","The xml file of test case. Default value is set in EnvironmentConfiguration.xml"));
//		parameters.put("ResultName", new TaskParameterEntry("","A sub folder with the name will be created under 'Result' folder."));
		parameters.put("TotalUserNumber", new TaskParameterEntry("1","How many concurrent will be."));
//		parameters.put("RemoteTestCaseRepository", new TaskParameterEntry("\\\\CNPVG50859391\\TestCaseRepository","The shared folder store all test cases."));
//		parameters.put("LocalTestCaseRepository", new TaskParameterEntry("C:\\PerfTestCenter\\TestCaseRepository","The local folder to store test case."));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();
		clientHostList.add(new Host("CNPVG50819354", "10.58.136.41", "administrator",
				"Initial0", "B1 Client Host"));
		hostsParameter.put("TestClientHosts", new TaskParameterEntry(clientHostList,
				"The hosts will be used in this test."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return "Run test case via call LiteController.exe & LiteRunner.exe";
	}
}
