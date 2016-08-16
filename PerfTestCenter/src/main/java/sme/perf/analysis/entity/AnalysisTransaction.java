package sme.perf.analysis.entity;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;

import lombok.Getter;
import lombok.Setter;


@SqlResultSetMapping(
		name="TransactionAnalysisMap",
		entities={
			@EntityResult(
				entityClass=AnalysisTransaction.class,
				fields={
					@FieldResult(name="TransactionName", column="TransactionName"),
					@FieldResult(name="AvgRspTm", column="AvgRspTm"),
					@FieldResult(name="MaxRspTm", column="MaxRspTm"),
					@FieldResult(name="MinRspTm", column="MinRspTm"),
					@FieldResult(name="Pass", column="Pass"),
					@FieldResult(name="Fail", column="Fail"),
					@FieldResult(name="resultSessionId", column="resultSessionId")
				}
			)
		}
	)



@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name ="GetTransactionReport", query = "SELECT  (ROW_NUMBER() over(order by T1.TransactionName)) Id,"
				+ "T1.TransactionName AS transactionName,T1.AvgRspTm, T1.MaxRspTm, T1.MinRspTm, T1.Pass, T3.Fail,?1 resultSessionId "
				+ " FROM   (SELECT     TRANSACTIONNAME AS TransactionName, COUNT(1) AS Pass, CAST(AVG(RESPONSETIME) as decimal(10,2)) AS AvgRspTm, "
				+ "CAST(MAX(RESPONSETIME) as decimal(10,2)) AS MaxRspTm , CAST(MIN(RESPONSETIME) as decimal(10,2)) AS MinRspTm"
				+ " FROM   [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME AS T0 WHERE (RESULT = 'Pass') AND (RESULTSESSION_ID = ?1)"
				+ " GROUP BY TRANSACTIONNAME) AS T1 FULL OUTER JOIN "
				+ "(SELECT  TRANSACTIONNAME AS TransactionName, COUNT(1) AS Fail FROM [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME AS T2 "
				+ "WHERE      (RESULT = 'Fail') AND (RESULTSESSION_ID = ?1)"
				+ " GROUP BY TRANSACTIONNAME) AS T3 ON T1.TransactionName = T3.TransactionName ORDER BY transactionName",
				resultSetMapping="TransactionAnalysisMap"),
		@NamedNativeQuery(name ="GetSessionIds", query = 
				"SELECT  ROW_NUMBER() over(order by Resultsession_Id) Id,'null' transactionName,0 AvgRspTm, 0 MaxRspTm, 0 MinRspTm, 0 Pass, 0 Fail,"
						+ "RESULTSESSION_ID AS resultSessionId  from [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME t0"
						+ " where t0.ID in (select MIN(id) from [PerfCenterResultDebug].dbo.TRANSACTIONRESPONSETIME t1"
						+ " group by RESULTSESSION_ID)  order by RESULTSESSION_ID",
				resultSetMapping="TransactionAnalysisMap"),
		@NamedNativeQuery(name ="SelectTransaction", query = "select * from ANALYSISTRANSACTION where resultSessionId=?1",		
				resultSetMapping="TransactionAnalysisMap"),
})

@Getter
@Setter
public class AnalysisTransaction {
	

 	
 	@Id
    @GeneratedValue(generator = "AnalysisTransSeq")
    @SequenceGenerator(name = "AnalysisTransSeq", sequenceName = "ANA_SEQ", allocationSize = 1, initialValue = 1)
 	private int Id;

	private String transactionName;
	
	private int resultSessionId;


	private String AvgRspTm;

    private double MaxRspTm;
    
    private double MinRspTm;
    
    private int Pass;
    
    private int Fail;

    
}
