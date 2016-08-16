package sme.perf.task.impl.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import sme.perf.entity.Host;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.PropertyFile;
import sme.perf.utility.RunRemoteSSH;

public class MSSQLDBRestore extends Task {

	private String dbServer, dbUser, dbPassword, dbName, dbBackup;
	private List<Host> dbServerHosts ;
	
	@Override
	public Result execute() throws ParameterMissingException {
		Result result = Result.Unknown;
		status = Status.Running;
		initialParameters();
		//1. write the restore DB sql to a temp file
		UUID randomUUID = UUID.randomUUID();
		String essentialPath = PropertyFile.getValue("LocalEssentialPath");
		String restoreSQLFileName = String.format("%s\\%s", essentialPath, randomUUID.toString());
		String restoreDBSql = getRestoreDBSql(dbName, dbBackup);
		try {
			PrintWriter writer = new PrintWriter(restoreSQLFileName);
			writer.print(restoreDBSql);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
			status = Status.Failed;
			result = Result.Fail;
			return result;
		}

		String localEssentialPath = String.format("%s\\%s", PropertyFile.getValue("LocalRootPath"), essentialPath); 
		String sqlFileName = String.format("%s\\%s", localEssentialPath, "RestoreDB.sql");
		
		String createEssentialFolderCmd = String.format("cmd /c if not exist \"%s\" mkdir \"%s\"", localEssentialPath, localEssentialPath);
		String copyRestoreSqlCmd = String.format("cmd /c copy /Y %s\\%s %s", 
				PropertyFile.getValue("RemoteEssential"), randomUUID.toString(),
				sqlFileName);
		String restoreDBCmd = String.format("cmd /c sqlcmd -S %s -U %s -P %s -i %s ", dbServer, dbUser, dbPassword, sqlFileName);
		
		List<RunRemoteSSH> runRemoteSSHList = new ArrayList<RunRemoteSSH>();
		for(Host host: dbServerHosts){
			try{
				RunRemoteSSH remoteSSH = new RunRemoteSSH(host.getIP(), host.getUserName(), host.getUserPassword());
				remoteSSH.setLogger(logger);
				runRemoteSSHList.add(remoteSSH);
				remoteSSH.execute(createEssentialFolderCmd);
				remoteSSH.execute(copyRestoreSqlCmd);
				remoteSSH.execute(restoreDBCmd, false, 0, 3600 * 1000 ); //set time out to be 1 hour
			}
			catch(Exception ex){
				logger.error(ex);
				result = Result.Fail;
				status = Status.Failed;
			}
		}
		//delete the file
		File restoreSQLFile = new File(restoreSQLFileName);
		restoreSQLFile.delete();
		
		status = Status.Finished;
		return result;
	}

	private void initialParameters() throws ParameterMissingException{
		this.dbServer = parameters.getValue("MSSQLDBServer").toString().trim();
		this.dbUser = parameters.getValue("MSSQLDBUser").toString().trim();
		this.dbPassword = parameters.getValue("MSSQLDBPassword").toString().trim();
		this.dbBackup = parameters.getValue("MSSQLDBBackup").toString().trim();
		this.dbName = parameters.getValue("MSSQLDBName").toString().trim();
		
		this.dbServerHosts = (List<Host>) hostParameters.getValue("MSSQLDBServer");
	}
	
	private String getRestoreDBSql(String dbName, String dbBackup) {
		String template = "-- 1. close all connection to the DB\r\n" + 
				"USE master\r\n" + 
				"DECLARE @DBName nvarchar(257)\r\n" + 
				"DECLARE @SPID int\r\n" + 
				"DECLARE @DBBackupFile nvarchar(257)\r\n" + 
				"-- 1.1 set the parameters\r\n" + 
				"SET @DBName = '$dbName'\r\n" + 
				"SET @DBBackupFile = '$dbBackup'\r\n" + 
				"-- 1.2 Delcare the cursor\r\n" + 
				"DECLARE CurrentID CURSOR FOR \r\n" + 
				"SELECT spid FROM sysprocesses WHERE dbid=db_id(@DBName)\r\n" + 
				"-- 1.3 Open the cursor\r\n" + 
				"OPEN CurrentID\r\n" + 
				"-- 1.4 get data from the cursor\r\n" + 
				"FETCH NEXT FROM CurrentID INTO @SPID\r\n" + 
				"print @spid\r\n" + 
				"WHILE @@FETCH_STATUS <>-1\r\n" + 
				"BEGIN\r\n" + 
				"-- 1.5 kill the connection \r\n" + 
				" exec('KILL '+@SPID)\r\n" + 
				" FETCH NEXT FROM  CurrentID INTO @SPID\r\n" + 
				"END\r\n" + 
				"-- 1.6 close and deallocate the cursor\r\n" + 
				"CLOSE CurrentID\r\n" + 
				"DEALLOCATE CurrentID\r\n" + 
				"-- 2. restore db\r\n" + 
				"-- 2.1 put the db into single_user mode\r\n" + 
				"exec('alter database ' + @DBName + ' set single_user with rollback immediate')\r\n" + 
				"-- 2.2 restore db with specific file\r\n" + 
				"RESTORE DATABASE @DBName  FROM DISK=@DBBackupFile WITH REPLACE \r\n" + 
				"-- 2.3 put the db into multiple user mode\r\n" + 
				"exec('alter database ' + @DBName + ' set multi_user')";
		return template.replace("$dbName", dbName).replace("$dbBackup", dbBackup);
	}

	@Override
	public String getDescription() {
		return getDescriptionTemplate();
	}
	
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		parameters.put("MSSQLDBServer", new TaskParameterEntry("10.58.136.98","The DB Server."));
		parameters.put("MSSQLDBUser", new TaskParameterEntry("sa","The DB User."));
		parameters.put("MSSQLDBPassword", new TaskParameterEntry("SAPB1Admin","The DB user's password."));
		parameters.put("MSSQLDBBackup", new TaskParameterEntry("C:\\dbbackup\\9.2_PL02_1453208\\performance_db_9.2_PL02_1453208.bak","The full file name of the db backup"));
		parameters.put("MSSQLDBName", new TaskParameterEntry("performance_db","The DB Name."));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> clientHostList = new ArrayList<Host>();
		clientHostList.add(new Host("CNPVG50817039", "10.58.136.98", "administrator",
				"Initial0", "B1 Client Host"));
		hostsParameter.put("MSSQLDBServer", new TaskParameterEntry(clientHostList,
				"The hosts of MSSQL DB Server."));
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return "To restore a DB of MSSQL.";
	}
}
