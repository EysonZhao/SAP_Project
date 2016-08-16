package sme.perf.request.dao;

import org.junit.Test;

import sme.perf.dao.GenericRawDataDao;
import sme.perf.request.entity.Central_Codeline;
import sme.perf.requst.dao.CodelineDao;

public class CodelineTest {
	@Test
	public void test() {
		CodelineDao dao = new CodelineDao();
		
		
		System.out.println(dao.getAll());
	}
}
