package sme.perf.result.entity.benchmark;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class CpuMemoryInfoSummary {
 	@Id
    @GeneratedValue(generator = "CpuMemoryInfoSummarySeq")
    @SequenceGenerator(name = "CpuMemoryInfoSummarySeq", sequenceName = "CPUMEMORYSUMMARY_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    private String hostname;

    private double avgCPU;
    
    private double maxCPU;
    
    private double avgMemory;
    
    private double maxMemory;

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

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public double getAvgCPU() {
		return avgCPU;
	}

	public void setAvgCPU(double avgCPU) {
		this.avgCPU = avgCPU;
	}

	public double getAvgMemory() {
		return avgMemory;
	}

	public void setAvgMemory(double avgMemory) {
		this.avgMemory = avgMemory;
	}

	public double getMaxCPU() {
		return maxCPU;
	}

	public void setMaxCPU(double maxCPU) {
		this.maxCPU = maxCPU;
	}

	public double getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(double maxMemory) {
		this.maxMemory = maxMemory;
	}
}
