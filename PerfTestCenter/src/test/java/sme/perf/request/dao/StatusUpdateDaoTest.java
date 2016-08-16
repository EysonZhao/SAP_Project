package sme.perf.request.dao;

import static org.junit.Assert.*;

import org.junit.Test;

import sme.perf.request.entity.StatusUpdate;
import sme.perf.requst.dao.StatusUpdateDao;

public class StatusUpdateDaoTest {

	@Test
	public void test() {
		StatusUpdateDao dao = new StatusUpdateDao();
		StatusUpdate update = dao.getLatestStatus(1);
	}

}
