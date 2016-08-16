package sme.perf.task;

import java.util.HashMap;
import java.util.Map;
import sme.perf.utility.JsonHelper;
import sme.perf.utility.LogHelper;
import sme.perf.utility.TaskParameterMapJsonDeserializer;
import sme.perf.utility.TaskParameterMapJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TaskParameterMap{
	
	@JsonSerialize(using=TaskParameterMapJsonSerializer.class)
	@JsonDeserialize(using=TaskParameterMapJsonDeserializer.class)
	Map<String, TaskParameterEntry> parameters;

	public TaskParameterMap(){
		this.parameters = new HashMap<String, TaskParameterEntry>();
	}
	
	public Map<String, TaskParameterEntry> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, TaskParameterEntry> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString(){
		return new JsonHelper<TaskParameterMap>().serializeObject(this);
	}
	
	public TaskParameterMap merge(TaskParameterMap map){
		Map<String, TaskParameterEntry> newMap = new HashMap<String, TaskParameterEntry>();
		newMap.putAll(parameters);
		if(null != map){
			for(Map.Entry<String, TaskParameterEntry> entry : map.getParameters().entrySet()){
				newMap.putIfAbsent(entry.getKey(), entry.getValue());
			}
		}
		
		TaskParameterMap retMap = new TaskParameterMap();
		retMap.setParameters(newMap);
		return retMap;
	}
	
	public void copyValueTo(TaskParameterMap otherMap){
		for(Map.Entry<String, TaskParameterEntry> entry: otherMap.getParameters().entrySet()){
			if(this.parameters.containsKey(entry.getKey())){
				TaskParameterEntry thisParam = this.parameters.get(entry.getKey());
				TaskParameterEntry otherParam = entry.getValue();
				otherParam.setValue(thisParam.getValue());
			}
		}
	}
	
	public Object getValue(String parameterName) throws ParameterMissingException{
		return getValue(parameterName, false);
	}
	
	public Object getValue(String parameterName, boolean isNullable) throws ParameterMissingException{
		TaskParameterEntry parameterEntry = this.parameters.get(parameterName);
		if(null == parameterEntry){
			if(true == isNullable){
				return null;
			}
			else {
				throw new ParameterMissingException(parameterName);
			}
		}
		else{
			return parameterEntry.getValue();
		}
	}
	
//	public String getEntriesValue(String parameterName, boolean isNullable) throws ParameterMissingException{
//		
//	}
	
	public void setValue(String parameterName, Object value) throws ParameterMissingException{
		setValue(parameterName, value, false);
	}
	
	public void setValue(String parameterName, Object value, boolean isNullable) throws ParameterMissingException{
		TaskParameterEntry parameterEntry = this.parameters.get(parameterName);
		if(null == parameterEntry){
			if(false == isNullable){
				throw new ParameterMissingException(parameterName);
			}
		}
		else{
			parameterEntry.setValue(value);
		}
	}
}
