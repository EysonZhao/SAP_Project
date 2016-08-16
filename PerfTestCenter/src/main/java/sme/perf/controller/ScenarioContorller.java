package sme.perf.controller;

import java.util.List;



import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.entity.Scenario;

@RestController
@RequestMapping("/Scenario")
public class ScenarioContorller {
	GenericDao<Scenario> snoDao ;
	
	public ScenarioContorller(){
		snoDao = new GenericDao<Scenario>(Scenario.class);
	}
	
	@RequestMapping("/List")
	public @ResponseBody List<Scenario> list(){
		return snoDao.getAll();
	}
	
	@RequestMapping("/Get/{scenarioId}")
	public @ResponseBody Scenario get(@PathVariable long scenarioId){
		return (Scenario) snoDao.getByID(scenarioId);
	}
	
	@RequestMapping("/List/{projectId}")
	public @ResponseBody List<Scenario> list(@PathVariable long projectId){
		GenericDao<Project> prjDao = new GenericDao<Project>(Project.class);
		Project prj = prjDao.getByID(projectId);
		return prj.getScenarios();
	}
	
	@RequestMapping("/Add")
	public @ResponseBody Scenario add(@RequestBody Scenario scenario){
		return snoDao.add(scenario);
	}
	
	@RequestMapping("/Update")
	public @ResponseBody Scenario update(@RequestBody Scenario scenario){
		return snoDao.update(scenario);
	}
}
