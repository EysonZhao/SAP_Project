package sme.perf.task;

public class TaskParameterEntry{
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	///Current support 2 type objects: String and List<Host>
	///Implemented in TaskParameterMapJsonDeserializer & TaskParameterMapJsonSerializer
	Object value;
	String description;
	
	public TaskParameterEntry(Object value, String description){
		this.value = value;
		this.description = description;
	}

}
