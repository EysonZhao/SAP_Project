package sme.perf.task.impl.common;

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

public class MSSQLDBBackup extends Task{

	private String dbServer, dbUser, dbPassword, dbName, dbBackup;
	private List<Host> dbServerHosts ;
	
	@Override
	public Result execute() throws ParameterMissingException {
		status = Status.Running;
		
		initialParameters();
		
		String backupSql = String.format("BACKUP DATABASE [%s] TO  DISK = N'%s' WITH NOFORMAT, NOINIT, SKIP, NOREWIND, NOUNLOAD, COMPRESSION,  STATS = 10",
				dbName, dbBackup);
		String backupDBCmd = String.format("cmd /c sqlcmd -S %s -U %s -P %s -Q \"%s\"", dbServer, dbUser, dbPassword, backupSql);
		for(Host host: dbServerHosts){
			RunRemoteSSH remoteSSH = new RunRemoteSSH(host.getIP(), host.getUserName(), host.getUserPassword());
			remoteSSH.setLogger(logger);
			try {
				remoteSSH.execute(backupDBCmd, false, 0, 3600 * 1000);
			} catch (Exception e) {
				logger.error(e);
				status = Status.Failed;
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
		this.dbServer = parameters.getValue("MSSQLDBServer").toString().trim();
		this.dbUser = parameters.getValue("MSSQLDBUser").toString().trim();
		this.dbPassword = parameters.getValue("MSSQLDBPassword").toString().trim();
		this.dbBackup = parameters.getValue("MSSQLDBBackup").toString().trim();
		this.dbName = parameters.getValue("MSSQLDBName").toString().trim();
		
		this.dbServerHosts = (List<Host>) hostParameters.getValue("MSSQLDBServer");
	}
	
	
	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
		parameters.put("MSSQLDBServer", new TaskParameterEntry("10.58.136.98","The DB Server."));
		parameters.put("MSSQLDBUser", new TaskParameterEntry("sa","The DB User."));
		parameters.put("MSSQLDBPassword", new TaskParameterEntry("SAPB1Admin","The DB user's password."));
		parameters.put("MSSQLDBBackup", new TaskParameterEntry("C:\\dbbackup\\performance_db_9.2_PL02_000000.bak","The full file name of the db backup"));
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
		return "To Backup a MSSQL DB.";
	}
}
