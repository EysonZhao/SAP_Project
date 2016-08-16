package sme.perf.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.analysis.dao.AnalysisTransactionDao;
import sme.perf.analysis.dao.ThroughputDao;
import sme.perf.analysis.dao.TransactionTimeAnalysisDao;
import sme.perf.analysis.dao.UserNumbersDao;
import sme.perf.analysis.entity.AnalysisTemplate;
import sme.perf.analysis.entity.AnalysisTransaction;
import sme.perf.analysis.entity.Throughput;
import sme.perf.analysis.entity.ThroughputParameters;
import sme.perf.analysis.entity.Transactiontimeanalysis;
import sme.perf.analysis.entity.UserNumbers;

@RestController
@RequestMapping("/AnalysisTransaction")
public class AnalysisTransactionController {

	AnalysisTransactionDao dao = new AnalysisTransactionDao();
	UserNumbersDao udao = new UserNumbersDao();
	ThroughputDao tdao = new ThroughputDao();
	
	@RequestMapping("/Get/{id}")
	@ResponseBody List<AnalysisTransaction> get(@PathVariable int id){
		List <AnalysisTransaction> list= dao.selectTransactionResult(id);
		if(list.size()==0){
			list=dao.getTransactionResult(id);
			for(AnalysisTransaction it:list){
				dao.add(it);
			}
		}
		return list;
	}
	
	@RequestMapping("/Getfromview/{id}")
	@ResponseBody List<Transactiontimeanalysis> getFromView(@PathVariable int id){
		TransactionTimeAnalysisDao mdao = new TransactionTimeAnalysisDao();
		List <Transactiontimeanalysis> list= mdao.getBySessionId(id);
		return list;
	}
	
	@RequestMapping("/GetsessionIds")
	@ResponseBody List<AnalysisTransaction> getIds(){
		return dao.getAllResultSessionIds();
	}
	
	
	
	@RequestMapping("/Users/{id}")
	@ResponseBody List<UserNumbers> getUsers(@PathVariable int id){
		return udao.getUserNumbers(id);
	}
	
	@RequestMapping("/Throughput")
	@ResponseBody List<Throughput> getThroughput(@RequestBody ThroughputParameters param ){
		return tdao.getThrouphput(param);
	}
	
}
