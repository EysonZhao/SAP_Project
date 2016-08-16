package sme.perf.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.task.PackageClassesInfo;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.LogHelper;
import sme.perf.utility.TaskServiceProvider;

@RestController
@RequestMapping("/Task")
public class TaskController {

	@RequestMapping("/ListTaskLibrary")
	public @ResponseBody List<PackageClassesInfo> listTaskLibrary(){
		return TaskServiceProvider.getAllPackageClassesInfo();
	}
	
	@RequestMapping("/GetTasksHostParameters")
	public @ResponseBody TaskParameterMap getTasksHostParameters(@RequestBody List<String> taskNameList){
		TaskParameterMap retTaskHostParam = new TaskParameterMap();
		for(String taskName: taskNameList){
			String pkgName = taskName.substring(0, taskName.lastIndexOf("."));
			TaskParameterMap taskHostParam = TaskServiceProvider.getTaskHostParameter(taskName, pkgName);
			if(null != taskHostParam){
				retTaskHostParam = retTaskHostParam.merge(taskHostParam);	
			}
		}
		return retTaskHostParam;
	}
	
	@RequestMapping("/GetTasksParameters")
	public @ResponseBody TaskParameterMap getTasksParameters(@RequestBody List<String> taskNameList){
		TaskParameterMap retTaskParamMap = new TaskParameterMap();
		for(String taskName: taskNameList){
			String pkgName = taskName.substring(0, taskName.lastIndexOf("."));
			TaskParameterMap taskParamMap = TaskServiceProvider.getTaskParameter(taskName, pkgName);
			if(null != taskParamMap){
				retTaskParamMap = retTaskParamMap.merge(taskParamMap);
			}
		}
		return retTaskParamMap;
	}
	
}
