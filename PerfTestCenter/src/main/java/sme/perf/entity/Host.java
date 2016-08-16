package sme.perf.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Host {
	public Host(String hostName, String iP, String userName,
			String userPassword, String description) {
		super();
		this.hostName = hostName;
		IP = iP;
		this.userName = userName;
		this.userPassword = userPassword;
		this.description = description;
	}

	public Host(){
		
	}

	@Id
	@GeneratedValue(generator="HostSeq")
    @SequenceGenerator(name = "HostSeq", sequenceName = "HOST_REQ", allocationSize = 1, initialValue = 1)
	private long id;
	
	private String hostName;
	private String IP;
	private String mode;
	private long memoryGB;
	private long cpuCore;
	private long diskGB;
	private String operationSystem;
	
	private String userName;
	private String userPassword;
	@Column(length=400)
	private String description;
	
	private String iloAddress;
	private String iloUser;
	private String iloPassword;

	public String getIloAddress() {
		return iloAddress;
	}

	public void setIloAddress(String iloAddress) {
		this.iloAddress = iloAddress;
	}

	public String getIloUser() {
		return iloUser;
	}

	public void setIloUser(String iloUser) {
		this.iloUser = iloUser;
	}

	public String getIloPassword() {
		return iloPassword;
	}

	public void setIloPassword(String iloPassword) {
		this.iloPassword = iloPassword;
	}

	@Column(precision=12, scale=2)
	private BigDecimal frequency;
		
	public BigDecimal getFrequency() {
		return frequency;
	}

	public void setFrequency(BigDecimal frequency) {
		this.frequency = frequency;
	}

	public String getOperationSystem() {
		return operationSystem;
	}

	public void setOperationSystem(String operationSystem) {
		this.operationSystem = operationSystem;
	}

	public long getMemoryGB() {
		return memoryGB;
	}

	public void setMemoryGB(long memoryGB) {
		this.memoryGB = memoryGB;
	}

	public long getCpuCore() {
		return cpuCore;
	}

	public void setCpuCore(long cpuCore) {
		this.cpuCore = cpuCore;
	}

	public long getDiskGB() {
		return diskGB;
	}

	public void setDiskGB(long diskGB) {
		this.diskGB = diskGB;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
