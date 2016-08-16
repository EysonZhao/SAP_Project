package sme.perf.controller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Category;
import sme.perf.entity.Scenario;
import sme.perf.execution.Execution;
import sme.perf.execution.State;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.execution.impl.RunExecution;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;
import sme.perf.utility.TaskServiceProvider;

@RestController
@RequestMapping("/Analaysis")
public class AnalysisController {

	public AnalysisController(){
		
	}
	
	@RequestMapping("/Run")
	public @ResponseBody void duplicate(@PathVariable long id){

		//TODO WHETHER NEED?
		//get Result Session ID
		//
		//
		
	}
}
