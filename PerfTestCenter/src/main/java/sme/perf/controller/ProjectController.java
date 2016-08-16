package sme.perf.controller;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;


@RestController
@RequestMapping("/Project")
public class ProjectController {
	GenericDao<Project> dao;
	
	public ProjectController(){
		dao = new GenericDao<Project>(Project.class);
	}
	
	@RequestMapping("/List")
	@ResponseBody List<Project> list(){
		return dao.getAll();
	}
	
	@RequestMapping("/Get/{projectId}")
	@ResponseBody Project get(@PathVariable long projectId){
		return dao.getByID(projectId);
	}
	
	@RequestMapping("/Update")
	@ResponseBody Project update(@RequestBody Project project){
		return dao.update(project);
	}
	
	@RequestMapping("/Add")
	@ResponseBody Project add(@RequestBody Project project){
		return dao.add(project);
	}
}
