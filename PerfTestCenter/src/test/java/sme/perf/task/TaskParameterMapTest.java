package sme.perf.task;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskParameterMapTest {

//	@Before
//	public void setUp() throws Exception {
//	}
//
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	@Test
//	public void testMerge() {
//		try{
//			Map[] hashMapArray = new HashMap[2];
//			for(int i=0 ; i< 4 ; i++){
//				if(hashMapArray[i%2] == null){
//					hashMapArray[i%2] = new HashMap<String, TaskParameterEntry>();
//				}
//				hashMapArray[i%2].put("key" + i, new TaskParameterEntry("value" + i, "desc" + i));
//			}
//			hashMapArray[0].put("key1", new TaskParameterEntry("value1", "desc1"));
//			
//			TaskParameterMap[] paramMapArray = new TaskParameterMap[2];
//			paramMapArray[0] = new TaskParameterMap();
//			paramMapArray[1] = new TaskParameterMap();
//			paramMapArray[0].setParameters(hashMapArray[0]);
//			paramMapArray[1].setParameters(hashMapArray[1]);
//			
//			TaskParameterMap map2 = paramMapArray[0].merge(paramMapArray[1]);
//			assertTrue(map2.getParameters().entrySet().size() == 4);
//		}
//		catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}
//
//	@Test
//	public void testCopyValueTo(){
//		try{
//			Map[] hashMapArray = new HashMap[2];
//			hashMapArray[0] = new HashMap<String, TaskParameterEntry>();
//			for(int i=0 ; i<5 ; i++){
//				hashMapArray[0].put("key"+i, new TaskParameterEntry("value" + i, "desc" + i));
//			}
//			
//			hashMapArray[1] = new HashMap<String, TaskParameterEntry>();
//			for(int i=0 ; i<2 ; i++){
//				hashMapArray[1].put("key"+i, new TaskParameterEntry(null, "desc"));
//			}
//			
//			TaskParameterMap[] taskParamMapArray = new TaskParameterMap[2];
//			for(int i=0 ; i<2 ; i++){
//				taskParamMapArray[i] = new TaskParameterMap();
//				taskParamMapArray[i].setParameters(hashMapArray[i]);
//			}
//
//			for(int i=0 ; i<2 ; i++){
//				assertNull(taskParamMapArray[1].getParameters().get("key"+i).getValue());
//				assertTrue(taskParamMapArray[1].getParameters().get("key"+i).getDescription().equals("desc"));
//			}
//						
//			taskParamMapArray[0].copyValueTo(taskParamMapArray[1]);
//			
//			for(int i=0 ; i<2 ; i++){
//				assertTrue(taskParamMapArray[1].getParameters().get("key"+i).getValue().equals("value"+i));
//				assertTrue(taskParamMapArray[1].getParameters().get("key"+i).getDescription().equals("desc"));
//			}
//		}
//		catch(Exception ex){
//			fail(ex.getMessage());
//		}
//	}
//	
//	@Test(expected=ParameterMissingException.class)
//	public void testGetValueNullDefault() throws ParameterMissingException{
//		Map[] hashMapArray = new HashMap[2];
//		hashMapArray[0] = new HashMap<String, TaskParameterEntry>();
//		Map<String, TaskParameterEntry> parameterHashMap = new HashMap<String, TaskParameterEntry>();
//		
//		parameterHashMap.put("key1", new TaskParameterEntry("value1", "desc1"));
//		
//		TaskParameterMap parameterMap = new TaskParameterMap();
//		parameterMap.setParameters(parameterHashMap);
//		parameterMap.getValue("key2");
//	}
//	
//	@Test(expected=ParameterMissingException.class)
//	public void testGetValueNull() throws ParameterMissingException{
//		Map[] hashMapArray = new HashMap[2];
//		hashMapArray[0] = new HashMap<String, TaskParameterEntry>();
//		Map<String, TaskParameterEntry> parameterHashMap = new HashMap<String, TaskParameterEntry>();
//		
//		parameterHashMap.put("key1", new TaskParameterEntry("value1", "desc1"));
//		
//		TaskParameterMap parameterMap = new TaskParameterMap();
//		parameterMap.setParameters(parameterHashMap);
//		parameterMap.getValue("key2", false);
//	}
//	
//	
//	@Test
//	public void testGetValue(){
//		Map[] hashMapArray = new HashMap[2];
//		hashMapArray[0] = new HashMap<String, TaskParameterEntry>();
//		Map<String, TaskParameterEntry> parameterHashMap = new HashMap<String, TaskParameterEntry>();
//		
//		parameterHashMap.put("key1", new TaskParameterEntry("value1", "desc1"));
//		
//		TaskParameterMap parameterMap = new TaskParameterMap();
//		parameterMap.setParameters(parameterHashMap);
//		try {
//			assertTrue(parameterMap.getValue("key1").toString().equals("value1"));
//			assertTrue(parameterMap.getValue("key2", true) == null);
//		} catch (ParameterMissingException e) {
//			fail("Default should support nullable parameter");
//		}
//		
//
//	}

}
