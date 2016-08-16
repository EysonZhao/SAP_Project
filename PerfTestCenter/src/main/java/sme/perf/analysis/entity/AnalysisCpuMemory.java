package sme.perf.analysis.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;


@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = "getDockerCpuMemoryResult", query = " Select T0.HOSTNAME,t0.AvgCpu/t1.CPUCORENUMBER,t0.MaxCpu/t1.CPUCORENUMBER,t0.AvgMem,T0.MaxMem,T0.\"service\" from "
				+ "(SELECT r.hostName, AVG(o.cpuUsage) as AvgCpu,MAX(o.cpuUsage) as MaxCpu,AVG(o.memUsage) as AvgMem, MAX(o.memUsage) as MaxMem ,r.\"service\" "
				+ "FROM DockerStatsSubLine o, DockerStatsHeader r where r.RESULTSESSION_ID=?1 and o.dockerStatsHeader_id=r.ID and r.date>=?2 and r.date<?3 and r.\"service\" is not null "
				+ "group by r.hostName,r.\"service\") T0, "
				+ "(SELECT m.hostName,m.CPUCORENUMBER FROM MACHINE m where m.RESULTSESSION_ID=?4) T1 "
				+ "where T1.HOSTNAME like T0.HOSTNAME+'%'"),
		@NamedNativeQuery(name = "getHANACpuMemoryResult", query = "Select T0.HOSTNAME,Sum(t0.AvgCpu/t1.CPUCORENUMBER),Sum(t0.MaxCpu/t1.CPUCORENUMBER),Sum(t0.AvgMem),Sum(T0.MaxMem) from "
				+ "(SELECT r.hostName,AVG(o.cpu) as AvgCpu ,MAX(o.cpu)as MaxCpu,AVG(o.memory)as AvgMem,MAX(o.memory)as MaxMem ,o.command FROM TopSubLine o, TopHeader r where r.RESULTSESSION_ID=?1 "
				+ "and o.topHeader_id=r.id and r.date>=?2 and r.date <?3 and o.command like 'hdb%'  group by r.hostName,o.command) T0, "
				+ "(SELECT m.hostName,m.CPUCORENUMBER FROM MACHINE m where m.RESULTSESSION_ID=?4) T1 "
				+ "where T1.HOSTNAME like T0.HOSTNAME+'%' group by T0.HOSTNAME ") })
		//TODO  where T1.HOSTNAME like T0.HOSTNAME+'%' change to  where T1.HOSTNAME=T0.HOSTNAME?
public class AnalysisCpuMemory {
 	@Id
    @GeneratedValue(generator = "AnalysisCpuMemorySeq")
    @SequenceGenerator(name = "AnalysisCpuMemorySeq", sequenceName = "ANACPUMEMORY_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    private String hostname;

    private double avgCPU;
    
    private double maxCPU;
    
    private double avgMemory;
    
    private double maxMemory;
    
    private String service;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    private AnalysisSession analysisSession;

    public AnalysisSession getAnalysisSession() {
        return analysisSession;
    }

    public void setAnalysisSession(AnalysisSession analysisSession) {
        this.analysisSession = analysisSession;
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

	public String getService() {
		return service;
	}

	public void setService(String service) {
		if(service!=null){
			this.service = service;
		}else{
			this.service = "Docker";
		}
	}
}
