package sme.perf.utility;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/Src")
public class ImageResouce {
    //for the test
    
    @RequestMapping(value="/Logo", method=RequestMethod.GET)
    public void getLogo(HttpServletResponse response) throws IOException {
    	String fullFileName =PropertyFile.getValue("ReportPath") 
    			+ File.separator + "pics" + File.separator + "image001.jpg";
    	File file = new File(fullFileName);
    	FileDownloadHelper.download(response, file);
    }
    @RequestMapping(value="/BackgroundImg", method=RequestMethod.GET)
    public void getBackgroundImg(HttpServletResponse response) throws IOException {
    	String fullFileName =PropertyFile.getValue("ReportPath") 
    			+ File.separator + "pics" + File.separator + "image002.jpg";
    	File file = new File(fullFileName);
    	FileDownloadHelper.download(response, file);
    }
}
