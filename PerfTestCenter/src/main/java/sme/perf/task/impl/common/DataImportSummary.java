package sme.perf.task.impl.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataImportSummary {
	boolean isSuccess;
	long resultSessionId;
	private String resultSessionName;
	long recordCount;

}
