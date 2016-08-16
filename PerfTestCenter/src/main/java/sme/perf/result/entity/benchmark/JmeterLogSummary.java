package sme.perf.result.entity.benchmark;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class JmeterLogSummary {
	 	@Id
	    @GeneratedValue(generator = "JmeterLogSummarySeq")
	    @SequenceGenerator(name = "JmeterLogSummarySeq", sequenceName = "JMETERSUMMARY_SEQ", allocationSize = 1, initialValue = 1)
	    private int id;

	    private String request;

	    private double tps;
	    
	    private double averageResponseTime;
	    
	    private double maxResponseTime;
	    
	    private double percent90ResponseTime;
	    
	    private StatusEnum status;
	    
	    private int baseRequestId;
	    
	    private double difference; 

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    @ManyToOne(fetch = FetchType.LAZY)
	    private RequestSummary requestSummary;

	    public RequestSummary getRequestSummary() {
	        return requestSummary;
	    }

	    public void setRequestSummary(RequestSummary requestSummary) {
	        this.requestSummary = requestSummary;
	    }

	    public String getRequest() {
	        return request;
	    }

	    public void setRequest(String request) {
	        this.request = request;
	    }

	    public double getTps() {
	        return tps;
	    }

	    public void setTps(double tps) {
	        this.tps = tps;
	    }

		public double getAverageResponseTime() {
			return averageResponseTime;
		}

		public void setAverageResponseTime(double averageResponseTime) {
			this.averageResponseTime = averageResponseTime;
		}

		public double getMaxResponseTime() {
			return maxResponseTime;
		}

		public void setMaxResponseTime(double maxResponseTime) {
			this.maxResponseTime = maxResponseTime;
		}

		public double getPercent90ResponseTime() {
			return percent90ResponseTime;
		}

		public void setPercent90ResponseTime(double percent90ResponseTime) {
			this.percent90ResponseTime = percent90ResponseTime;
		}

		public StatusEnum getStatus() {
			return status;
		}

		public void setStatus(StatusEnum status) {
			this.status = status;
		}

		public int getBaseRequestId() {
			return baseRequestId;
		}

		public void setBaseRequestId(int baseRequestId) {
			this.baseRequestId = baseRequestId;
		}

		public double getDifference() {
			return difference;
		}

		public void setDifference(double difference) {
			this.difference = difference;
		}

}

