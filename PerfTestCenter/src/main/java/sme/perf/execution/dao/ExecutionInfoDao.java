package sme.perf.execution.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import sme.perf.dao.GenericDao;
import sme.perf.execution.State;
import sme.perf.execution.entity.ExecutionFilterParameter;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.execution.entity.ExecutionTaskInfo;
import sme.perf.execution.impl.ExecutionTaskInfoSorter;
import sme.perf.task.TaskParameterMap;

public class ExecutionInfoDao extends GenericDao<ExecutionInfo> {
	@Override 
	public ExecutionInfo update(ExecutionInfo execInfo){
		ExecutionTaskInfoDao execTaskInfoDao = new ExecutionTaskInfoDao();
		//delete unncessary ones
		List<ExecutionTaskInfo> existingExecTaskInfoList = execTaskInfoDao.getListByExecutionInfoId(execInfo.getId());
		existingExecTaskInfoList.removeAll(execInfo.getTasks());	//after remove all, the left ones need to be deleted
		execTaskInfoDao.deleteList(existingExecTaskInfoList);
		
		execInfo = super.update(execInfo);
		return getByID(execInfo.getId());
	}
	
	@Override
	public ExecutionInfo getByID(long id){
		ExecutionInfo execInfo = super.getByID(id);
		
		List<ExecutionTaskInfo> sortedList = new ArrayList<ExecutionTaskInfo> (execInfo.getTasks());
		sortedList = sortExecutionTaskInfoTree(sortedList);
		execInfo.setTasks(sortedList);
		return execInfo;
	}
	
	public List<ExecutionInfo> listFinished(){
		String strSQL = "SELECT e From ExecutionInfo e WHERE e.state = ?1";
		EntityManager em = this.getEntityManager();
		TypedQuery query = em.createQuery(strSQL, ExecutionInfo.class);
		query.setParameter(1, State.Finished);
		return query.getResultList();
	}
	
	public List<ExecutionInfo> filter(ExecutionFilterParameter param){
		String strSQL = "SELECT e from ExecutionInfo e WHERE e.state in " + param.getState() + 
				" and (e." + param.getDateType() +" between ?1 and ?2 )";
		EntityManager em = this.getEntityManager();
		TypedQuery query = em.createQuery(strSQL, ExecutionInfo.class);
		query.setParameter(1, param.getFrom());
		query.setParameter(2, param.getTo());
		return query.getResultList();
	}

	
	//make sure the root tasks are the parentId==0, and sort the tree nodes
	private ExecutionInfo sortExecutionTaskInfoTree(ExecutionInfo execInfo) {
		execInfo.setTasks(sortExecutionTaskInfoTree(execInfo.getTasks()));
		return execInfo;
	}
	
	private List<ExecutionTaskInfo> sortExecutionTaskInfoTree(List<ExecutionTaskInfo> execTaskInfoList){
		if(null != execTaskInfoList && execTaskInfoList.size() >= 1){
			// as the default List retrieved from DB is not support to be sorted, so need to duplicate into another list to sort
			List<ExecutionTaskInfo> infoList = new ArrayList<ExecutionTaskInfo>(execTaskInfoList);
			infoList.sort(new ExecutionTaskInfoSorter());
			for(ExecutionTaskInfo taskInfo: infoList){
				if(taskInfo.getSubExecutionTaskInfoList() != null && taskInfo.getSubExecutionTaskInfoList().size() >= 1){
					taskInfo.setSubExecutionTaskInfoList(sortExecutionTaskInfoTree(taskInfo.getSubExecutionTaskInfoList()));
				}
			}
			return infoList;
		}
		return null;
	}
	
}
