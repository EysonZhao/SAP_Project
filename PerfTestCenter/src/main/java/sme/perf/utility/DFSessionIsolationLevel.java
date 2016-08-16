package sme.perf.utility;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionCustomizer;

public class DFSessionIsolationLevel implements SessionCustomizer {

	@Override
	public void customize(Session session) throws Exception {
		DatabaseLogin databaseLogin = (DatabaseLogin) session.getDatasourceLogin();
        databaseLogin.setTransactionIsolation(DatabaseLogin.TRANSACTION_READ_UNCOMMITTED);
	}

}
