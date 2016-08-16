package sme.perf.result.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

public class ResultDescription {
	
    @Id
    @GeneratedValue(generator = "ResultDescription")
    @SequenceGenerator(name = "ResultDescription", sequenceName = "RESULT_SEQ", allocationSize = 1, initialValue = 1)
	private int id;
	
	private String resultName;
	
	private int threadNumber;

	public String getResultName() {
		return resultName;
	}

	public void setResultName(String resultName) {
		this.resultName = resultName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getThreadNumber() {
		return threadNumber;
	}

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}
}
