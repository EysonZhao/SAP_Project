package sme.perf.result.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class DockerStatsSubLine {
	
    @Id
    @GeneratedValue(generator = "DockerStatsSubLineSeq")
    @SequenceGenerator(name = "DockerStatsSubLineSeq", sequenceName = "DOCKERSTATSSUBLINE_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    @ManyToOne
    private DockerStatsHeader dockerStatsHeader;
	private String dockerId;
	private float cpuUsage;
	private float memUsage;
	private float memLimit;
	private float memPercentage;
	private float netIn;
	private float netOut;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public DockerStatsHeader getDockerStatsHeader() {
		return dockerStatsHeader;
	}
	public void setDockerStatsHeader(DockerStatsHeader dockerStatsHeader) {
		this.dockerStatsHeader = dockerStatsHeader;
	}
	public String getDockerId() {
		return dockerId;
	}
	public void setDockerId(String dockerId) {
		this.dockerId = dockerId;
	}
	public float getCpuUsage() {
		return cpuUsage;
	}
	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	public float getMemUsage() {
		return memUsage;
	}
	public void setMemUsage(float memUsage) {
		this.memUsage = memUsage;
	}
	public float getMemLimit() {
		return memLimit;
	}
	public void setMemLimit(float memLimit) {
		this.memLimit = memLimit;
	}
	public float getMemPercentage() {
		return memPercentage;
	}
	public void setMemPercentage(float memPercentage) {
		this.memPercentage = memPercentage;
	}
	public float getNetIn() {
		return netIn;
	}
	public void setNetIn(float netIn) {
		this.netIn = netIn;
	}
	public float getNetOut() {
		return netOut;
	}
	public void setNetOut(float netOut) {
		this.netOut = netOut;
	}

}
