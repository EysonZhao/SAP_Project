package sme.perf.task;

import java.util.List;

import sme.perf.utility.NameValueHolder;

public class PackageClassesInfo {
	private String name;
	private List<NameValueHolder> classes;
	
	public PackageClassesInfo() {}
	
	public PackageClassesInfo(String name, List<NameValueHolder> classes) {
		super();
		this.name = name;
		this.classes = classes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<NameValueHolder> getClasses() {
		return classes;
	}
	public void setClasses(List<NameValueHolder> classes) {
		this.classes = classes;
	}
	
}
