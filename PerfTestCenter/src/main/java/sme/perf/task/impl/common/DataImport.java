package sme.perf.task.impl.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.joda.time.DateTime;

import sme.perf.dao.GenericDao;
import sme.perf.dao.GenericRawDataDao;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.result.entity.ImportedSessionIds;
import sme.perf.result.entity.ResultSession;
import sme.perf.result.impl.*;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Result;
import sme.perf.task.Status;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.PropertyFile;

public class DataImport extends Task {

	private TaskParameterMap parameters;
	private TaskParameterMap hostParameters;
	private Status status=Status.New;
	private static final String IMPORTED_LIST = "Imported.list";
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenterResult";
	private static EntityManagerFactory factory = null;
	private List<DataImportSummary> dataImportSummrayList= new ArrayList<DataImportSummary>();
	private long globalRecordCount;
	private boolean globalIsSuccess;
	private long isTimeAnalyse;
	private long requestId;
	private String executionName;
	
	public void setTimeAnalyse(long isTimeAnalyse) {
		this.isTimeAnalyse = isTimeAnalyse;
	}
	public void setRequestId(long requestId){
		this.requestId=requestId;
	}
	public void setRequestName(String executionName){
		this.executionName=executionName;
	}
	

	@Override
	public Result execute() throws ParameterMissingException {
		this.status=Status.Running;
		
		long executionId=-1L;
		if(parameters.getValue("executionId",true)!=null){
			executionId=Long.parseLong(parameters.getValue("executionId").toString());
		}
		
		ExecutionInfo exeinfo = (new ExecutionInfoDao()).getByID(executionId);
		this.requestId = exeinfo.getTestRequestId();
		this.executionName = exeinfo.getName();
		this.isTimeAnalyse = exeinfo.getIsTimeAnalyse();
		
		String filePath = parameters.getValue("filePath").toString();
		
		//joybean 2016-3-24 provide a default path
		if (filePath.isEmpty()){
			filePath = String.format("%s\\%d", PropertyFile.getValue("TestResultRepository"), executionId);
		}
		
		String scenario="Undefined";
		if(parameters.getValue("scenario",true)!=null){
			scenario=parameters.getValue("scenario").toString();
		}
		
		List<File> subFolderList=getSubFolderList(filePath);
		getInitializeDataImportSummrayList(subFolderList);
		
		logger.info("Start to Import Data.");
		try {
			for (File SubFolderFile:subFolderList)
			{
				String SubFolder=SubFolderFile.getPath();
				String sessionName = SubFolderFile.getName();
				int userNumber = Integer.parseInt(sessionName.substring(0,sessionName.indexOf("Thread_")));
				globalIsSuccess=true;
				globalRecordCount=0L;
				ResultSession newResultSession = createNewResultSession(sessionName,
						userNumber,executionId,executionName,requestId,scenario,isTimeAnalyse);
				
				List<String> parserList=new ArrayList<String>();
				parserList.add("io");
				parserList.add("top");
				parserList.add("transaction");
				parserList.add("jmeter");
				parserList.add("dockerstats");
				parserList.add("machine");	
				
				for (String parser:parserList){
					importFolderByType(parser, SubFolder, newResultSession);
					if(!globalIsSuccess){
						break;
					}
				}
				
				DataImportSummary dis=dataImportSummrayList.get(subFolderList.indexOf(SubFolderFile));
				dis.setResultSessionId(newResultSession.getId());
				dis.setResultSessionName(sessionName);
				dis.setSuccess(globalIsSuccess);
				dis.setRecordCount(globalRecordCount);
				logger.info(String.format("Data Import Succeed. retCode=%d", 0));
			}
			
			logger.info("Finish to Import Data.");
			this.status=Status.Finished;
			return Result.Pass;
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.status=Status.Failed;
			return Result.Fail;
		}
	}

	public List<DataImportSummary> getDataImportSummary()
	{
		return dataImportSummrayList;
	}
	
	private void getInitializeDataImportSummrayList(List<File> folderList){
		for (File SubFolderFile:folderList){
			String sessionName = SubFolderFile.getName();
			DataImportSummary dis=new DataImportSummary();
			dis.setResultSessionId(-1L);
			dis.setResultSessionName(sessionName);
			dis.setSuccess(false);
			dis.setRecordCount(0);
			dataImportSummrayList.add(dis);
		}	
	}

	@Override
	public void setParameters(TaskParameterMap parameters) {
		this.parameters = parameters;
	}

	@Override
	public TaskParameterMap getParameters() {
		if (null == this.parameters) {
			this.parameters = DataImport.getParameterTemplate();
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
			this.hostParameters = DataImport.getHostsTemplate();
		}
		return hostParameters;
	}

	@Override
	public String getDescription() {
		return "Task for Data Import.";
	}

	public static TaskParameterMap getParameterTemplate() {		
		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();	
		//Common
		parameters.put("filePath", new TaskParameterEntry("","Full File Path. If it's not set, the default folder (TestResultRepoistory\\execId) will be provided."));
		parameters.put("executionId", new TaskParameterEntry("","Execution Id"));
		parameters.put("scenario", new TaskParameterEntry("OCCSizing","Senario Name"));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		return template;
	}

	public static TaskParameterMap getHostsTemplate() {
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(hostsParameter);
		return template;
	}

	public static String getDescriptionTemplate() {
		return new DataImport().getDescription();
	}
	
	private List<File> getSubFolderList(String filePath) {
		List<File> subFolderList=new ArrayList<File>();
		File f = new File(filePath);
		if (f.isDirectory()) {
			File[] fList = f.listFiles();
			for (int j = 0; j < fList.length; j++) {
				if (fList[j].isDirectory()) {
					subFolderList.add(fList[j]);
				}
			}
		}
		return subFolderList;
	}
	
	public static void addNewFile2ImportedList(String topFolder, String newImportedFile)
			throws IOException {
		String importListFile = null;
		if (!topFolder.trim().endsWith(File.separator)) {
			importListFile = topFolder + File.separator + IMPORTED_LIST;
		} else {
			importListFile = topFolder + IMPORTED_LIST;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(
				importListFile, true));
		writer.append(newImportedFile + "\n");
		writer.close();
	}

	public static boolean isFileHasBeenImported(String topFolder, String fileName)
			throws IOException {
		String importListFile = null;
		if (!topFolder.trim().endsWith(File.separator)) {
			importListFile = topFolder + File.separator + IMPORTED_LIST;
		} else {
			importListFile = topFolder + IMPORTED_LIST;
		}
		File f = new File(importListFile);
		if (f.exists() == false) {
			f.createNewFile();
		}

		BufferedReader reader = new BufferedReader(new FileReader(
				importListFile));

		String newLine = null;
		while ((newLine = reader.readLine()) != null) {
			if (newLine.trim().toLowerCase()
					.equals(fileName.trim().toLowerCase())) {
				reader.close();
				return true;
			}
		}
		reader.close();
		return false;
	}

	class ImportFileThread extends Thread{
		String type;
		String fileName;
		ResultSession newResultSession;
		String filterString;
		
		public ImportFileThread(String type, String fileName, ResultSession newResultSession, String filterString){
			this.type = type;
			this.fileName = fileName;
			this.newResultSession = newResultSession;
			this.filterString = filterString;
		}
		
		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public void run(){
			try {
				List records = null;
				switch (type.toLowerCase().trim()) {
				case "io":
						records = new IOParser(newResultSession, logger).parse(fileName);
						break;
					case "top":
						records = new TopParser(
								newResultSession, logger, filterString).parse(fileName);
	//					ImportDataToDB(new TopParser(
	//							newResultSession, logger, filterString).parse(fileName));
						break;
					case "transaction":
						records = new TransactionResponseTimeParser(
								newResultSession, logger).parse(fileName);
	//					ImportDataToDB(new TransactionResponseTimeParser(
	//									newResultSession, logger).parse(fileName));
						break;
					case "jmeter":
						records = new JmeterLogParser(
								newResultSession, logger, filterString).parse(fileName);
	//					ImportDataToDB(new JmeterLogParser(
	//							newResultSession, logger, filterString).parse(fileName));
						break;
					case "dockerstats":
						records = new DockerStatsParser(
								newResultSession, logger).parse(fileName);
	//					ImportDataToDB(new DockerStatsParser(
	//							newResultSession, logger).parse(fileName));
						break;
					case "machine":
						records = new MachineParser(
								newResultSession, logger).parse(fileName);
	//					ImportDataToDB(new MachineParser(
	//							newResultSession, logger).parse(fileName));
						break;
				}
				if(records != null){
					ImportDataToDB(records);
				}
			} catch (Exception e) {
				logger.error(e);
			}
			logger.info(String.format(
					"Imported %s from File: %s with filter :%s", type,
					fileName, filterString));
		}
	}
	
	public void importFolderByType(String type, String topFolder,
			ResultSession newResultSession) throws Exception {
		String folder;
		if (topFolder.trim().endsWith(File.separator)) {
			folder = topFolder + type;
		} else {
			folder = topFolder + File.separator + type;
		}
		String filterFileName = folder + File.separator + "filter.regex";
		String filterString = getFilterString(filterFileName);
		File f = new File(folder);
		File[] files = f.listFiles();
		if (null == files || files.length <= 0)
			return;
		try{
			List<ImportFileThread> importFileThreadList = new ArrayList<ImportFileThread>();
			for (File file : files) {
				String fileName = file.toString();
				if (fileName.toLowerCase().trim().contains("filter.regex"))
					continue;

				if (isFileHasBeenImported(topFolder, fileName)) {
					logger.info(String
							.format("file %s has been imported before. it will be ignored.",
									fileName));
					continue;
				}
				importFileThreadList.add(new ImportFileThread(type.toLowerCase().trim(), 
						fileName, newResultSession, filterString));
			}
			int i = 0, j=0;
			for(i=0 ; i<importFileThreadList.size() ; i++){
				importFileThreadList.get(i).start();
				importFileThreadList.get(i).join();
				addNewFile2ImportedList(topFolder, importFileThreadList.get(i).getFileName());
//				if(i % 5 == 4){
//					for(j=i-4 ; j<=i ; j++){
//						importFileThreadList.get(j).join();
//						addNewFile2ImportedList(topFolder, importFileThreadList.get(j).getFileName());
//					}
//				}
			}
//			for(i=j ; i<importFileThreadList.size() ; i++){
//				importFileThreadList.get(i).join();	
//				addNewFile2ImportedList(topFolder, importFileThreadList.get(j).getFileName());
//			}
			
			System.err.println("Insert session_id="+newResultSession.getId());
//			GenericRawDataDao<ImportedSessionIds> dao = new GenericRawDataDao<ImportedSessionIds>(ImportedSessionIds.class);
//			ImportedSessionIds im = new ImportedSessionIds();
//			im.setResultSession_Id(newResultSession.getId());
//			dao.add(im);
			globalIsSuccess=true;
		}catch (Exception ex) {
			logger.error(ex);
			globalIsSuccess=false;
		}
	}

	public static String getFilterString(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}

	public ResultSession createNewResultSession(String sessionName,int userNumber, 
			long executionId, String executionName, long requestId, String scenario ,String branch, String buildInfo, long isTimeAnalyse) {
		ResultSession newResultSession = null;
		if (null == factory) {
			factory = Persistence
					.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
		EntityManager em = factory.createEntityManager();

		Query q = em
				.createQuery("SELECT s FROM ResultSession s Where s.name = :Name");
		q.setParameter("Name", sessionName);
		if (q.getResultList().size() > 0) {
			newResultSession = (ResultSession) q.getResultList().get(0);
			String errMsg = String.format(
					"An existing session with same name %s is detected",
					sessionName);
			logger.info(errMsg);
		} else {
			newResultSession = new ResultSession();
			newResultSession.setCreateDate(DateTime.now());
			newResultSession.setName(sessionName);
			newResultSession.setUserNumber(userNumber);
			newResultSession.setScenario(scenario);
			newResultSession.setExecutionId(executionId);
			newResultSession.setRequestId(requestId);
			newResultSession.setBranch(branch);
			newResultSession.setBuildInfo(buildInfo);
			newResultSession.setIsTimeAnalyse(isTimeAnalyse);
			newResultSession.setExecutionName(executionName);
			em.getTransaction().begin();
			em.persist(newResultSession);
			em.getTransaction().commit();
			logger.info(String.format("new result session %s is created",
					sessionName));
		}
		em.close();
		return newResultSession;
	}
	
	public ResultSession createNewResultSession(String sessionName,int userNumber,
			long executionId,String executionName, long requestId,String scenario,long isTimeAnalyse) {
		return createNewResultSession(sessionName,
				userNumber,executionId,executionName,requestId,scenario,null,null,isTimeAnalyse);
	}
	
	
	
    public <T> void ImportDataToDB(List<T> dataList) {
    	int start = 0;
    	List<ImportDataThread> threadList = new ArrayList<ImportDataThread>();
    	while (start < dataList.size()) {
    		int batchSize = 15000;
			int end = start + batchSize > dataList.size() ? dataList.size() : start + batchSize;
    		ImportDataThread<T> thread = new ImportDataThread<T>(dataList, start, end);
    		//add the thread in to threadList
    		threadList.add(thread);
    		start += batchSize;
    		thread.start();
    	}
    	for(ImportDataThread<T> thread : threadList){
    		try {
				thread.join();
			} catch (InterruptedException e) {
				logger.error(e);
			}
    	}
    };
    
    class ImportDataThread<T> extends Thread{
    	List<T> dataList;
    	int startIndex, endIndex;
    	
    	public ImportDataThread(List<T> dataList, int startIndex, int endIndex){
    		this.dataList = dataList;
    		this.startIndex = startIndex;
    		this.endIndex = endIndex;
    	}
    	
    	public void run(){
            EntityManager em = factory.createEntityManager();
            logger.info(dataList.size() + " line records need to be imported.");
            globalRecordCount+=dataList.size();
            long startTm = System.currentTimeMillis();
    		
            int i = startIndex;
            em.getTransaction().begin();
    		while (i < endIndex) {
    			em.persist(dataList.get(i));
    			i++;
    		}
    		em.getTransaction().commit();
    		
    		em.close();
    		logger.info((endIndex - startIndex)
    				+ " line records have been imported. Response time (ms) = "
    				+ (System.currentTimeMillis() - startTm));
    	}
    }
}
