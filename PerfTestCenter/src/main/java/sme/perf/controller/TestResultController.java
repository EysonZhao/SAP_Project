package sme.perf.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Level;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionResultInfo;
import sme.perf.task.ParameterMissingException;
import sme.perf.task.Status;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.task.impl.common.DataImport;
import sme.perf.task.impl.common.DataImportSummary;
import sme.perf.utility.LogHelper;
import sme.perf.utility.PropertyFile;

@RestController
@RequestMapping("/TestResult")
public class TestResultController {

	Map<Long, DataImport> dataImportTaskMap = new HashMap();
	
	@RequestMapping("/Get/{executionId}")
	public List<ExecutionResultInfo> getExecutionResultInfo(@PathVariable long executionId){
		ExecutionInfoDao execDao = new ExecutionInfoDao();
		ExecutionInfo execInfo = execDao.getByID(executionId);
		if(execInfo != null){
			return execInfo.getResultList();
		}
		return null;
	}
	
	@RequestMapping("/Import/{executionId}")
	public Long startImport(@PathVariable long executionId, @RequestBody String path){		
		Thread dataImportThread = new Thread(){
			public void run(){
				DataImport dataImport = dataImportTaskMap.get(executionId);
				if(null == dataImport){
					dataImport = new DataImport();
					dataImportTaskMap.put(executionId, dataImport);
				}
				if(dataImport.getStatus() != Status.Running){
					//get scenario name
					ExecutionInfoDao execDao = new ExecutionInfoDao();
					ExecutionInfo execInfo = execDao.getByID(executionId);
					String scenarioName = execInfo.getScenario().getName();
					long isTimeAnalyse = execInfo.getIsTimeAnalyse();
					long requestId = execInfo.getTestRequestId();
					String exeName = execInfo.getName();

					//set parameters
					TaskParameterMap parameters = DataImport.getParameterTemplate();
					parameters.getParameters().get("executionId").setValue(executionId);
					parameters.getParameters().get("filePath").setValue(path);
					parameters.getParameters().get("scenario").setValue(scenarioName);
					dataImport.setParameters(parameters);
					dataImport.setTimeAnalyse(isTimeAnalyse);
					dataImport.setRequestId(requestId);
					dataImport.setRequestName(exeName);
					//start to import result
					try {
						dataImport.setLogger(LogHelper.getLogger(this.getClass().getName(), Level.DEBUG, 
								PropertyFile.getValue("LoggerPath") + executionId + File.separator + "importLog.txt"));
						dataImport.execute();
					} catch (ParameterMissingException e) {
						LogHelper.error(e);
					}
					//save the result information
					List<DataImportSummary> importSummaryList = dataImport.getDataImportSummary();
					List<ExecutionResultInfo> resultInfoList = new ArrayList<ExecutionResultInfo>();
					for(DataImportSummary summary: importSummaryList){
						if(summary.isSuccess()){
							ExecutionResultInfo resultInfo = new ExecutionResultInfo();
							resultInfo.setExecutionInfoId(executionId);
							resultInfo.setImportDate(DateTime.now());
							resultInfo.setResultCount(summary.getRecordCount());
							resultInfo.setResultSessionId("" + summary.getResultSessionId());
							resultInfo.setResultSessionName(summary.getResultSessionName());
							resultInfoList.add(resultInfo);
						}
					}
					if(resultInfoList.size() > 0){
						execInfo.setResultList(resultInfoList);
						execDao.update(execInfo);
					}
				}
			}
		};
		dataImportThread.start();
		return executionId;
	}
	
	@RequestMapping("/GetImportSummary/{executionId}")
	public List<DataImportSummary> getImportSummary(@PathVariable long executionId){
		DataImport dataImport = dataImportTaskMap.get(executionId);
		if(dataImport != null){
			return dataImport.getDataImportSummary();
		}
		return null;
	}
}
