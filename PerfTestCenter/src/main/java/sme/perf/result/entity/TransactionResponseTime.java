package sme.perf.result.entity;

import java.sql.Timestamp;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Setter
@Entity
public class TransactionResponseTime {
	// @GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	@GeneratedValue(generator = "TransactionResponseTimeSeq")
	@SequenceGenerator(name = "TransactionResponseTimeSeq", sequenceName = "TRANSACTION_SEQ", allocationSize = 1, initialValue = 1)
	private int id;

	@ManyToOne
	private ResultSession resultSession;
	private String hostName;

	@Column(name = "date")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Timestamp date;
	private String transactionName;
	private double responseTime;
	private String result;
	private long thread_id;
//July 11 added by Yansong	
	@Column(columnDefinition = "numeric(10,2)")
	private float cpuUtil;
	@Column(columnDefinition = "numeric(10,2)")
	private float cpuTime;
	
	
	private long memPrivateFrom;
	private long memPrivateTo;
	
	private long memPeakFrom;
	private long memPeakTo;
	
}
