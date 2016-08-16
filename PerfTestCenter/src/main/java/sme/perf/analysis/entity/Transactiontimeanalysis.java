package sme.perf.analysis.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

class TransactiontimeanalysiIdClass{
	private int resultSessionId;
	private String transactionName;
}
/**
 * The persistent class for the TRANSACTIONTIMEANALYSIS database table.
 * 
 */


@Getter
@Setter
@Entity
@IdClass(value = TransactiontimeanalysiIdClass.class)
@Table(name="TRANSACTIONTIMEANALYSIS")

public class Transactiontimeanalysis implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name="AvgRspTm")
	private BigDecimal avgRspTm;
	@Column(name="Fail")
	private int fail;
	@Column(name="MaxRspTm")
	private BigDecimal maxRspTm;
	@Column(name="MinRspTm")
	private BigDecimal minRspTm;
	@Column(name="StdRspTm")
	private BigDecimal stdRspTm;
	@Column(name="Pass")
	private int pass;
	@Id
	private int resultSessionId;
	@Id
	private String transactionName;


}