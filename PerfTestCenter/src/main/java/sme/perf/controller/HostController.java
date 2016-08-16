package sme.perf.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.dao.GenericDao;
import sme.perf.entity.Host;

@RestController
@RequestMapping("/Host")
public class HostController {
	GenericDao<Host> hostDao ;
	
	public HostController(){
		hostDao = new GenericDao<Host>(Host.class);
	}
	
	@RequestMapping("/List")
	public @ResponseBody List<Host> list(){
		return hostDao.getAll();
	}
	
	@RequestMapping("/Get/{hostId}")
	public @ResponseBody Host get(@PathVariable long hostId){
		return (Host) hostDao.getByID(hostId);
	}
	
	@RequestMapping("/Add")
	public @ResponseBody Host add(@RequestBody Host host){
		return hostDao.add(host);
	}
	
	@RequestMapping("/Update")
	public @ResponseBody Host update(@RequestBody Host host){
		return hostDao.update(host);
	}
}