package sme.perf.analysis.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;

import sme.perf.utility.JodaDateConverter;

@Entity
@Converter(name = "jodaDateConverter3", converterClass = JodaDateConverter.class)
public class AnalysisSession {
	 	@Id
	    private int id;

	    private String name;

	    @Column(columnDefinition = "DateTime")
	    @Convert("jodaDateConverter3")
	    private DateTime createDate;

	    @Column(columnDefinition = "DateTime")
	    @Convert("jodaDateConverter3")
	    private DateTime testStartDate;
	    
	    @Column(columnDefinition = "DateTime")
	    @Convert("jodaDateConverter3")
	    private DateTime testEndDate;

	    private int userNumber;

	    private String scenario;
	    
	    private String buildInfo;
	    
	    private String branch;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public DateTime getCreateDate() {
	        return createDate;
	    }

	    public void setCreateDate(DateTime createDate) {
	        this.createDate = createDate;
	    }

	    public DateTime getTestStartDate() {
	        return testStartDate;
	    }

	    public void setTestStartDate(DateTime testStartDate) {
	        this.testStartDate = testStartDate;
	    }

	    public DateTime getTestEndDate() {
	        return testEndDate;
	    }

	    public void setTestEndDate(DateTime testEndDate) {
	        this.testEndDate = testEndDate;
	    }
	    
	    public int getUserNumber() {
	        return userNumber;
	    }

	    public void setUserNumber(int userNumber) {
	        this.userNumber = userNumber;
	    }

	    public String getScenario() {
	        return scenario;
	    }

	    public void setScenario(String scenario) {
	        this.scenario = scenario;
	    }

		public String getBuildInfo() {
			return buildInfo;
		}

		public void setBuildInfo(String buildInfo) {
			this.buildInfo = buildInfo;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}
		
	    @OneToMany(mappedBy = "analysisSession")
	    private List<AnalysisJmeter> analysisJmeters = new ArrayList<AnalysisJmeter>();
	    
	    @OneToMany(mappedBy = "analysisSession")
	    private List<AnalysisCpuMemory> analysisCpuMemorys = new ArrayList<AnalysisCpuMemory>();

	    @OneToMany(mappedBy = "analysisSession")
	    private List<AnalysisIO> analysisIOs = new ArrayList<AnalysisIO>();
}
