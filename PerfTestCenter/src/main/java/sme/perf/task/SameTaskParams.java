package sme.perf.task;

public class SameTaskParams {
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	private Object value;
	
	public SameTaskParams(String name,Object value){
		this.setName(name);
		this.setValue(value);
	}
	public SameTaskParams(String objectString){
		String[] parts= objectString.split(", ");
		String pname = parts[0].substring(6, parts[0].length());
		this.setName(pname);
		String pvalue = parts[1].substring(6, parts[1].length()-1);
		this.setValue(pvalue);
	}
}
