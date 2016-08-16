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
import sme.perf.utility.LogHelper;
import sme.perf.utility.RunRemoteSSH;

public class CreateDBPool extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;

	@SuppressWarnings("unchecked")
	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		
		String version = parameters.getValue("version").toString();
		int startSchemaNumber = Integer.parseInt(parameters.getValue("startSchemaNumber").toString());
		int endSchemaNumber = Integer.parseInt(parameters.getValue("endSchemaNumber").toString());
		int tenantNumber = endSchemaNumber-startSchemaNumber+1;
		String mariaDBPasswd = parameters.getValue("mariaDBPasswd").toString();
		List<Host> csmServerList = (List<Host>) hostParameters.getValue("csmServer");
		Host csmServer=csmServerList.get(0);
		
		LogHelper.info("Start to Create DB Pool.");
		try {
			for (int i = startSchemaNumber; i < startSchemaNumber + tenantNumber; i++) {
				int indexID = i + 15000;
				String createDBPoolCmd = "mysql -uroot -p"+mariaDBPasswd+" -e \"INSERT INTO CSM.COMPANYDB values ('PERFDB"
						+ i
						+ "', 'CN', (select GUID from CSM.DKEYS where id = 1), (select id from CSM.SERVICE_UNIT where status = 'ONLINE'),"
						+ indexID
						+ ")\"\nmysql -uroot -p"+mariaDBPasswd+" -e \"INSERT INTO CSM.DBSCHEMA (id,name,type,db_cluster_id,status,istrustedconnection,lifecyclestatus,substatus,version,create_time,update_time,db_schema_type) values ("
						+ indexID
						+ ",'PERFDB"
						+ i
						+ "','POOL', (select id from CSM.BASE_CLUSTER where type = 'HANADB'),'ONLINE',0,'RUNNING','Normal','"
						+ version + "','2014-09-05','2014-09-05','COMPANY')\"";
				RunRemoteSSH rrs=new RunRemoteSSH(csmServer.getIP(), csmServer.getUserName(), csmServer.getUserPassword());
				rrs.setLogger(logger);
				rrs.execute(createDBPoolCmd);
			}
			LogHelper.info("Finish to Create DB Pool.");
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			LogHelper.error(e.getMessage());
			this.status=Status.Failed;
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
			this.parameters = CreateDBPool.getParameterTemplate();
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
			this.hostParameters = CreateDBPool.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Create DB Pool.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();		
		parameters.put("version", new TaskParameterEntry("7.0.0","OCC version"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
		parameters.put("endSchemaNumber", new TaskParameterEntry("10","End Schema Number"));
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
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}
	
	public static String getDescriptionTemplate() {
		return new CreateDBPool().getDescription();
	}
}
