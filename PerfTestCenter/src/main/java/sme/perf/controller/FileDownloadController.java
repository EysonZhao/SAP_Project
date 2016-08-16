package sme.perf.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import sme.perf.execution.dao.ExecutionInfoDao;
import sme.perf.execution.entity.ExecutionInfo;
import sme.perf.utility.FileDownloadHelper;
import sme.perf.utility.LogHelper;
import sme.perf.utility.PropertyFile;
import sme.perf.utility.ZGetLocalServer;

@Controller
@RequestMapping("/Download")
public class FileDownloadController {

    @RequestMapping(value="/Attachment", method = RequestMethod.GET)
    public void downloadAttachmentFile(HttpServletResponse response, 
    		@RequestParam("requestId") long requestId,
    		@RequestParam("fileName") String fileName) throws IOException {
     
    	String fullFileName = PropertyFile.getValue("FileUploadRootPath") 
    			+ File.separator + requestId
    			+ File.separator + fileName;
        File file = new File(fullFileName);
        
        FileDownloadHelper.download(response, file);
    }
    
    @RequestMapping(value="/ExcutionLog/{executionId}", method = RequestMethod.GET)
    public void downLoadLogFile(HttpServletResponse response, 
    		@PathVariable("executionId") long executionId) throws IOException {
    	String fullFileName = PropertyFile.getValue("LoggerPath") 
    			+ File.separator + executionId
    			+ File.separator + "execution.txt";
        File file = new File(fullFileName);
        
        FileDownloadHelper.download(response, file);
    }
    
    @RequestMapping(value="/ExecutionExport/{executionId}", method=RequestMethod.GET)
    public void downloadExecutionExportFile(HttpServletResponse response, 
    		@PathVariable("executionId") long executionId) throws IOException {
    	String fullFileName = PropertyFile.getValue("ExecutionExportPath") 
    			+ File.separator + "ExecutionInfo_" + executionId + ".json";
        new File(PropertyFile.getValue("ExecutionExportPath")).mkdirs();
		ExecutionInfo info = new ExecutionInfoDao().getByID(executionId);
		ObjectMapper mapper = new ObjectMapper();
		try {
			File file = new File(fullFileName);
			mapper.writeValue(file, info);
		} catch (IOException e) {
			LogHelper.error(e);
		}
    	
    	File file = new File(fullFileName);
    	FileDownloadHelper.download(response, file);
    }
    
    @RequestMapping(value="/Report", method=RequestMethod.GET)
    public void SeeReportFile(HttpServletResponse response) throws IOException {
    	DateTime now=new DateTime();
    	String fullFileName =PropertyFile.getValue("ReportPath") 
    			+ File.separator + "CW" + now.getWeekOfWeekyear()+".html";
    	File file = new File(fullFileName);
    	FileDownloadHelper.download(response, file);
    }
    
    @RequestMapping(value="/GetReport", method=RequestMethod.GET)
    public void downloadReportFile(HttpServletResponse response) throws IOException {
    	DateTime now=new DateTime();
    	String fullFileName =PropertyFile.getValue("ReportPath") 
    			+ File.separator + "CW" + now.getWeekOfWeekyear()+".html";

    	File file = new File(fullFileName);
    	FileDownloadHelper.downloaddirectly(response, file);
    }

}
