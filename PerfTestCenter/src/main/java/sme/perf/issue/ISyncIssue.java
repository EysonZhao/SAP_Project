package sme.perf.issue;

import java.io.IOException;

import sme.perf.request.entity.TestIssue;

public interface ISyncIssue {
	public TestIssue sync(TestIssue issue) throws IOException;
}
