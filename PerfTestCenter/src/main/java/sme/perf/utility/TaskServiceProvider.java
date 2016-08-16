package sme.perf.utility;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import sme.perf.task.PackageClassesInfo;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterMap;
import sme.perf.utility.LogHelper;

public class TaskServiceProvider {
	
	private static final String GET_HOSTS_TEMPLATE = "getHostsTemplate";

	private static final String GET_DESCRIPTION_TEMPLATE = "getDescriptionTemplate";

	private static final String GET_PARAMETER_TEMPLATE = "getParameterTemplate";

	private static Map<String, Map<String, Class<? extends Task>>> pkgClsCacheMap = new HashMap<String, Map<String, Class<? extends Task>>>();
	
	public static Map<String, Class<? extends Task>> getTasksClass(String pkg) {
		Map<String, Class<? extends Task>> pkgClsInfo = pkgClsCacheMap.get(pkg);
		if(pkgClsInfo == null){
			Map<String, Class<? extends Task>>taskClassMap = new HashMap<String, Class<? extends Task>>();	
			Reflections reflections = new Reflections(pkg);
			// a task is a class implements 'Task' interface.
			Set<Class<? extends Task>> subTypes = reflections.getSubTypesOf(Task.class);
			Iterator<Class<? extends Task>> it = subTypes.iterator();
			while(it.hasNext()){
				Class<? extends Task> cls = it.next();
			// a task must include 'getParameterTemplate' and 'getDescriptionTemplate' method
				try{
					if(cls.getMethod(GET_PARAMETER_TEMPLATE) != null 
//							&& cls.getMethod(GET_DESCRIPTION_TEMPLATE) != null
							&& cls.getMethod(GET_HOSTS_TEMPLATE) != null){
						taskClassMap.put(cls.getName(), cls);
					}
				}
				catch(Exception ex){
					LogHelper.error(ex.getMessage());
				}
			}
			pkgClsCacheMap.put(pkg, taskClassMap);
			return taskClassMap;
		}
		else{
			return pkgClsCacheMap.get(pkg);
		}
	}
	
	public static Task createTaskInstance(String className, String pkg) throws InstantiationException, IllegalAccessException {
		Map<String, Class<? extends Task>> taskClassMap = getTasksClass(pkg);
		if(taskClassMap.get(className) == null){
			throw new InstantiationException(className + " is not found.");
		}
		return taskClassMap.get(className).newInstance();
	}
	
	public static String getTaskDescription(String className, String pkg) throws Exception{
		return (String)invokeClassMethod(GET_DESCRIPTION_TEMPLATE, className, pkg);
	}
	
	public static TaskParameterMap getTaskParameter(String className, String pkg){
		return (TaskParameterMap)invokeClassMethod(GET_PARAMETER_TEMPLATE, className, pkg);
	}
	
	public static TaskParameterMap getTaskHostParameter(String className, String pkg){
		return (TaskParameterMap) invokeClassMethod(GET_HOSTS_TEMPLATE, className, pkg);
	}
	
	private static Object invokeClassMethod(String method, String className, String pkg) {
		Object retObj = null;
		Map<String, Class<? extends Task>> pkgClsInfo = getTasksClass(pkg);
		Class cls = pkgClsInfo.get(className);
		try {
			retObj = cls.getMethod(method).invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			LogHelper.error(e.getMessage());
		}
		return retObj;
	}
	
	public static List<String> getAllPackageList(){
		List<String> pkgList = new ArrayList<String>();
		pkgList.add("sme.perf.task.impl.anywhere");
		pkgList.add("sme.perf.task.impl.common");
		pkgList.add("sme.perf.task.impl.op");
		return pkgList;
	}
	
	public static List<PackageClassesInfo> getAllPackageClassesInfo(){
		List<PackageClassesInfo> pkgClsInfoList = new ArrayList<PackageClassesInfo>();
		for(String pkg: getAllPackageList()){
			PackageClassesInfo pkgClsInfo = new PackageClassesInfo(pkg, new ArrayList<NameValueHolder>());
			for(Map.Entry<String, Class<? extends Task>> it : getTasksClass(pkg).entrySet()){
				pkgClsInfo.getClasses().add(NameValueHolder.ToValueHolder(it.getValue().getName()));
			}
			pkgClsInfo.getClasses().sort(new NameValueHolderComparator());
			pkgClsInfoList.add(pkgClsInfo);
		}
		
		return pkgClsInfoList;
	}
}
