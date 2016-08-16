package sme.perf.controller;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Category;
import sme.perf.entity.Project;

@RestController
public class CategoryController {
	private GenericDao<Category> dao;
	
	public CategoryController(){
		dao = new GenericDao<Category>(Category.class);
	}

	@RequestMapping("/List")
	@ResponseBody List<Category> list(){
		return dao.getAll();
	}
	
	@RequestMapping("/Get/{categoryId")
	@ResponseBody Category get(@PathVariable long categoryId){
		return dao.getByID(categoryId);
	}
	
	@RequestMapping("/Update/{categoryId}")
	@ResponseBody Category update(@RequestBody Category category){
		return dao.update(category);
	}
	
	@RequestMapping("/Add")
	@ResponseBody Category add(@RequestBody Category category){
		return dao.add(category);
	}
}
