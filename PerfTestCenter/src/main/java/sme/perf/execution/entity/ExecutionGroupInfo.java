package sme.perf.execution.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import sme.perf.execution.State;
import sme.perf.task.Result;
import sme.perf.utility.LogHelper;

@Entity
public class ExecutionGroupInfo implements Cloneable{
    @Id
    @GeneratedValue(generator = "ExecutionInfo")
    @SequenceGenerator(name = "ExecutionInfo", sequenceName = "EXECUTION_SEQ", allocationSize = 1, initialValue = 1)
    private long id;
    private String name;
	private String executionIdList;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExecutionIdList() {
		return executionIdList;
	}
	public void setExecutionIdList(String executionIdList) {
		this.executionIdList = executionIdList;
	}
	
	@Column(name = "startDate")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private DateTime startDate;
	
	@Column(name = "endDate")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private DateTime endDate;
	
    private State state;
    private Result result;
    private int timeout;
    

	public int getTimeout() {
		return timeout==0?10800:timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public DateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}
	public DateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	} 
	
	@Override
	public Object clone(){
		ExecutionGroupInfo execGroupInfo = null;
		try{
			execGroupInfo = (ExecutionGroupInfo) super.clone();
			execGroupInfo.setId(0L);
		}
		catch(CloneNotSupportedException e){
			LogHelper.error(e);
		}
		return execGroupInfo;
	}
}
