package sme.perf.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import sme.perf.analysis.entity.AnalysisTemplate;
import sme.perf.dao.GenericDao;
import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.request.entity.Attachment;
import sme.perf.request.entity.TestRequest;
import sme.perf.requst.dao.AttachmentDao;
import sme.perf.utility.FileUploadHelper;
import sme.perf.utility.PropertyFile;

@Controller
@RequestMapping("/Upload")
public class FileUploadController {
	//older method used by html
	@RequestMapping(value="/ExecutionResult", method=RequestMethod.POST)
	public @ResponseBody String executionResultUpload(@RequestParam("executionId") long executionId,
            @RequestParam("file") MultipartFile file){
		String fileName = file.getOriginalFilename();
        if (!file.isEmpty()) {
            try {
            	String fileRootPathStr = PropertyFile.getValue("ExecutionImportPath");
            	File filePath = new File(fileRootPathStr);            	
            	FileUploadHelper.upload(file,  filePath);
                
                ExecutionInfo execInfo = new ObjectMapper().readValue(new File(filePath + File.separator + fileName), ExecutionInfo.class);
                new ExecutionInfoDao().update(execInfo);
                
                return "Execution Result is successfully uploaded " + fileName + "!";
            } catch (Exception e) {
                return "Execution Result is failed to upload " + fileName + " => " + e.getMessage();
            }
        } else {
            return "Execution Result failed to upload " + fileName + " because the file was empty.";
        }
    }
	 @RequestMapping("/ExecutionResult/{id}")
		public @ResponseBody String executionResultUpload(MultipartHttpServletRequest request, HttpServletResponse response, @PathVariable long id) {                 
			Iterator<String> itFileName = request.getFileNames();
			String fileName;
			while(itFileName.hasNext()){
				fileName = itFileName.next();
				MultipartFile file = request.getFile(fileName);
		        if (!file.isEmpty()) {
		            try {
		            	String fileRootPathStr = PropertyFile.getValue("ExecutionImportPath");
		            	File filePath = new File(fileRootPathStr);            	
		            	FileUploadHelper.upload(file,  filePath);
		                
		                ExecutionInfo execInfo = new ObjectMapper().readValue(new File(filePath + File.separator + fileName), ExecutionInfo.class);
		                new ExecutionInfoDao().update(execInfo);
		                
		                return "Execution Result is successfully uploaded " + fileName + "!";
		                }
		              catch (Exception e) {
		                return "Failed to upload " + fileName + " => " + e.getMessage();
		            }
		        } else {
		            return "Failed to upload " + fileName + " because the file was empty.";
		        }
			}
			return "Successfully uploaded the file!";
		}
	    
	
	
	//older method used by html
    @RequestMapping(value="/TestRequestAttachment", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("requestId") long requestId,
            @RequestParam("file") MultipartFile file){
    	String fileName = file.getOriginalFilename();
        if (!file.isEmpty()) {
            try {
            	String fileRootPathStr = PropertyFile.getValue("FileUploadRootPath");
            	File fileRootPath = new File(fileRootPathStr);
            	File testRequestAttachmentPath = new File(fileRootPath.getAbsolutePath() + File.separator + requestId);
            	
            	FileUploadHelper.upload(file, testRequestAttachmentPath);
                
                //if the record exists, update the date. else add a new record
                AttachmentDao attachmentDao = new AttachmentDao();
                Attachment attachment = attachmentDao.getByNotId(requestId, fileName);
                if(attachment == null){
	                attachment = new Attachment();
	                attachment.setFileName(fileName);
	                attachment.setTestRequestId(requestId);
	                attachment.setUploadDate(new Timestamp((System.currentTimeMillis())));
	                attachmentDao.add(attachment);
                }
                else{
                	attachment.setUploadDate(new Timestamp((System.currentTimeMillis())));
                	attachmentDao.update(attachment);
                }
                
                return "You successfully uploaded " + fileName + "!";
            } catch (Exception e) {
                return "You failed to upload " + fileName + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + fileName + " because the file was empty.";
        }
    }
    @RequestMapping("/TestRequestAttachment/{id}")
	public @ResponseBody String upload(MultipartHttpServletRequest request, HttpServletResponse response, @PathVariable long id) {                 
		Iterator<String> itFileName = request.getFileNames();
		String fileName;
		while(itFileName.hasNext()){
			fileName = itFileName.next();
			MultipartFile file = request.getFile(fileName);
	        if (!file.isEmpty()) {
	            try {
	            	String fileRootPathStr = PropertyFile.getValue("FileUploadRootPath");
	            	File fileRootPath = new File(fileRootPathStr);
	            	File testRequestAttachmentPath = new File(fileRootPath.getAbsolutePath() + File.separator + id);
	            	
	            	FileUploadHelper.upload(file, testRequestAttachmentPath);
	                //if the record exists, update the date. else add a new record
	                AttachmentDao attachmentDao = new AttachmentDao();
	                Attachment attachment = attachmentDao.getByNotId(id, fileName);
	                if(attachment == null){
		                attachment = new Attachment();
		                attachment.setFileName(file.getOriginalFilename());
		                attachment.setTestRequestId(id);
		                attachment.setUploadDate(new Timestamp((System.currentTimeMillis())));
		                attachmentDao.add(attachment);
	                }
	                else{
	                	attachment.setUploadDate(new Timestamp((System.currentTimeMillis())));
	                	attachmentDao.update(attachment);
	                }
	                
	            } catch (Exception e) {
	                return "Failed to upload " + fileName + " => " + e.getMessage();
	            }
	        } else {
	            return "Failed to upload " + fileName + " because the file was empty.";
	        }
		}
		return "Successfully uploaded the file!";
	}
    
//    @RequestMapping(value="/TestRequestAttachment/{id}", method=RequestMethod.GET)
//    public @ResponseBody String handleFileUpload(){
//        return "You could upload attachment for test request";
//    }
}
