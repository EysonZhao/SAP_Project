/**
 * 
 */
package sme.perf.result.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 * @author I311112
 * 
 */
@Entity
public class Machine {
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue(generator = "MachineSeq")
    @SequenceGenerator(name = "MachineSeq", sequenceName = "MACHINE_SEQ", allocationSize = 1, initialValue = 1)
    private int id;

    private String os;

    private String hostName;

    private int cpuCoreNumber;

    private String cpuCoreModel;

    private long memorySize;
    
    private String osType;
    
    private String hanaVersion;
    
    private String mariadbVersion;
    
    private String fileSystem;
    
    private String buildVersion;
    
    private String csmContainer;
    
    private String csmJavaVersion;

    private String csmJVMHeapSize;
    
    private String occContainer;
    
    private String occJavaVersion;

    private String occJVMHeapSize;
    
    private String jobContainer;
    
    private String jobJavaVersion;

    private String jobJVMHeapSize;
    
    private String eshopContainer;

    @ManyToOne
    private ResultSession resultSession;

    public ResultSession getResultSession() {
        return resultSession;
    }

    public void setResultSession(ResultSession resultSession) {
        this.resultSession = resultSession;
    }

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getCpuCoreNumber() {
		return cpuCoreNumber;
	}

	public void setCpuCoreNumber(int cpuCoreNumber) {
		this.cpuCoreNumber = cpuCoreNumber;
	}

	public String getCpuCoreModel() {
		return cpuCoreModel;
	}

	public void setCpuCoreModel(String cpuCoreModel) {
		this.cpuCoreModel = cpuCoreModel;
	}

	public long getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getHanaVersion() {
		return hanaVersion;
	}

	public void setHanaVersion(String hanaVersion) {
		this.hanaVersion = hanaVersion;
	}

	public String getMariadbVersion() {
		return mariadbVersion;
	}

	public void setMariadbVersion(String mariadbVersion) {
		this.mariadbVersion = mariadbVersion;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}

	public String getCsmContainer() {
		return csmContainer;
	}

	public void setCsmContainer(String csmContainer) {
		this.csmContainer = csmContainer;
	}

	public String getCsmJavaVersion() {
		return csmJavaVersion;
	}

	public void setCsmJavaVersion(String csmJavaVersion) {
		this.csmJavaVersion = csmJavaVersion;
	}


	public String getOccContainer() {
		return occContainer;
	}

	public void setOccContainer(String occContainer) {
		this.occContainer = occContainer;
	}

	public String getEshopContainer() {
		return eshopContainer;
	}

	public void setEshopContainer(String eshopContainer) {
		this.eshopContainer = eshopContainer;
	}


	public String getJobJavaVersion() {
		return jobJavaVersion;
	}

	public void setJobJavaVersion(String jobJavaVersion) {
		this.jobJavaVersion = jobJavaVersion;
	}

	public String getJobContainer() {
		return jobContainer;
	}

	public void setJobContainer(String jobContainer) {
		this.jobContainer = jobContainer;
	}


	public String getOccJavaVersion() {
		return occJavaVersion;
	}

	public void setOccJavaVersion(String occJavaVersion) {
		this.occJavaVersion = occJavaVersion;
	}

	public String getCsmJVMHeapSize() {
		return csmJVMHeapSize;
	}

	public void setCsmJVMHeapSize(String csmJVMHeapSize) {
		this.csmJVMHeapSize = csmJVMHeapSize;
	}

	public String getOccJVMHeapSize() {
		return occJVMHeapSize;
	}

	public void setOccJVMHeapSize(String occJVMHeapSize) {
		this.occJVMHeapSize = occJVMHeapSize;
	}

	public String getJobJVMHeapSize() {
		return jobJVMHeapSize;
	}

	public void setJobJVMHeapSize(String jobJVMHeapSize) {
		this.jobJVMHeapSize = jobJVMHeapSize;
	}

}
