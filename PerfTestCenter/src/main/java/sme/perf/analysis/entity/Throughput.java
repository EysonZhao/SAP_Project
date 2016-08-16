package sme.perf.analysis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SqlResultSetMapping(
		name="ThroughputMap",
		entities={
			@EntityResult(
				entityClass=Throughput.class,
				fields={
					@FieldResult(name="N", column="N"),
					@FieldResult(name="sessionId", column="sessionId"),
					@FieldResult(name="TPS", column="TPS"),
					@FieldResult(name="AvgResponseTime", column="AvgResponseTime"),
					@FieldResult(name="TransactionName", column="TransactionName"),
				}
			)
		}
	)

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name ="ThroughputSQL", query = 
				"declare @sessionid int set @sessionid=?1;"
				+ "declare @STARTTIME DATETIME SET @STARTTIME="
				+ "(select MIN([date]) from [PerfCenterResultDebug].dbo.TransactionResponseTime  where RESULTSESSION_ID = @sessionid); "
				+ "declare @granularity int set @granularity= ?2;"
				+ "select DateDiff(s, @STARTTIME,[date])/@granularity As N, @sessionid SessionId, "
				+ "MIN([date]) as [StartTime], T0.TransactionName, round(avg(T0.ResponseTime),2) AS AvgResponseTime, "
				+ "round(cast( count(1) as float)/@granularity,2) AS TPS ,(ROW_NUMBER() OVER (ORDER BY T0.TransactionName)) Id  "
				+ "from [PerfCenterResultDebug].dbo.TransactionResponseTime T0 "
				+ "where T0.RESULTSESSION_ID = @sessionid and Result='Pass' and TransactionName like ?3 "
				+ "Group By T0.TransactionName, DateDiff(s,@STARTTIME , [date] )/@granularity Order By T0.TransactionName, [StartTime]",
				resultSetMapping="ThroughputMap"),

})


public class Throughput {

 	@Id
    @GeneratedValue(generator = "ThroughputSeq")
    @SequenceGenerator(name = "ThroughputSeq", sequenceName = "THROU_SEQ", allocationSize = 1, initialValue = 1)
 	private int Id;
 	
 	private int N;
 	
 	private int sessionId;
 	
 	private String TransactionName;
 	
	@Column(columnDefinition = "DateTime")
    @Convert("jodaDateConverter")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
 	private DateTime startTime;
 	
 	private float AvgResponseTime;
 	
 	private float TPS;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getTransactionName() {
		return TransactionName;
	}

	public void setTransactionName(String transactionName) {
		this.TransactionName = transactionName;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public float getAvgResponseTime() {
		return AvgResponseTime;
	}

	public void setAvgResponseTime(float avgResponseTime) {
		AvgResponseTime = avgResponseTime;
	}

	public float getTPS() {
		return TPS;
	}

	public void setTPS(float tPS) {
		TPS = tPS;
	}
 	
	
}
