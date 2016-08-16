package sme.perf.execution.impl;

import java.util.Comparator;
import sme.perf.execution.entity.ExecutionTaskInfo;

public class ExecutionTaskInfoSorter implements Comparator<ExecutionTaskInfo> {

	@Override
	public int compare(ExecutionTaskInfo o1, ExecutionTaskInfo o2) {
		return (int) (o1.getSn() - o2.getSn());
	}

}
