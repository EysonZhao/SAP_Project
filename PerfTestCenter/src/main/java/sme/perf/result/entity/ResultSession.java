package sme.perf.result.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ResultSession {
	// @GeneratedValue(strategy=GenerationType.TABLE)
	@Id
	@GeneratedValue(generator = "ResultSessionSeq")
	@SequenceGenerator(name = "ResultSessionSeq", sequenceName = "RESULTSESSION_SEQ", allocationSize = 1, initialValue = 1)
	private int id;
	private String name;

	@Column(columnDefinition = "DateTime")
	@JsonSerialize(using = sme.perf.utility.JsonDateSerializer.class)
	@JsonDeserialize(using = sme.perf.utility.JsonDateDeserializer.class)
	@Convert("jodaDateConverter1")
	private DateTime createDate;
	private int userNumber;
	private String scenario;
	private String buildInfo;
	private String branch;
	private long executionId;
	private String executionName;
	private long requestId;
	private long isTimeAnalyse;
	
	@OneToMany(mappedBy = "resultSession")
	private List<TopHeader> topHeaders = new ArrayList<TopHeader>();
	@OneToMany(mappedBy = "resultSession")
	private List<IOHeader> ioHeaders = new ArrayList<IOHeader>();
	// @OneToMany(mappedBy = "resultSession")
	// private List<LogHttpResponseTime> logHttpResponseTimes = new
	// ArrayList<LogHttpResponseTime>();
	// @OneToMany(mappedBy = "resultSession")
	// private List<RRDResult> rrdResults = new ArrayList<RRDResult>();
	@OneToMany(mappedBy = "resultSession")
	private List<TransactionResponseTimeCache> transactionResponseTimes = new ArrayList<TransactionResponseTimeCache>();
	// @OneToMany(mappedBy = "resultSession")
	// private List<CpuCoreHeader> coreHeaders = new ArrayList<CpuCoreHeader>();
	// @OneToMany(mappedBy = "resultSession")
	// private List<Landscape> landscapes = new ArrayList<Landscape>();
	// @OneToMany(mappedBy = "resultSession")
	// private List<Network> networks = new ArrayList<Network>();
	// @OneToMany(mappedBy = "resultSession")
	// private List<DockerTopHeader> dockerTopHeaders = new
	// ArrayList<DockerTopHeader>();
	@OneToMany(mappedBy = "resultSession")
	private List<JmeterLog> jmeterLogs = new ArrayList<JmeterLog>();
	@OneToMany(mappedBy = "resultSession")
	private List<DockerStatsHeader> dockerStatsHeaders = new ArrayList<DockerStatsHeader>();
	@OneToMany(mappedBy = "resultSession")
	private List<Machine> machines = new ArrayList<Machine>();

}
