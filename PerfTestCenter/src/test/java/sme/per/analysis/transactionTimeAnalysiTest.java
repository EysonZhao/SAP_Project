package sme.per.analysis;


import java.util.List;

import org.junit.Test;

import sme.perf.analysis.dao.TransactionTimeAnalysisDao;
import sme.perf.analysis.entity.Transactiontimeanalysis;
import sme.perf.dao.GenericDao;
import sme.perf.entity.Host;

public class transactionTimeAnalysiTest {
	@Test
	public void test() {

		TransactionTimeAnalysisDao dao = new TransactionTimeAnalysisDao();
//		List<Transactiontimeanalysis> ls = dao.getAllItems();
		List<Transactiontimeanalysis> ls = dao.getBySessionId(43);
		System.err.println(ls.size());
		for(Transactiontimeanalysis t:ls){
			System.out.println(t.getTransactionName());
		}
	}
}
