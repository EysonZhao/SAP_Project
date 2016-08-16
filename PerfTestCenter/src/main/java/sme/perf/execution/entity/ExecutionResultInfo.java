package sme.perf.execution.entity;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sme.perf.task.Result;
import sme.perf.utility.JodaDateConverter;

@Entity
public class ExecutionResultInfo {
    @Id
    @GeneratedValue(generator = "ExecutionResultInfo")
    @SequenceGenerator(name = "ExecutionResultInfo", sequenceName = "ExecutionResultInfo_Seq", allocationSize = 1, initialValue = 1)
    private long id;
    
    private long executionInfoId;
    private String resultSessionId;
    private String resultSessionName;
    private long resultCount;
    
	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter")
    private DateTime importDate;
	
	@ManyToOne(optional=true,  fetch=FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.PERSIST})
	@PrimaryKeyJoinColumn(name="executionInfoId")
    @JsonBackReference(value="result-executionInfo")
	private ExecutionInfo executionInfo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getExecutionInfoId() {
		return executionInfoId;
	}

	public void setExecutionInfoId(long executionInfoId) {
		this.executionInfoId = executionInfoId;
	}

	public String getResultSessionId() {
		return resultSessionId;
	}

	public void setResultSessionId(String resultSessionId) {
		this.resultSessionId = resultSessionId;
	}

	public String getResultSessionName() {
		return resultSessionName;
	}

	public void setResultSessionName(String resultSessionName) {
		this.resultSessionName = resultSessionName;
	}

	public long getResultCount() {
		return resultCount;
	}

	public void setResultCount(long resultCount) {
		this.resultCount = resultCount;
	}

	public DateTime getImportDate() {
		return importDate;
	}

	public void setImportDate(DateTime importDate) {
		this.importDate = importDate;
	}

	public ExecutionInfo getExecutionInfo() {
		return executionInfo;
	}

	public void setExecutionInfo(ExecutionInfo executionInfo) {
		this.executionInfo = executionInfo;
	}
}
