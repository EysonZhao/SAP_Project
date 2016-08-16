package sme.perf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import sme.perf.execution.Execution;
import sme.perf.execution.State;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.execution.impl.RunExecution;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;

public class RunExecutionApplication {

	public static void main(String[] args) {
		if(args.length < 1)
			return;
		String execSourceFileName = args[0];
		String execResultFileName = args[1];
		try{	
			ObjectMapper objMapper = new ObjectMapper();
		    ExecutionInfo execInfo = objMapper.readValue(new File(execSourceFileName), ExecutionInfo.class);
		    
		    if(execInfo.getState() == State.New){
		    	execInfo.setState(State.Ready);
				Execution runExec = new RunExecution();
				runExec.Execute(execInfo);
				objMapper.writeValue(new File(execResultFileName), execInfo);
			}
		} catch (FileNotFoundException e) {
			LogHelper.error(e);
		} catch (IOException e) {
			LogHelper.error(e);
		}
		catch(Exception e){
			LogHelper.error(e);
		}
	}

}
