package sme.perf.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import sme.perf.analysis.entity.AnalysisTemplate;
import sme.perf.dao.GenericDao;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.utility.FileDownloadHelper;
import sme.perf.utility.FileUploadHelper;
import sme.perf.utility.LogHelper;
import sme.perf.utility.PropertyFile;

@RestController
@RequestMapping("/AnalysisTemplate")
public class AnalysisTemplateController {
	
	GenericDao dao = new GenericDao<AnalysisTemplate>(AnalysisTemplate.class);

	@RequestMapping("/List")
	@ResponseBody List<AnalysisTemplate> list(){
		return dao.getAll();
	}
	
	@RequestMapping("/Get/{id}")
	@ResponseBody AnalysisTemplate get(@PathVariable long id){
		return (AnalysisTemplate) dao.getByID(id);
	}
	
	@RequestMapping("/Add")
	@ResponseBody AnalysisTemplate add(@RequestBody AnalysisTemplate newTemplate){
		return (AnalysisTemplate) dao.add(newTemplate);
	}
	
	@RequestMapping("/Update")
	@ResponseBody AnalysisTemplate update(@RequestBody AnalysisTemplate newTemplate){
		return (AnalysisTemplate) dao.update(newTemplate);
	}
	
	@RequestMapping("/Upload/{id}")
	public @ResponseBody String upload(MultipartHttpServletRequest request, HttpServletResponse response, @PathVariable long id) {                 
		Iterator<String> itFileName = request.getFileNames();
		String fileName;
		int nFile = 0;
		AnalysisTemplate template = (AnalysisTemplate) dao.getByID(id);
		while(itFileName.hasNext()){
			fileName = itFileName.next();
			MultipartFile file = request.getFile(fileName);
	        if (!file.isEmpty()) {
	            try {
	            	String fileRootPathStr = PropertyFile.getValue("AnalysisTemplateFolder");
	            	File filePath = new File(fileRootPathStr + File.separator + id);            	
	            	FileUploadHelper.upload(file, filePath);
	            	template.setFileName(file.getOriginalFilename());
	            	dao.update(template);
	            } catch (Exception e) {
	                return "Failed to upload " + fileName + " => " + e.getMessage();
	            }
	        } else {
	            return "Failed to upload " + fileName + " because the file was empty.";
	        }
		}
		return "Successfully uploaded " + nFile + "file(s)!";
	}
	
	@RequestMapping("/Download/{id}")
	public void download(HttpServletResponse response, @PathVariable long id){
		AnalysisTemplate template = (AnalysisTemplate) dao.getByID(id);
    	String fileRootPathStr = PropertyFile.getValue("AnalysisTemplateFolder");
    	File file = new File(fileRootPathStr + File.separator + id + File.separator + template.getFileName());
    	try{
    		FileDownloadHelper.download(response, file);
    	}
    	catch(Exception ex){
    		LogHelper.error(ex);
    	}
	}
}
