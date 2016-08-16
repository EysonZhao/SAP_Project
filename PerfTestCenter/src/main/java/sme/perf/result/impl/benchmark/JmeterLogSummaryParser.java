package sme.perf.result.impl.benchmark;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.apache.log4j.Logger;

import sme.perf.result.entity.ResultSession;
import sme.perf.result.entity.benchmark.*;

/**
 * @author I311112
 * 
 */
public class JmeterLogSummaryParser {
	private final RequestSummary requestsummary;
	private static final String PERSISTENCE_UNIT_NAME = "PerfTestCenterResult";
	private final EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	private final EntityManager em = emf.createEntityManager();
	private Logger logger;
	private final ResultSession rs;

	public JmeterLogSummaryParser(RequestSummary requestsummary, Logger logger) {
		this.requestsummary = requestsummary;
		this.logger=logger;
		int rsmaxId = requestsummary.getId();
		this.rs = em
				.createQuery("SELECT r FROM ResultSession r where r.id=:ID",
						ResultSession.class).setParameter("ID", rsmaxId)
				.getSingleResult();
	}

	public List<String> setListQurey() {
		// Set Request Query List
		List<String> listQur = new ArrayList<String>();

		// edit query list for new Transaction
		listQur.add("SalesOrderCreateBO");
		listQur.add("SalesOrderCreateBONode");
		listQur.add("AddSalesOrderFromOCC");
		
		//For InventoryCounting + PurchaseReceipts Case
		listQur.add("InventoryCountingCreate");
		listQur.add("InventoryCountingConfirm");
		listQur.add("GetInventoryCounting");
		listQur.add("PurchaseOrderCreate");
		listQur.add("PurchaseOrdercopyToPurchaseReceipts");
		listQur.add("PurchaseReceiptsCreate");
		
		//For InventoryCounting + PurchaseReceipts Case
		listQur.add("InventoryCountingCreate_withUDF");
		listQur.add("InventoryCountingConfirm_withUDF");
		listQur.add("GetInventoryCounting_withUDF");
		listQur.add("PurchaseOrderCreate_withUDF");
		listQur.add("PurchaseOrdercopyToPurchaseReceipts_withUDF");
		listQur.add("PurchaseReceiptsCreate_withUDF");
		
		//For PAL
		//listQur.add("PAL_A_Customers");
		//listQur.add("PAL_B_Customers");
		//listQur.add("PAL_C_Customers");
		
		//If Add RequestList
		listQur.addAll(setOpenAPIListQurey());
		return listQur;
	}

	public List<String> setOpenAPIListQurey() {
		// Set Request Query List
		List<String> listOpenAPIRequest = new ArrayList<String>();
		listOpenAPIRequest = em
				.createQuery(
						"SELECT o.request FROM JmeterLog o where o.resultSession=:RESULTSESSION and o.status=:STATUS and o.request like :REQUEST group by o.request",
						String.class).setParameter("RESULTSESSION", rs)
				.setParameter("STATUS", "ok").setParameter("REQUEST", "OpenAPI%").getResultList();
		return listOpenAPIRequest;
	}
	
	public List<JmeterLogSummary> setJmeterLogSummaryList(int baseResultRequestID, double standard) {
		if (standard==0){
			standard=0.1;
		}
		long start = System.currentTimeMillis();
		List<JmeterLogSummary> jmeterLogSummarylist = new ArrayList<JmeterLogSummary>();

		DateTime startDate = requestsummary.getTestStartDate();
		DateTime endDate = requestsummary.getTestEndDate();

		Duration d = new Duration(startDate, endDate);  
		long diffSeconds = d.getStandardSeconds();
		
		RequestSummary basers=null;
		if (baseResultRequestID != 0) {
			basers = em
					.createQuery(
							"SELECT o FROM RequestSummary o where o.id=:ID",
							RequestSummary.class)
					.setParameter("ID", baseResultRequestID)
					.getSingleResult();
		} else {
			logger.info("No RequestSummary is set to base.");
		}

		for (String qur : setListQurey()) {
			double tps = 0, p9r = 0, avgr = 0, maxr = 0;
			List<Double> jmeterList = new ArrayList<Double>();;
			if(qur.contains("SalesOrder")&&!qur.contains("OpenAPI")){
				jmeterList = em
						.createQuery(
								"SELECT o.responseTime FROM JmeterLog o where o.resultSession=:RESULTSESSION and o.status=:STATUS and o.request=:REQUEST and o.date>=:DATE and o.date <:DATEEND order by o.responseTime",
								double.class).setParameter("RESULTSESSION", rs)
						.setParameter("STATUS", "ok").setParameter("REQUEST", qur)
						.setParameter("DATEEND", endDate)
						.setParameter("DATE", startDate).getResultList();
				avgr = em
						.createQuery(
								"SELECT AVG(o.responseTime) FROM JmeterLog o where o.resultSession=:RESULTSESSION and o.status=:STATUS and o.request=:REQUEST and o.date>=:DATE and o.date <:DATEEND",
								double.class).setParameter("RESULTSESSION", rs)
						.setParameter("STATUS", "ok")
						.setParameter("REQUEST", qur)
						.setParameter("DATEEND", endDate)
						.setParameter("DATE", startDate).getSingleResult();
			}else{
				jmeterList = em
						.createQuery(
								"SELECT o.responseTime FROM JmeterLog o where o.resultSession=:RESULTSESSION and o.status=:STATUS and o.request=:REQUEST order by o.responseTime",
								double.class).setParameter("RESULTSESSION", rs)
						.setParameter("STATUS", "ok").setParameter("REQUEST", qur).getResultList();
				avgr = em
						.createQuery(
								"SELECT AVG(o.responseTime) FROM JmeterLog o where o.resultSession=:RESULTSESSION and o.status=:STATUS and o.request=:REQUEST",
								double.class).setParameter("RESULTSESSION", rs)
						.setParameter("STATUS", "ok")
						.setParameter("REQUEST", qur).getSingleResult();
			}
			
			// Tps
			int count = jmeterList.size();
			if (count != 0) {
				if (qur.contains("Add")) {
					BigDecimal tpsBig = new BigDecimal((double) count
							/ (double) diffSeconds);
					tps = tpsBig.setScale(2, BigDecimal.ROUND_HALF_UP)
							.doubleValue();
				} else {
					tps = 0;
				}

				// percent90 ResponseTime
				int p9index = new BigDecimal(0.9 * count).setScale(0,
						BigDecimal.ROUND_HALF_UP).intValue();
				p9r = jmeterList.get(p9index);

				// average ResponseTime
				BigDecimal avgrBig = new BigDecimal((double) avgr);
				avgr = avgrBig.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();

				// max ResponseTime
				maxr = jmeterList.get(count - 1);

				JmeterLogSummary jls = new JmeterLogSummary();
				jls.setRequest(qur);
				jls.setRequestSummary(requestsummary);
				jls.setTps(tps);
				jls.setMaxResponseTime(maxr);
				jls.setPercent90ResponseTime(p9r);
				jls.setAverageResponseTime(avgr);

				// for analysis status update
				//int baseid = 0;
				StatusEnum status = StatusEnum.None;
				double basep9r = 0;
				double diff = 0;

				if (basers != null) {
					List<Object[]> baselist = em
							.createQuery(
									"SELECT o.id, o.percent90ResponseTime FROM JmeterLogSummary o where o.requestSummary=:REQUESTSUMMARY and o.request=:REQUEST",
									Object[].class)
							.setParameter("REQUESTSUMMARY", basers)
							.setParameter("REQUEST", qur).getResultList();
					for (Object[] ob : baselist) {
						// baseid = Integer.parseInt(ob[0].toString());
						basep9r = Double.parseDouble(ob[1].toString());
						diff = (p9r - basep9r) / basep9r;
						diff = new BigDecimal((double) diff).setScale(4,
								BigDecimal.ROUND_HALF_UP).doubleValue();
						if (diff >= standard) {
							status = StatusEnum.Red;
						} else if (diff < standard && diff > standard * (-1)) {
							status = StatusEnum.Yellow;
						} else if (diff < standard * (-1)) {
							status = StatusEnum.Green;
						}
					}
				}

				jls.setDifference(diff);
				jls.setBaseRequestId(baseResultRequestID);
				jls.setStatus(status);
				jmeterLogSummarylist.add(jls);
			}	
		}

		logger.info("JmeterLog Summary Has been parsed. Response time (ms) = "
				+ (System.currentTimeMillis() - start));
		return jmeterLogSummarylist;
	}
}
