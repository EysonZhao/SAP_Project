package sme.perf.task.impl.op.b1920;

import java.io.File;
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

public class CollectTestResult extends Task{

	List<Host> clientList;
	String resultName;
	String testSetName;
	int totalUser;
	long executionId;
	
	@Override
	public Result execute() throws ParameterMissingException {
		status = Status.Running;
		initialParameters();
		
		//copy results to central server
		//create folder o central server
		//move the results to backup folder
		DateTime now = DateTime.now();
		
		String fullResultName = String.format("%dThread_%s_%s"
				, totalUser
				, resultName 
				, DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss").print(now));
		String serverResultFolder = String.format("%s\\%d\\%s\\transaction",PropertyFile.getValue("TestResultRepository")
				,executionId
				,fullResultName);
		File resultFolder = new File(serverResultFolder);
		
		while(resultFolder.exists()){
			now.plusSeconds(1);
			fullResultName = String.format("%dThread_%s_%s"
					, totalUser
					, resultName 
					, DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss").print(now));
			
			serverResultFolder = String.format("%s\\%d\\%s\\transaction",PropertyFile.getValue("TestResultRepository")
					,executionId
					,fullResultName);
			resultFolder = new File(serverResultFolder);
		}
		resultFolder.mkdirs();
		
		List<RunRemoteSSH> remoteSSHList = new ArrayList<RunRemoteSSH>();
		for(Host host: clientList){
			RunRemoteSSH remoteSSH = new RunRemoteSSH(host.getIP(), host.getUserName(), host.getUserPassword());
			remoteSSH.setLogger(logger);
			remoteSSHList.add(remoteSSH);
			String localResultFolder = String.format("%s\\%s\\Result", PropertyFile.getValue("LocalTestCaseRepository"), testSetName);
			String remoteResultFolder = String.format("%s\\%d\\%s\\transaction", PropertyFile.getValue("RemoteTestResultRepository") ,executionId, fullResultName);
			String copyResultToRepo = String.format("cmd /c xcopy /y /h /q /i  \"%s\\*.txt\" \"%s\"", localResultFolder, remoteResultFolder);
			String localResultBackupFolder = String.format("%s\\%s", localResultFolder, resultName);
			String createResultBackupFolder = String.format("cmd /c if not exist \"%s\" mkdir \"%s\"", localResultBackupFolder, localResultBackupFolder);
			String moveResultToLocalBackup = String.format("cmd /c move /Y \"%s\\*.txt\" \"%s\\%s\"", localResultFolder, localResultFolder, resultName);
			try {
				remoteSSH.execute(copyResultToRepo);
				remoteSSH.execute(createResultBackupFolder);
				remoteSSH.execute(moveResultToLocalBackup);
			} catch (Exception e) {
				status = Status.Failed;
				logger.error(e);
				return Result.Fail;
			}
		}
		status = Status.Finished;
		return Result.Pass;
	}

	@Override
	public String getDescription() {
		return getDescriptionTemplate();
	}

	
	private void initialParameters( ) throws ParameterMissingException{
		resultName = parameters.getValue("ResultName").toString();
		testSetName = parameters.getValue("TestSetName").toString();
		totalUser = Integer.parseInt(parameters.getValue("TotalUserNumber").toString());
		executionId = Integer.parseInt(parameters.getValue("executionId").toString());
		clientList = (List<Host>) hostParameters.getValue("TestClientHosts");
	}
	
	
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		parameters.put("ResultName", new TaskParameterEntry("","A sub folder with the name will be created under 'Result' folder."));
		parameters.put("TestSetName", new TaskParameterEntry("","The folder name in the test case repository."));
		parameters.put("TotalUserNumber", new TaskParameterEntry("1","How many concurrent will be."));
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
		return "Collect the results from clients";
	}
}
