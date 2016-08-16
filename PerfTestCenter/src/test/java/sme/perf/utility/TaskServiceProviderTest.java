package sme.perf.utility;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import sme.perf.task.ITask;
import sme.perf.task.TaskParameterMap;

public class TaskServiceProviderTest {

//	@Test
//	public void testGetAllTasksClass() {
//		try{
//			@SuppressWarnings("unused")
//			Map<String, Class<? extends Task>> taskMap = TaskServiceProvider.getAllTasksClass();
//		}
//		catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}
//	
//	@Test
//	public void testGetAllTasksDecription(){
//		try{
//			Map<String, String> allTasksDesc = TaskServiceProvider.getAllTasksDescription();
//			for(Map.Entry<String,String> it : allTasksDesc.entrySet()){
//				LogHelper.info(it.getKey() + ": " + it.getValue());
//			}
//		}
//		catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}
	
//	@Test
//	public void testGetAllTasksParameter(){
//		try{
//			Map<String, TaskParameterMap> allTasksParam = TaskServiceProvider.getAllTasksParameter();
//			for(Map.Entry<String,TaskParameterMap> it : allTasksParam.entrySet()){
//				LogHelper.info(it.getKey() + ": " + it.getValue().toString());
//			}
//		}
//		catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}

	@Test
	public void testCreateTaskInstance(){
		try{
			ITask installB1Client = TaskServiceProvider.createTaskInstance("sme.perf.task.impl.InstallB1Client", "sme.perf.task.impl");
			installB1Client.execute();
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
}
