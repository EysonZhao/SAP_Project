package sme.perf.task;

import sme.perf.utility.LogHelper;

public class ParameterMissingException extends Exception {
	
	private String parameterName;
	
	public ParameterMissingException(String parameterName){
		super(String.format("Parameter: %s is missing.", parameterName));
		LogHelper.error(String.format("Parameter: %s is missing.", parameterName));
		this.parameterName = parameterName;
	}
}
