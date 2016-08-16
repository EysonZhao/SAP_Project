package sme.perf.analysis.entity;

public class ThroughputParameters {
	
	private int resultSessionId;
	
	private int granularity;
	
	private String transactionName;

	public int getResultSessionId() {
		return resultSessionId;
	}

	public void setResultSessionId(int resultSessionId) {
		this.resultSessionId = resultSessionId;
	}

	public int getGranularity() {
		return granularity;
	}

	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}


	
	
}
