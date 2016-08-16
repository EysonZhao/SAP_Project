package sme.perf.analysis.entity;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FetchType;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;

import sme.perf.request.entity.StatusReportItem;

@Entity
@NamedNativeQueries({
		@NamedNativeQuery(name = "getAnalysisResultNew", query = "select T0.avg, T0.max, T0.PassCount,T1.AllCount,T4.RESPONSETIME as p9r,T0.REQUEST "
				+ "from (SELECT AVG(RESPONSETIME) as avg, Count(RESPONSETIME) as PassCount, MAX(RESPONSETIME) as max,REQUEST "
				+ "FROM JmeterLog o "
				+ "where o.RESULTSESSION_ID=?1 and o.REQUEST not like 'javaScript%' "
				+ "and o.date>=?2 and o.date<?3 "
				+ "and o.STATUS='ok' group by REQUEST) T0 join "
				+ "(Select Count(RESPONSETIME) as AllCount ,REQUEST FROM JmeterLog o "
				+ "where RESULTSESSION_ID=?4 and o.date>=?5 and o.date<?6 and "
				+ "REQUEST not like 'javaScript%' group by REQUEST) T1 "
				+ "on T0.REQUEST=T1.REQUEST join (Select T3.ResponseTime,T3.Request,T3.RequestRowCount,T3.RN "
				+ "FROM (SELECT T5.ResponseTime,T5.Request,T6.RequestRowCount,T5.RN "
				+ "FROM ((SELECT T2.ResponseTime,T2.Request, RN = ROW_NUMBER() OVER (Partition by request ORDER BY ResponseTime,request) "
				+ "FROM JmeterLog T2 "
				+ "where T2.RESULTSESSION_ID=?7 and T2.STATUS='ok' and T2.REQUEST not like 'javaScript%' and T2.date>=?8 and T2.date<?9) T5 join "
				+ "(select COUNT(1) as RequestRowCount, Request FROM JmeterLog where RESULTSESSION_ID=?10 and date>=?11 and date<?12 group by request) T6 on T6.REQUEST=T5.REQUEST)) T3 "
				+ "where T3.RN =CEILING(0.9*RequestRowCount)) T4 on T4.REQUEST=T1.REQUEST"),
		@NamedNativeQuery(name = "getAnalysisResultNewWithRequest", query = "select T0.avg, T0.max, T0.PassCount,T1.AllCount,T4.RESPONSETIME as p9r,T0.REQUEST "
				+ "from (SELECT AVG(RESPONSETIME) as avg, Count(RESPONSETIME) as PassCount, MAX(RESPONSETIME) as max,REQUEST "
				+ "FROM JmeterLog o "
				+ "where o.RESULTSESSION_ID=?1 and o.REQUEST not like 'javaScript%' "
				+ "and o.date>=?2 and o.date<?3 "
				+ "and o.STATUS='ok' group by REQUEST) T0 join "
				+ "(Select Count(RESPONSETIME) as AllCount ,REQUEST FROM JmeterLog o "
				+ "where RESULTSESSION_ID=?4 and o.date>=?5 and o.date<?6 and "
				+ "REQUEST not like 'javaScript%' group by REQUEST) T1 "
				+ "on T0.REQUEST=T1.REQUEST join (Select T3.ResponseTime,T3.Request,T3.RequestRowCount,T3.RN "
				+ "FROM (SELECT T5.ResponseTime,T5.Request,T6.RequestRowCount,T5.RN "
				+ "FROM ((SELECT T2.ResponseTime,T2.Request, RN = ROW_NUMBER() OVER (Partition by request ORDER BY ResponseTime,request) "
				+ "FROM JmeterLog T2 "
				+ "where T2.RESULTSESSION_ID=?7 and T2.STATUS='ok' and T2.REQUEST not like 'javaScript%' and T2.date>=?8 and T2.date<?9) T5 join "
				+ "(select COUNT(1) as RequestRowCount, Request FROM JmeterLog where RESULTSESSION_ID=?10 and date>=?11 and date<?12 group by request) T6 on T6.REQUEST=T5.REQUEST)) T3 "
				+ "where T3.RN =CEILING(0.9*RequestRowCount)) T4 on T4.REQUEST=T1.REQUEST and T4.REQUEST in (?13 , ?14 , ?15 , ?16 , ?17 , ?18 , ?19 , ?20 , ?21 , ?22 , ?23 , ?24, ?25, ?26, ?27, ?28, ?29, ?30, ?31, ?32)") })
public class AnalysisJmeter {
	@Id
	@GeneratedValue(generator = "AnalysisJmeterSeq")
	@SequenceGenerator(name = "AnalysisJmeterSeq", sequenceName = "ANAJMETER_SEQ", allocationSize = 1, initialValue = 1)
	private int id;

	private String request;

	private double hps;

	private double averageResponseTime;

	private double maxResponseTime;

	private double percent90ResponseTime;

	private double failRate;
	
	private long passCount;

	private long allCount;
	// private StatusEnum status;

	// private int baseRequestId;

	// private double difference;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	private AnalysisSession analysisSession;

	public AnalysisSession getAnalysisSession() {
		return analysisSession;
	}

	public void setAnalysisSession(AnalysisSession analysisSession) {
		this.analysisSession = analysisSession;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public double getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(double averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public double getMaxResponseTime() {
		return maxResponseTime;
	}

	public void setMaxResponseTime(double maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	public double getPercent90ResponseTime() {
		return percent90ResponseTime;
	}

	public void setPercent90ResponseTime(double percent90ResponseTime) {
		this.percent90ResponseTime = percent90ResponseTime;
	}

	public double getfailRate() {
		return failRate;
	}

	public void setfailRate(double failRate) {
		this.failRate = failRate;
	}

	public double getHps() {
		return hps;
	}

	public void setHps(double hps) {
		this.hps = hps;
	}

	public long getAllCount() {
		return allCount;
	}

	public void setAllCount(long allCount) {
		this.allCount = allCount;
	}

	public long getPassCount() {
		return passCount;
	}

	public void setPassCount(long passCount) {
		this.passCount = passCount;
	}

	// public StatusEnum getStatus() {
	// return status;
	// }
	//
	// public void setStatus(StatusEnum status) {
	// this.status = status;
	// }
	//
	// public int getBaseRequestId() {
	// return baseRequestId;
	// }
	//
	// public void setBaseRequestId(int baseRequestId) {
	// this.baseRequestId = baseRequestId;
	// }
	//
	// public double getDifference() {
	// return difference;
	// }
	//
	// public void setDifference(double difference) {
	// this.difference = difference;
	// }
}
