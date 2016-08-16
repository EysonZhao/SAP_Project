package sme.perf.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import sme.perf.dao.GenericDao;
import sme.perf.entity.Project;
import sme.perf.request.entity.ProjectStatusReport;
import sme.perf.request.entity.StatusReport;
import sme.perf.request.entity.StatusReportItem;
import sme.perf.request.entity.ZIssueFinalReport;
import sme.perf.request.entity.ZIssueFinalReportItem;
import sme.perf.requst.dao.StatusReportDao;
import sme.perf.requst.dao.TestIssueDao;
import sme.perf.requst.dao.ZIssueFinalReportDao;

public class ZGenerateReport {

	private static String Headline2(String name){
		return " <p class=headline2>"+ name +"</p>\r\n<p><span style='font-size:11.0pt'>&nbsp;</span></p>\r\n";
	}
	public static void generateReport(String fullFileName,DateTime from,DateTime now) throws UnknownHostException{
		//get the host ip and port info
		String AddrAndPort = ZGetLocalServer.getAddr()+ ":" +ZGetLocalServer.getPort(); 

		String ContentHead = "  <div class=MsoNormal align=center style='text-align:center'>"
				+ "<hr size=2 width=\"100%\" align=center></div>\r\n";
		String BackToTop = "<p align=right style='text-align:right'><a href=\"#Top_e\">Back to top</a></p>\r\n";
		String EmptyLine = "<p><span style='font-size:11.0pt'>&nbsp;</span></p>\r\n";
		String ItemFormat1 = "<p style='margin-left:.5in;text-indent:-.25in'><span style='font-family:Symbol'>";
		String Image1="<img width=91 height=73 id= Picture 1 src= \"http://"+ AddrAndPort +"/Src/Logo\" alt=\"SAP Logo\">\r\n";
		String AfterImage1="</p>\r\n</td>\r\n<td width=530 style='width:397.5pt;padding:0in 0in 0in 0in;height:.75in'>\r\n"+
    "<p class=MsoNormal style='margin-bottom:10.0pt;line-height:115%'>\r\n<span class=header1><span style='font-size:13.0pt;line-height:115%;font-weight:"+
    "normal'>SME Performance Test Service</span></span><b><br> </b>\r\n";
		
      	String BeforeImage2="</td>\r\n</tr>\r\n</table>\r\n</td>\r\n</tr>\r\n"+
 "<tr style='height:150.0pt'>\r\n<td width=507 style='width:380.25pt;padding:0in 0in 0in 0in;height:150.0pt'>\r\n"+
  "<p class=MsoNormal style='margin-bottom:10.0pt;line-height:115%'>\r\n"+
  "<span style='font-size:12.0pt;line-height:115%;color:#222222'>\r\n";
      	
		String Image2="<img width=633 height=200 id=\"Picture 2\" src=\"http://" + AddrAndPort + "/Src/BackgroundImg\""+
  "alt=\"Keyvisual_alternate_7\"></span></p>\r\n</p>\r\n</td>\r\n</tr>\r\n";
		
		String ItemFormat2 = "<span style='font:7.0pt \"Times New Roman\"'>\r\n &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span style='background:white'>";
		String NewStatus = "<span style='font-weight:700'>  NEW </span>";
		
		//Generate the CW id automatically
		int weekNo=new DateTime().getWeekOfWeekyear();
		
		new File(PropertyFile.getValue("ReportPath")).mkdirs();
		FileInputStream fis;
		  FileOutputStream fos;
		  BufferedInputStream bis;
		  BufferedOutputStream bos;
		  int i;
		  
		  //Generate the <head> part of the HTML-report 
		  try
		  {
		   fis=new FileInputStream(PropertyFile.getValue("ReportPath")+"/template/1_head.txt");
		      bis=new BufferedInputStream(fis);   
		      fos=new FileOutputStream(fullFileName);
		      bos=new BufferedOutputStream(fos);  
		      i=bis.read();     
		      while(i!=-1)
		      {
		       bos.write(i);       
		       bos.flush();      
		       i=bis.read();
		      }
		    
		      fis.close();
		      fos.close();
		      bis.close();
		      bos.close();
		   }
		   catch(IOException e)
		   {
		    System.out.println("do not find the file");
		   }
		  try
		  {
		      fos=new FileOutputStream(fullFileName,true); 
		      bos=new BufferedOutputStream(fos);  
		      
		      bos.write(Image1.getBytes());
		      bos.flush();
		      bos.write(AfterImage1.getBytes());		
		      bos.flush();
		      bos.write(("<span class=subheader1><span style='font-size:13.0pt;line-height:115%'>CW " + weekNo + " Status Update</span></span></p>\r\n").getBytes());
		      bos.flush();
		      bos.write(BeforeImage2.getBytes());
		      bos.flush();
		      bos.write(Image2.getBytes());
		      bos.flush();
		      
		      fos.close();
		      bos.close();
		   }
		   catch(IOException e)
		   {
		    System.out.println("do not find the file");
		   }
		  
		  /***
		   * Generate the content from DB by using SQL  
		   */
		  // get the Status and Plan part

			StatusReport report = new StatusReport();
			report.setEndDate(now);
			report.setStartDate(from);
			report.setProjectStatusReportList(new ArrayList<ProjectStatusReport>());
			StatusReportDao reportDao = new StatusReportDao();
			GenericDao<Project> projectDao = new GenericDao<Project>(Project.class);
			List<Project> projectList = projectDao.getAll();
			TestIssueDao issueDao = new TestIssueDao();
			
			for(Project project: projectList){
				ProjectStatusReport projectReport = new ProjectStatusReport();
				projectReport.setActiveTestRequests(reportDao.getProjectActiveRequests(from, now, project.getId()));
				projectReport.setBlockedTestRequests(reportDao.getProjectBlockedRequests(project.getId()));
				projectReport.setPlanTestRequests(reportDao.getProjectPlanRequests(from, now, project.getId()));
				projectReport.setProjectName(project.getName());
				projectReport.setIssues(issueDao.getProjectIssuesByCreateDate(project.getId(), from, now));
				if((null != projectReport.getActiveTestRequests() && projectReport.getActiveTestRequests().size() > 0) ||
					(null != projectReport.getBlockedTestRequests() && projectReport.getBlockedTestRequests().size() > 0) ||
					(null != projectReport.getIssues() && projectReport.getIssues().size() > 0) ||
					(null != projectReport.getPlanTestRequests() && projectReport.getPlanTestRequests().size() > 0)){
					report.getProjectStatusReportList().add(projectReport);
				}
			}
			List<ProjectStatusReport> statusreport = report.getProjectStatusReportList();
  
		  // get the ISSUE content			
			List<ZIssueFinalReport> issueReportList=new ArrayList<ZIssueFinalReport>();

			for(Project project:projectList){
				ZIssueFinalReport issueReport=new ZIssueFinalReport();
				issueReport.setNearlySubmittedList(new ZIssueFinalReportDao().getLatestReportIssue(project.getId(),from,now,"%"));
				issueReport.setOlderSubmittedList(new ZIssueFinalReportDao().getOlderReportIssue(project.getId(),90,15));
				issueReport.setProjectName(project.getName());
				issueReportList.add(issueReport);
			}		
			
			/***
			 * Generate the HTML Report Content Part
			 */ 		  
		  try
		  {
		   fis=new FileInputStream(PropertyFile.getValue("ReportPath")+"/template/2_body_table.txt");
		      bis=new BufferedInputStream(fis);   
		      fos=new FileOutputStream(fullFileName,true);
		      bos=new BufferedOutputStream(fos);  
		      i=bis.read();     
		      while(i!=-1)
		      {
		       bos.write(i);       
		       bos.flush();      
		       i=bis.read();
		      }
		      fis.close();
		      fos.close();
		      bis.close();
		      bos.close();
		   }
		   catch(IOException e)
		   {
		    System.out.println("do not find the file");
		   }
			//write the content to the file
		  try
		  {
		      fos=new FileOutputStream(fullFileName,true);
		      bos=new BufferedOutputStream(fos);  
		      fos.write(("\r\n<p class=edition>CW"+weekNo+"</p>\r\n").getBytes());
		/**
		 * BA Part      
		 */
		      fos.write(ContentHead.getBytes());
		      fos.write("<p class=headline1><a name=\"Browse_Access\"></a>Browser access</p>\r\n".getBytes());
		      fos.write(Headline2("Status").getBytes());
		      for(ProjectStatusReport status:statusreport){
		    	  //Use regular expression to match the project name
		    	  Pattern pattern = Pattern.compile("^BA.*");
		    	  Matcher matcher = pattern.matcher(status.getProjectName());
		    	  if(matcher.matches()){
		    		  List<StatusReportItem> blocked = status.getBlockedTestRequests();
		    		  List<StatusReportItem> active = status.getActiveTestRequests();
		    		  
		    	  for(StatusReportItem item:blocked){
		    			 fos.write(ItemFormat1.getBytes());
		    			 fos.write(183);
		    			 fos.write((ItemFormat2+item.getTestRequestName()+" is " + "<span style='font-weight:600'>blocked</span>\r\n").getBytes());
		    		  		    		 
		    	   }
		    	  for(StatusReportItem item:active){
		    			 fos.write(ItemFormat1.getBytes());
		    			 fos.write(183);
			    			 fos.write((ItemFormat2+item.getTestRequestName()+" is " + "<span style='font-weight:600'>"+ 
			    			 item.getLatestStatus() +"</span>\r\n").getBytes());
			    		  		    		 
			       }
		    	  }
		      }//end for status
		      fos.write(Headline2("Plan").getBytes());
		      for(ProjectStatusReport status:statusreport){
		    	  Pattern pattern = Pattern.compile("^BA.*");
		    	  Matcher matcher = pattern.matcher(status.getProjectName());
		    	  if(matcher.matches()){
		    		  List<StatusReportItem> plan = status.getPlanTestRequests();
		    		  
		    	  for(StatusReportItem item:plan){
		    		String priority = item.getPriority();
		    		if(priority.equals("Very High")){
		    			 priority="<span style='font-weight:600'>Very High</span>";
		    		}
	    			 fos.write(ItemFormat1.getBytes());
	    			 fos.write(183);
		    			 fos.write((ItemFormat2+item.getTestRequestName()+" ["+ priority +"]\r\n").getBytes());		    		  		    		 
		    	   }
		      }
		      }
		      fos.write(Headline2("Issue").getBytes());
		      for(ZIssueFinalReport issueReport:issueReportList){
		    	  Pattern pattern = Pattern.compile("^BA.*");
		    	  Matcher matcher = pattern.matcher(issueReport.getProjectName());
		    	  if(matcher.matches()){
		    		  List<ZIssueFinalReportItem> nearlyList= issueReport.getNearlySubmittedList();
		    		  List<ZIssueFinalReportItem> olderList= issueReport.getOlderSubmittedList();
		    		  
		    		  for(ZIssueFinalReportItem item:nearlyList){
		    			  String priority = item.getPriority();
		    			  if(priority.equals("Very High")){
		    				  priority="<span style='font-weight:600'>Very High</span>";
		    			  }
			    			 fos.write(ItemFormat1.getBytes());
			    			 fos.write(183);
		    			  fos.write((ItemFormat2+"["+item.getJiraKey()+"] : "+item.getTitle()+" ("+priority +
		    					  ")" + NewStatus +"\r\n").getBytes());
		    		  }
		    		  for(ZIssueFinalReportItem item:olderList){
			    			 fos.write(ItemFormat1.getBytes());
			    			 fos.write(183);
		    			  fos.write((ItemFormat2+"["+item.getJiraKey()+"] : "+item.getTitle()+" ("+item.getPriority() +")\r\n").getBytes());
		    		  }
		    	  }
		      }
		      fos.write(BackToTop.getBytes());
				/**
				 * Anywhere Part      
				 */
		      fos.write(ContentHead.getBytes());
		      fos.flush();
		      fos.write("<p class=headline1><a name=\"Anywhere\"></a>Anywhere</p>\r\n".getBytes());
		      fos.write(Headline2("Status").getBytes());
		      for(ProjectStatusReport status:statusreport){
		    	  if(status.getProjectName().equals("Anywhere_2.0")){
		    		  List<StatusReportItem> blocked = status.getBlockedTestRequests();
		    		  List<StatusReportItem> active = status.getActiveTestRequests();
		    		  
		    	  for(StatusReportItem item:blocked){
		    			 fos.write(ItemFormat1.getBytes());
		    			 fos.write(183);
		    			 fos.write((ItemFormat2+item.getTestRequestName()+" is " + "<span style='font-weight:600'>blocked</span>\r\n").getBytes());
		    		  		    		 
		    	   }
		    	  for(StatusReportItem item:active){
		    			 fos.write(ItemFormat1.getBytes());
		    			 fos.write(183);
			    			 fos.write((ItemFormat2+item.getTestRequestName()+" is " + "<span style='font-weight:600'>"+ 
			    			 item.getLatestStatus() +"</span>\r\n").getBytes());
			    				    		 
			       }
		      }
		      }//end for status
		      fos.write(Headline2("Plan").getBytes());
		      for(ProjectStatusReport status:statusreport){
		    	  if(status.getProjectName().equals("Anywhere_2.0")){
		    		  List<StatusReportItem> plan = status.getPlanTestRequests();
		    		  
		    	  for(StatusReportItem item:plan){
		    		String priority = item.getPriority();
		    		if(priority.equals("Very High")){
		    			 priority="<span style='font-weight:600'>Very High</span>";
		    		}
	    			 fos.write(ItemFormat1.getBytes());
	    			 fos.write(183);
		    		fos.write((ItemFormat2+item.getTestRequestName()+" ["+ priority +"]\r\n").getBytes());
		    	   }
		      }
		      }
		      fos.write(Headline2("Issue").getBytes());
		      for(ZIssueFinalReport issueReport:issueReportList){
		    	  if(issueReport.getProjectName().equals("Anywhere_2.0")){
		    		  List<ZIssueFinalReportItem> nearlyList= issueReport.getNearlySubmittedList();
		    		  List<ZIssueFinalReportItem> olderList= issueReport.getOlderSubmittedList();
		    		  
		    		  for(ZIssueFinalReportItem item:nearlyList){
		    			  String priority = item.getPriority();
		    			  if(priority.equals("Very High")){
		    				  priority="<span style='font-weight:600'>Very High</span>";
		    			  }
			    			 fos.write(ItemFormat1.getBytes());
			    			 fos.write(183);
		    			  fos.write((ItemFormat2+"["+item.getJiraKey()+"] : "+item.getTitle()+" ("+priority +
		    					  ")" + NewStatus +"\r\n").getBytes());
		    		  }
		    		  for(ZIssueFinalReportItem item:olderList){
			    			 fos.write(ItemFormat1.getBytes());
			    			 fos.write(183);
		    			  fos.write((ItemFormat2+"["+item.getJiraKey()+"] : "+item.getTitle()+" ("+item.getPriority() +")\r\n").getBytes());
		    		  }
		    	  }
		      }
		      fos.write(BackToTop.getBytes());
		      
				/**
				 * B1 Part      
				 */
		      fos.write(ContentHead.getBytes());
		      fos.write("<p class=headline1><a name=\"B1_On_Premise\"></a>B1</p>\r\n".getBytes());
		      fos.write(Headline2("Status").getBytes());
		      for(ProjectStatusReport status:statusreport){
		    	  Pattern pattern = Pattern.compile("^B1.*");
		    	  Matcher matcher = pattern.matcher(status.getProjectName());
		    	  if(matcher.matches()){
		    		  List<StatusReportItem> blocked = status.getBlockedTestRequests();
		    		  List<StatusReportItem> active = status.getActiveTestRequests();
		    		  
		    	  for(StatusReportItem item:blocked){
		    		String priority = item.getPriority();
		    		if(priority.equals("Very High")){
		    			 priority="<span style='font-weight:600'>Very High</span>";
		    		}
	    			 fos.write(ItemFormat1.getBytes());
	    			 fos.write(183);
	    			 fos.write((ItemFormat2+item.getTestRequestName()+" is " + "<span style='font-weight:600'>Blocked</span>\r\n").getBytes());
		    		    		 
		    	   }
		    	  for(StatusReportItem item:active){
			    		String priority = item.getPriority();
			    		if(priority.equals("Very High")){
			    			 priority="<span style='font-weight:600'>Very High</span>";
			    		}
		    			 fos.write(ItemFormat1.getBytes());
		    			 fos.write(183);
			    			 fos.write((ItemFormat2+item.getTestRequestName()+" is " + "<span style='font-weight:600'>"+ 
			    			 item.getLatestStatus() +"</span>\r\n").getBytes());		    		 
			       }
		      }
		      }//end for status
		      fos.write(Headline2("Plan").getBytes());
		      for(ProjectStatusReport status:statusreport){
		    	  Pattern pattern = Pattern.compile("^B1.*");
		    	  Matcher matcher = pattern.matcher(status.getProjectName());
		    	  if(matcher.matches()){
		    		  List<StatusReportItem> plan = status.getPlanTestRequests();
		    		  
		    	  for(StatusReportItem item:plan){
		    		String priority = item.getPriority();
		    		if(priority.equals("Very High")){
		    			 priority="<span style='font-weight:600'>Very High</span>";
		    		}
	    			fos.write(ItemFormat1.getBytes());
	    			fos.write(183);
		    		fos.write((ItemFormat2+item.getTestRequestName()+" ["+ priority +"]\r\n").getBytes());		    		  		    		 
		    	   }
		      }
		      }
		      fos.write(Headline2("Issue").getBytes());
		      for(ZIssueFinalReport issueReport:issueReportList){
		    	  Pattern pattern = Pattern.compile("^B1.*");
		    	  Matcher matcher = pattern.matcher(issueReport.getProjectName());
		    	  if(matcher.matches()){
		    		  List<ZIssueFinalReportItem> nearlyList= issueReport.getNearlySubmittedList();
		    		  List<ZIssueFinalReportItem> olderList= issueReport.getOlderSubmittedList();
		    		  
		    		  for(ZIssueFinalReportItem item:nearlyList){
		    			  String priority = item.getPriority();
		    			  if(priority.equals("Very High")){
		    				  priority="<span style='font-weight:600'>Very High</span>";
		    			  }
			    			 fos.write(ItemFormat1.getBytes());
			    			 fos.write(183);
		    			  fos.write((ItemFormat2+"["+item.getJiraKey()+"] : "+item.getTitle()+" ("+priority +
		    					  ")" + NewStatus +"\r\n").getBytes());
		    		  }
		    		  for(ZIssueFinalReportItem item:olderList){
			    			 fos.write(ItemFormat1.getBytes());
			    			 fos.write(183);
		    			  fos.write((ItemFormat2+"["+item.getJiraKey()+"] : "+item.getTitle()+" ("+item.getPriority() +")\r\n").getBytes());
		    		  }
		    	  }
		      }
		      fos.write(BackToTop.getBytes());		      

		      fos.close();	
		      bos.close();
		   }
		   catch(IOException e)
		   {
		    System.out.println("do not find the file");
		   }
		  try
		  {
		   fis=new FileInputStream( PropertyFile.getValue("ReportPath")+"/template/3_end.txt");
		      bis=new BufferedInputStream(fis);   
		      fos=new FileOutputStream(fullFileName,true);
		      bos=new BufferedOutputStream(fos);  
		      i=bis.read();     
		      while(i!=-1)
		      {
		       bos.write(i);       
		       bos.flush();      
		       i=bis.read();
		      }
		      fis.close();
		      fos.close();
		      bis.close();
		      bos.close();
		   }
		   catch(IOException e)
		   {
		    System.out.println("do not find the file");
		   }
	}
}
