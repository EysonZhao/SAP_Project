package sme.perf.utility;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

///it's desigend for front end data binding. very value is a object {name: "xxxx"}
public class NameValueHolder {
	private String name;

	public NameValueHolder() {};
	public NameValueHolder(String value){
		this.name = value;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static String ToString(NameValueHolder valueHolder){
		if(null != valueHolder){
			return valueHolder.getName();
		}
		return "";
	}
	
	public static List<String> ToString(List<NameValueHolder> valueHolderList){
		List<String> retList = new ArrayList<String>();
		if(null != valueHolderList){
			for(NameValueHolder valueHolder: valueHolderList ){
				if(valueHolder != null){
					retList.add(valueHolder.getName());
				}
			}
		}
		return retList;
	}
	
	public static NameValueHolder ToValueHolder(String value){
		return new NameValueHolder(value);
	}
	
	public static List<NameValueHolder> ToValueHolder(List<String> valueList){
		List<NameValueHolder> holderList = new ArrayList<NameValueHolder>();
		if(null != valueList){
			for(String str: valueList){
				holderList.add(new NameValueHolder(str));
			}
		}
		return holderList;
	}
}

class NameValueHolderComparator implements Comparator<NameValueHolder>{

	@Override
	public int compare(NameValueHolder o1, NameValueHolder o2) {
		if(null == o2)
			return 1;
		if(null == o1)
			return -1;
		return o1.getName().compareTo(o2.getName());
	}
}