package sme.perf.result.entity.benchmark;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
@Entity
public class IOInfoSummary {
	@Id
    @GeneratedValue(generator = "JmeterLogSummarySeq")
    @SequenceGenerator(name = "JmeterLogSummarySeq", sequenceName = "JMETERSUMMARY_SEQ", allocationSize = 1, initialValue = 1)
    private int id;
	
	private String hostname;
	
	private String device;
	
    private double avgAverageQueueSize;
    
    private double maxAverageQueueSize;
   
    private double avgWritePerSecond;
    
    private double maxWritePerSecond;
    
    private double avgAwait;
    
    private double maxAwait;
    
    private double avgUtility;
    
    private double maxUtility;
    
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

	public double getAvgAverageQueueSize() {
		return avgAverageQueueSize;
	}

	public void setAvgAverageQueueSize(double avgAverageQueueSize) {
		this.avgAverageQueueSize = avgAverageQueueSize;
	}

	public double getMaxAverageQueueSize() {
		return maxAverageQueueSize;
	}

	public void setMaxAverageQueueSize(double maxAverageQueueSize) {
		this.maxAverageQueueSize = maxAverageQueueSize;
	}

	public double getAvgUtility() {
		return avgUtility;
	}

	public void setAvgUtility(double avgUtility) {
		this.avgUtility = avgUtility;
	}

	public double getMaxUtility() {
		return maxUtility;
	}

	public void setMaxUtility(double maxUtility) {
		this.maxUtility = maxUtility;
	}

	public double getAvgWritePerSecond() {
		return avgWritePerSecond;
	}

	public void setAvgWritePerSecond(double avgWritePerSecond) {
		this.avgWritePerSecond = avgWritePerSecond;
	}

	public double getMaxWritePerSecond() {
		return maxWritePerSecond;
	}

	public void setMaxWritePerSecond(double maxWritePerSecond) {
		this.maxWritePerSecond = maxWritePerSecond;
	}

	public double getAvgAwait() {
		return avgAwait;
	}

	public void setAvgAwait(double avgAwait) {
		this.avgAwait = avgAwait;
	}

	public double getMaxAwait() {
		return maxAwait;
	}

	public void setMaxAwait(double maxAwait) {
		this.maxAwait = maxAwait;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

}
