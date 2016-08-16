package sme.perf.task.impl;

import java.io.File;
import java.io.IOException;

//import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sme.perf.entity.Host;
import sme.perf.task.Result;
import sme.perf.task.ITask;
import sme.perf.task.Task;
import sme.perf.task.TaskParameterEntry;
import sme.perf.task.TaskParameterMap;
//import sme.perf.task.impl.anywhere.*;
//import sme.perf.task.impl.anywhere.p1601.*;
//import sme.perf.task.impl.anywhere.p1603.*;
import sme.perf.task.impl.anywhere.p1604.*;
import sme.perf.task.impl.common.*;
import sme.perf.utility.LogHelper;
import sme.perf.utility.PropertyFile;

public class AnywhereTaskImplement {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("static-access")
	@Test
	public void ImplementTest() {

//		// //LOCAL SIZING
//		 Map<String, TaskParameterEntry> parameters = new HashMap<String,
//		 TaskParameterEntry>();
//		 //Common
//		parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
//		parameters.put("windowsFolder", new TaskParameterEntry("C:\\WindowsMainFolder", "Windows Main Folder"));
//		parameters.put("repository", new TaskParameterEntry("10.58.113.37:5000", "repository"));
//		parameters.put("instanceBackupPath", new TaskParameterEntry("/usr/sap/hc/dbbackup/tempbackup","Linux Path for Keep Instance Backup File"));
//		parameters.put("initInstanceBackupPath", new TaskParameterEntry("/usr/sap/hc/dbbackup/InitStatus","Linux Path for Keep Init Instance Backup File"));
//		parameters.put("schemaBackupFolder", new TaskParameterEntry("/usr/sap/hc/dbbackup/tempbackup/PERFDB1","Linux Main Folder"));
//		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.121.129:30015", "Full Hana Instance URL"));
//		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
//		parameters.put("hanaUser", new TaskParameterEntry("ndbadm","Hana User Name"));
//		parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_60\\bin\\java.exe","Java exe file location"));
//		parameters.put("remoteSubvolume", new TaskParameterEntry("/opt/glusterfs/brick", "ShareFolder Remote Loacation"));
//		parameters.put("mainResultFolder",new TaskParameterEntry("C:\\WindowsMainFolder\\ResultCollection","Main Result Folder"));
//		parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
//		parameters.put("mariaDBPasswd", new TaskParameterEntry("12345","MariaDB root User Password"));
//		parameters.put("isLocal", new TaskParameterEntry("true","flag for Local/other, only true/false"));
//		parameters.put("domainName", new TaskParameterEntry("pvgl.sap.corp","domainName"));
//				
//		parameters.put("jmeterScriptName",new TaskParameterEntry("OCC_ALL_new_withthread_AllCases_CSM.jmx","Jmeter Script Name"));
//		parameters.put("scenario", new TaskParameterEntry("OCCSizing","Scenario Name"));
//		parameters.put("eshopDispatcherName", new TaskParameterEntry("eshop.pvgl.sap.corp", "Dispatcher Name of Eshop"));
//		parameters.put("dispatcherName", new TaskParameterEntry("occ.pvgl.sap.corp", "Dispatcher Name for OCC"));
//		parameters.put("rumpup", new TaskParameterEntry("100", "Rumpup Time(s)"));
//		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
//		parameters.put("threadNumber", new TaskParameterEntry("800","Total Thread #"));
//		parameters.put("threadNumberList", new TaskParameterEntry("[[\"16\",\"32\"]]","Run Cases Thread Number"));
//		parameters.put("remoteHostList", new TaskParameterEntry("10.58.108.31","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
//		parameters.put("duration", new TaskParameterEntry("500","Test Duration(s)"));
//		parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.108.24:3306", "Full CSM MariaDB URL"));
//		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
//		parameters.put("endSchemaNumber", new TaskParameterEntry("500","End Schema Number"));
//		parameters.put("maxUserNumberPerTenant", new TaskParameterEntry("10","Max User Number per Tenant"));
//		parameters.put("version",new TaskParameterEntry("9.0.0", "OCC version"));
//		parameters.put("addUserThreadNumber",new TaskParameterEntry("5", "Add User Script Thread Number"));
//		parameters.put("startUserNumber", new TaskParameterEntry("1","Start User Number"));	
//		parameters.put("duplicateThreadNumber", new TaskParameterEntry("10","Parrally Duplicate DBTemplate Thread Number"));
//		parameters.put("schemaNumberPerDuplicateThread",new TaskParameterEntry("50","Schema Number Per Duplicate thread"));
//		parameters.put("userNumberPerTenant", new TaskParameterEntry("4","Thread # per Tenant"));
//
//		TaskParameterMap template = new TaskParameterMap();
//		template.setParameters(parameters);
//
//		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
//		List<Host> CSMhostList = new ArrayList<Host>();
//		CSMhostList.add(new Host("cnpvgvb1od040.pvgl.sap.corp", "10.58.9.90","root", "Initial0", "CSM Server"));
//		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,"Host List for CSM,SP,IDP,etc."));
//		List<Host> occhostList = new ArrayList<Host>();
//		//occhostList.add(new Host("cnpvgvb1od034.pvgl.sap.corp", "10.58.9.84","root", "Initial0", "OCC Server 1"));
//		occhostList.add(new Host("cnpvgvb1od035.pvgl.sap.corp", "10.58.9.85","root", "Initial0", "OCC Server 1"));
//		occhostList.add(new Host("cnpvgvb1od036.pvgl.sap.corp", "10.58.9.86","root", "Initial0", "OCC Server 2"));
//		occhostList.add(new Host("cnpvgvb1od037.pvgl.sap.corp", "10.58.9.87","root", "Initial0", "OCC Server 3"));
//		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,"Host List for OCC Machine"));
//		List<Host> jobhostList = new ArrayList<Host>();
//		jobhostList.add(new Host("cnpvgvb1od038.pvgl.sap.corp", "10.58.9.88","root", "Initial0", "Job Server 1"));
//		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,"Host List for Job Machine"));
//		List<Host> eshophostList = new ArrayList<Host>();
////		eshophostList.add(new Host("cnpvgvb1od042.pvgl.sap.corp", "10.58.9.92","root", "Initial0", "Eshop Server"));
//		eshophostList.add(new Host("cnpvgvb1od043.pvgl.sap.corp", "10.58.9.93","root", "Initial0", "Eshop Server 2"));
//		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,"Host List for Eshop Machine"));
//		List<Host> WindowshostList = new ArrayList<Host>();
//		WindowshostList.add(new Host("cnpvgvb1pf031.pvgl.sap.corp","10.58.108.31", "admin", "Initial0", "Windows Server"));
//		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList, "Host List for WindowsMachine"));
//		List<Host> etcdhostList = new ArrayList<Host>();
//		etcdhostList.add(new Host("cnpvgvb1od089.pvgl.sap.corp", "10.58.9.89","root", "Initial0", "ETCD Server"));
//		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,"Host List for ETCD Machine "));
//		List<Host> sharefolderhostList = new ArrayList<Host>();
//		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp","10.97.76.179", "root", "Initial0", "Share Folder Server"));
//		hostsParameter.put("shareFolderServer", new TaskParameterEntry(sharefolderhostList, "Host List for Share Folder Machine"));
//		List<Host> hanahostList = new ArrayList<Host>();
//		hanahostList.add(new Host("CNPVG50862630.pvgl.sap.corp","10.58.121.129", "root", "Initial0", "HANA Server"));
//		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,"Host List for HANA Machine"));
//		//List<Host> mariadbhostList = new ArrayList<Host>();
//		//mariadbhostList.add(new Host("cnpvgvb1od044.pvgl.sap.corp","10.58.9.94", "root", "Initial0", "Mariadb Server"));
//		//hostsParameter.put("mariadbServer", new TaskParameterEntry(mariadbhostList, "Host List for MariaDB Machine"));
//		List<Host> centralServerList = new ArrayList<Host>();
//		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp","10.58.8.44", "administrator", "Initial0","Central Windows Server"));
//		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList, "Host List for Central Windows"));
//		TaskParameterMap hostTemplate = new TaskParameterMap();
//		hostTemplate.setParameters(hostsParameter);

//		 //Benchmark 2
//		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
//		// Common
//		parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
//		parameters.put("windowsFolder", new TaskParameterEntry("C:\\WindowsMainFolder", "Windows Main Folder"));
//		parameters.put("repository", new TaskParameterEntry("10.58.113.37:5000", "repository"));
//		parameters.put("instanceBackupPath", new TaskParameterEntry("/hana/dbbackup/JunitTest/h01adm/dbbackup","Linux Path for Keep Instance Backup File"));
//		parameters.put("initInstanceBackupPath", new TaskParameterEntry("/hana/dbbackup/JunitTest/h01adm/InitStatus","Linux Path for Keep Init Instance Backup File"));
//		parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_60\\bin\\java.exe","Java exe file location"));
//		parameters.put("remoteSubvolume", new TaskParameterEntry("/opt/glusterfs/brick", "ShareFolder Remote Loacation"));
//		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.136.79:30115", "Full Hana Instance URL"));
//		parameters.put("hanaPasswd", new TaskParameterEntry("Initial0","Hana User Password"));
//		parameters.put("hanaUser", new TaskParameterEntry("h01adm","Hana User Name"));
//		parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
//		parameters.put("schemaBackupFolder", new TaskParameterEntry("/hana/dbbackup/JunitTest/h01adm/PERFDB1","Linux Main Folder"));
//		parameters.put("mariaDBPasswd", new TaskParameterEntry("12345","MariaDB root User Password"));
//		parameters.put("isLocal", new TaskParameterEntry("true","flag for Local/other, only true/false"));
//		parameters.put("domainName", new TaskParameterEntry("pvgl.sap.corp","domainName"));
//		
//		parameters.put("jmeterScriptName",new TaskParameterEntry("OCC_ALL_new_withthread_AllCases_CSM.jmx","Jmeter Script Name"));
//		parameters.put("mainResultFolder",new TaskParameterEntry("C:\\WindowsMainFolder\\ResultCollection","Main Result Folder"));
//		parameters.put("scenario", new TaskParameterEntry("OCCSizing","Scenario Name"));
//		parameters.put("eshopDispatcherName", new TaskParameterEntry("eshop.pvgl.sap.corp", "Dispatcher Name of Eshop"));
//		parameters.put("dispatcherName", new TaskParameterEntry("CNPVGVB1PF007.pvgl.sap.corp", "Dispatcher Name for OCC"));
//		parameters.put("rumpup", new TaskParameterEntry("0", "Rumpup Time(s)"));
//		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
//		parameters.put("threadNumber", new TaskParameterEntry("8","Total Thread #"));
//		parameters.put("threadNumberList", new TaskParameterEntry("[[\"1\",\"24\"],[\"24\",\"36\"]]", "Run Cases Thread Number"));
//		parameters.put("remoteHostList", new TaskParameterEntry("10.58.108.31","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
//		parameters.put("duration", new TaskParameterEntry("500","Test Duration(s)"));
//		parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.8.43:3306", "Full CSM MariaDB URL"));
//		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
//		parameters.put("endSchemaNumber", new TaskParameterEntry("2","End Schema Number"));
//		parameters.put("maxUserNumberPerTenant", new TaskParameterEntry("10","Max User Number per Tenant"));
//		parameters.put("version",new TaskParameterEntry("9.0.0", "OCC version"));
//		parameters.put("addUserThreadNumber",new TaskParameterEntry("2", "Add User Script Thread Number"));
//		parameters.put("startUserNumber", new TaskParameterEntry("1","Start User Number"));	
//		parameters.put("duplicateThreadNumber", new TaskParameterEntry("1","Parrally Duplicate DBTemplate Thread Number"));
//		parameters.put("schemaNumberPerDuplicateThread",new TaskParameterEntry("2","Schema Number Per Duplicate thread"));
//		parameters.put("userNumberPerTenant", new TaskParameterEntry("10","Thread # per Tenant"));
//
//		TaskParameterMap template = new TaskParameterMap();
//		template.setParameters(parameters);
//
//		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
//		List<Host> CSMhostList = new ArrayList<Host>();
//		CSMhostList.add(new Host("CNPVGVB1PF009.pvgl.sap.corp", "10.58.8.43","root", "Initial0", "CSM Server"));
//		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,"Host List for CSM,SP,IDP,etc."));
//		List<Host> occhostList = new ArrayList<Host>();
//		occhostList.add(new Host("CNPVGVB1PF007.pvgl.sap.corp", "10.58.8.41","root", "Initial0", "OCC Server 1"));
//		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,"Host List for OCC Machine"));
//		List<Host> jobhostList = new ArrayList<Host>();
//		jobhostList.add(new Host("CNPVGVB1PF008.pvgl.sap.corp", "10.58.8.42","root", "Initial0", "Job Server 1"));
//		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,"Host List for Job Machine"));
//		List<Host> eshophostList = new ArrayList<Host>();
//		eshophostList.add(new Host("CNPVGVB1PF011.pvgl.sap.corp", "10.58.8.45","root", "Initial0", "Eshop Server"));
//		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,"Host List for Eshop Machine"));
//		List<Host> WindowshostList = new ArrayList<Host>();
//		WindowshostList.add(new Host("cnpvgvb1pf031.pvgl.sap.corp","10.58.108.31", "admin", "Initial0", "Windows Server"));
//		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList, "Host List for WindowsMachine"));
//		List<Host> etcdhostList = new ArrayList<Host>();
//		etcdhostList.add(new Host("CNPVGVB1PF012.pvgl.sap.corp", "10.58.8.46","root", "Initial0", "ETCD Server"));
//		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,"Host List for ETCD Machine "));
//		List<Host> sharefolderhostList = new ArrayList<Host>();
//		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp","10.97.76.179", "root", "Initial0", "Share Folder Server"));
//		hostsParameter.put("shareFolderServer", new TaskParameterEntry(sharefolderhostList, "Host List for Share Folder Machine"));
//		List<Host> hanahostList = new ArrayList<Host>();
//		hanahostList.add(new Host("CNPVG50819783.pvgl.sap.corp","10.58.136.79", "root", "CiOD@0vM", "HANA Server"));
//		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,"Host List for HANA Machine"));
////		List<Host> mariadbhostList = new ArrayList<Host>();
////		mariadbhostList.add(new Host("CNPVGVB1PF012.pvgl.sap.corp","10.58.8.46", "root", "Initial0", "Mariadb Server"));
////		hostsParameter.put("mariadbServer", new TaskParameterEntry(mariadbhostList, "Host List for MariaDB Machine"));
//		List<Host> centralServerList = new ArrayList<Host>();
//		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp","10.58.8.44", "administrator", "Initial0","Central Windows Server"));
//		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList, "Host List for Central Windows"));
//		TaskParameterMap hostTemplate = new TaskParameterMap();
//		hostTemplate.setParameters(hostsParameter);

		//Benchmark 1
		Map<String, TaskParameterEntry> parameters = new HashMap<String,
		TaskParameterEntry>();
		// Common
		parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
		parameters.put("windowsFolder", new TaskParameterEntry("C:\\WindowsMainFolder", "Windows Main Folder"));
		parameters.put("repository", new TaskParameterEntry("10.58.113.37:5000", "repository"));
		parameters.put("instanceBackupPath", new TaskParameterEntry("/hana/dbbackup/JunitTest/h00adm/dbbackup/COMPLETE_DATA_BACKUP","Linux Path for Keep Instance Backup File"));
		parameters.put("initInstanceBackupPath", new TaskParameterEntry("/hana/dbbackup/JunitTest/h00adm/InitStatus/COMPLETE_DATA_BACKUP","Linux Path for Keep Init Instance Backup File"));
		parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_60\\bin\\java.exe","Java exe file location"));
		parameters.put("remoteSubvolume", new TaskParameterEntry("/opt/glusterfs/brick", "ShareFolder Remote Loacation"));
		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.136.79:30015", "Full Hana Instance URL"));
		parameters.put("hanaPasswd", new TaskParameterEntry("Initial0","Hana User Password"));
		parameters.put("hanaUser", new TaskParameterEntry("h00adm","Hana User Name"));
		parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
		parameters.put("schemaBackupFolder", new TaskParameterEntry("/hana/dbbackup/JunitTest/h00adm/PERFDB1","Linux Main Folder"));
		parameters.put("mariaDBPasswd", new TaskParameterEntry("Initial0","MariaDB root User Password"));
		parameters.put("isLocal", new TaskParameterEntry("true","flag for Local/other, only true/false"));
		parameters.put("domainName", new TaskParameterEntry("pvgl.sap.corp","domainName"));
		
		parameters.put("jmeterScriptName",new TaskParameterEntry("OCC_ALL_new_withthread_AllCases_CSM.jmx","Jmeter Script Name"));
		parameters.put("mainResultFolder",new TaskParameterEntry("C:\\WindowsMainFolder\\ResultCollection","Main Result Folder"));
		parameters.put("scenario", new TaskParameterEntry("EshopSizing","Scenario Name"));
		parameters.put("eshopDispatcherName", new TaskParameterEntry("eshop.pvgl.sap.corp", "Dispatcher Name of Eshop"));
		parameters.put("dispatcherName", new TaskParameterEntry("CNPVGVB1PF001.pvgl.sap.corp", "Dispatcher Name for OCC"));
		parameters.put("rumpup", new TaskParameterEntry("0", "Rumpup Time(s)"));
		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
		parameters.put("threadNumber", new TaskParameterEntry("8","Total Thread #"));
		parameters.put("threadNumberList", new TaskParameterEntry("[[\"1\",\"24\"],[\"24\",\"36\"]]", "Run Cases Thread Number"));
		parameters.put("remoteHostList", new TaskParameterEntry("10.58.108.31","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
		parameters.put("duration", new TaskParameterEntry("500","Test Duration(s)"));
		parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.8.37:3306", "Full CSM MariaDB URL"));
		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
		parameters.put("endSchemaNumber", new TaskParameterEntry("2","End Schema Number"));
		parameters.put("maxUserNumberPerTenant", new TaskParameterEntry("10","Max User Number per Tenant"));
		parameters.put("version",new TaskParameterEntry("9.0.0", "OCC version"));
		parameters.put("addUserThreadNumber",new TaskParameterEntry("2", "Add User Script Thread Number"));
		parameters.put("startUserNumber", new TaskParameterEntry("1","Start User Number"));	
		parameters.put("duplicateThreadNumber", new TaskParameterEntry("1","Parrally Duplicate DBTemplate Thread Number"));
		parameters.put("schemaNumberPerDuplicateThread",new TaskParameterEntry("2","Schema Number Per Duplicate thread"));
		parameters.put("userNumberPerTenant", new TaskParameterEntry("20","Thread # per Tenant"));
		
		TaskParameterMap template = new TaskParameterMap();
		template.setParameters(parameters);
		
		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
		List<Host> CSMhostList = new ArrayList<Host>();
		CSMhostList.add(new Host("CNPVGVB1PF003.pvgl.sap.corp", "10.58.8.37","root", "Initial0", "CSM Server"));
		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,"Host List for CSM,SP,IDP,etc."));
		List<Host> occhostList = new ArrayList<Host>();
		occhostList.add(new Host("CNPVGVB1PF001.pvgl.sap.corp", "10.58.8.35","root", "Initial0", "OCC Server 1"));
		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,"Host List for OCC Machine"));
		List<Host> jobhostList = new ArrayList<Host>();
		jobhostList.add(new Host("CNPVGVB1PF002.pvgl.sap.corp", "10.58.8.36","root", "Initial0", "Job Server 1"));
		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,"Host List for Job Machine"));
		List<Host> eshophostList = new ArrayList<Host>();
		eshophostList.add(new Host("CNPVGVB1PF005.pvgl.sap.corp", "10.58.8.39","root", "Initial0", "Eshop Server"));
		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,"Host List for Eshop Machine"));
		List<Host> WindowshostList = new ArrayList<Host>();
		WindowshostList.add(new Host("cnpvgvb1pf031.pvgl.sap.corp","10.58.108.31", "admin", "Initial0", "Windows Server"));
		hostsParameter.put("windowsServer", new TaskParameterEntry(WindowshostList, "Host List for WindowsMachine"));
		List<Host> etcdhostList = new ArrayList<Host>();
		etcdhostList.add(new Host("CNPVGVB1PF006.pvgl.sap.corp", "10.58.8.40","root", "Initial0", "ETCD Server"));
		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,"Host List for ETCD Machine "));
		List<Host> sharefolderhostList = new ArrayList<Host>();
		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp","10.97.76.179", "root", "Initial0", "Share Folder Server"));
		hostsParameter.put("shareFolderServer", new TaskParameterEntry(sharefolderhostList, "Host List for Share Folder Machine"));
		List<Host> hanahostList = new ArrayList<Host>();
		hanahostList.add(new Host("CNPVG50819783.pvgl.sap.corp","10.58.136.79", "root", "CiOD@0vM", "HANA Server"));
		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,"Host List for HANA Machine"));
//		List<Host> mariadbhostList = new ArrayList<Host>();
//		mariadbhostList.add(new Host("CNPVGVB1PF006.pvgl.sap.corp","10.58.8.40", "root", "Initial0", "Mariadb Server"));
//		hostsParameter.put("mariadbServer", new TaskParameterEntry(mariadbhostList, "Host List for MariaDB Machine"));
		List<Host> centralServerList = new ArrayList<Host>();
		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp","10.58.8.44", "administrator", "Initial0","Central Windows Server"));
		hostsParameter.put("centralServer", new TaskParameterEntry(centralServerList, "Host List for Central Windows"));
		TaskParameterMap hostTemplate = new TaskParameterMap();
		hostTemplate.setParameters(hostsParameter);

//		// Debug Environment
//		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
//		// Common
//		parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
//		parameters.put("windowsFolder", new TaskParameterEntry("C:\\WindowsMainFolder", "Windows Main Folder"));
//		parameters.put("repository", new TaskParameterEntry("10.58.113.37:5000", "repository"));
//		parameters.put("instanceBackupPath", new TaskParameterEntry("/hana_data/Anywhere/dbbackup/COMPLETE_DATA_BACKUP","Linux Path for Keep Instance Backup File"));
//		parameters.put("initInstanceBackupPath", new TaskParameterEntry("/root/perftest/hanaInitBackup","Linux Path for Keep Init Instance Backup File"));
//		parameters.put("schemaBackupFolder", new TaskParameterEntry("/root/perftest/hanatmpbackup/PERFDB1", "Linux Main Folder"));
//		parameters.put("hanaInstanceFull", new TaskParameterEntry("10.58.120.235:30115", "Full Hana Instance URL"));
//		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
//		parameters.put("hanaUser", new TaskParameterEntry("cdbadm","Hana User Name"));
//		parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_60\\bin\\java.exe","Java exe file location"));
//		parameters.put("remoteSubvolume", new TaskParameterEntry("/opt/glusterfs/brick", "ShareFolder Remote Loacation"));
//		parameters.put("mainResultFolder",new TaskParameterEntry("C:\\WindowsMainFolder\\ResultCollection","Main Result Folder"));
//		parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
//		parameters.put("mariaDBPasswd", new TaskParameterEntry("Initial0","MariaDB root User Password"));
//		parameters.put("isLocal", new TaskParameterEntry("true","flag for Local/other, only true/false"));	
//		parameters.put("domainName", new TaskParameterEntry("pvgl.sap.corp","domainName"));
//		
//		parameters.put("jmeterScriptName",new TaskParameterEntry("OCC_ALL_new_withthread_AllCases_CSM.jmx","Jmeter Script Name"));
//		parameters.put("scenario", new TaskParameterEntry("OCCSizing","Scenario Name"));
//		parameters.put("eshopDispatcherName", new TaskParameterEntry("eshop.pvgl.sap.corp", "Dispatcher Name of Eshop"));
//		parameters.put("dispatcherName", new TaskParameterEntry("occ.pvgl.sap.corp", "Dispatcher Name for OCC"));
//		parameters.put("rumpup", new TaskParameterEntry("500", "Rumpup Time(s)"));
//		parameters.put("thinkTime", new TaskParameterEntry("30","Think Time(s)"));
//		parameters.put("threadNumber", new TaskParameterEntry("400","Total Thread #"));
//		parameters.put("threadNumberList", new TaskParameterEntry("[[\"400\"]]","Run Cases Thread Number"));
//		parameters.put("remoteHostList", new TaskParameterEntry("10.58.108.31,10.58.108.32,10.58.108.29,10.58.8.44","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
//		parameters.put("duration", new TaskParameterEntry("1200","Test Duration(s)"));
//		parameters.put("csmMariaDBFull", new TaskParameterEntry("10.58.108.24:3306", "Full CSM MariaDB URL"));
//		parameters.put("startSchemaNumber", new TaskParameterEntry("1","Start Schema Number"));
//		parameters.put("endSchemaNumber", new TaskParameterEntry("500","End Schema Number"));
//		parameters.put("maxUserNumberPerTenant", new TaskParameterEntry("10","Max User Number per Tenant"));
//		parameters.put("version",new TaskParameterEntry("11.0.0", "OCC version"));
//		parameters.put("addUserThreadNumber",new TaskParameterEntry("10", "Add User Script Thread Number"));
//		parameters.put("startUserNumber", new TaskParameterEntry("1","Start User Number"));	
//		parameters.put("duplicateThreadNumber", new TaskParameterEntry("10","Parrally Duplicate DBTemplate Thread Number"));
//		parameters.put("schemaNumberPerDuplicateThread",new TaskParameterEntry("50","Schema Number Per Duplicate thread"));
//		parameters.put("userNumberPerTenant", new TaskParameterEntry("4","Thread # per Tenant"));
//
//		TaskParameterMap template = new TaskParameterMap();
//		template.setParameters(parameters);
//
//		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
//		List<Host> CSMhostList = new ArrayList<Host>();
//		CSMhostList.add(new Host("CNPVGVB1PF024.pvgl.sap.corp", "10.58.108.24",
//				"root", "Initial0", "CSM Server"));
//		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,
//				"Host List for CSM,SP,IDP,etc."));
//		List<Host> occhostList = new ArrayList<Host>();
//		occhostList.add(new Host("CNPVGVB1PF025.pvgl.sap.corp", "10.58.108.25",
//				"root", "Initial0", "OCC Server 1"));
//		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,
//				"Host List for OCC Machine"));
//		List<Host> jobhostList = new ArrayList<Host>();
//		jobhostList.add(new Host("CNPVGVB1PF026.pvgl.sap.corp", "10.58.108.26",
//				"root", "Initial0", "Job Server 1"));
//		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
//				"Host List for Job Machine"));
//		List<Host> eshophostList = new ArrayList<Host>();
//		eshophostList.add(new Host("CNPVGVB1PF027.pvgl.sap.corp",
//				"10.58.108.27", "root", "Initial0", "Eshop Server"));
//		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,
//				"Host List for Eshop Machine"));
//		List<Host> WindowshostList = new ArrayList<Host>();
//		WindowshostList.add(new Host("cnpvgvb1pf031.pvgl.sap.corp",
//				"10.58.108.31", "admin", "Initial0", "Windows Server"));
//		hostsParameter.put("windowsServer", new TaskParameterEntry(
//				WindowshostList, "Host List for WindowsMachine"));
//		List<Host> etcdhostList = new ArrayList<Host>();
//		etcdhostList.add(new Host("CNPVGVB1PF028.pvgl.sap.corp",
//				"10.58.108.28", "root", "Initial0", "ETCD Server"));
//		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,
//				"Host List for ETCD Machine "));
//		List<Host> sharefolderhostList = new ArrayList<Host>();
//		sharefolderhostList.add(new Host("cnpvgvb1pf025.pvgl.sap.corp",
//				"10.97.76.179", "root", "Initial0", "Share Folder Server"));
//		hostsParameter.put("shareFolderServer", new TaskParameterEntry(
//				sharefolderhostList, "Host List for Share Folder Machine"));
//
//		List<Host> hanahostList = new ArrayList<Host>();
//		hanahostList.add(new Host("cnpvg50861384.pvgl.sap.corp",
//				"10.58.120.235", "root", "Initial0", "HANA Server"));
//		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,
//				"Host List for HANA Machine"));
//		//List<Host> mariadbhostList = new ArrayList<Host>();
//		// mariadbhostList.add(new
//		// Host("CNPVGVB1PF012.pvgl.sap.corp","10.58.8.46", "root", "Initial0",
//		// "Mariadb Server"));
//		//hostsParameter.put("mariadbServer", new TaskParameterEntry(mariadbhostList, "Host List for MariaDB Machine"));
//		List<Host> centralServerList = new ArrayList<Host>();
//		centralServerList.add(new Host("cnpvgvb1pf010.pvgl.sap.corp",
//				"10.58.8.44", "administrator", "Initial0",
//				"Central Windows Server"));
//		hostsParameter.put("centralServer", new TaskParameterEntry(
//				centralServerList, "Host List for Central Windows"));
//		TaskParameterMap hostTemplate = new TaskParameterMap();
//		hostTemplate.setParameters(hostsParameter);

		
//		//AWS ENV
//		Map<String, TaskParameterEntry> parameters = new HashMap<String, TaskParameterEntry>();
//		//Common
//		parameters.put("mainfolder", new TaskParameterEntry("/root/perftest/","Linux Main Folder"));
//		parameters.put("windowsFolder", new TaskParameterEntry("C:\\WindowsMainFolder","Windows Main Folder"));
//		parameters.put("repository", new TaskParameterEntry("52.24.69.227:5000","repository"));
//		parameters.put("instanceBackupPath", new TaskParameterEntry("/home/dbbackup/dbbackup","Linux Path for Keep Instance Backup File"));
//		parameters.put("initInstanceBackupPath", new TaskParameterEntry("/home/dbbackup/InitStatus","Linux Path for Keep Init Instance Backup File"));
//		parameters.put("windowsJava", new TaskParameterEntry("C:\\Program Files\\Java\\jre1.8.0_45\\bin\\java.exe","Java exe file location"));
//		parameters.put("remoteSubvolume", new TaskParameterEntry("csm-volume","ShareFolder Remote Loacation"));
//		parameters.put("hanaPasswd", new TaskParameterEntry("12345678","Hana User Password"));
//		parameters.put("hanaUser", new TaskParameterEntry("ndbadm","Hana User Name"));
//		parameters.put("sourceSchemaName", new TaskParameterEntry("PERFDB1","DB Template Name"));
//		parameters.put("schemaBackupFolder", new TaskParameterEntry("/home/dbbackup/PERFDB1","Linux Main Folder"));
//		parameters.put("mariaDBPasswd", new TaskParameterEntry("12345","MariaDB root User Password"));
//		parameters.put("isLocal", new TaskParameterEntry("false","flag for Local/other, only true/false"));
//
//		parameters.put("domainName", new TaskParameterEntry("us-west-2.compute.internal","domainName"));
//		parameters.put("jmeterScriptName", new TaskParameterEntry("OCC_ALL_new_withthread_AllCases_CSM.jmx","Jmeter Script Name"));
//		parameters.put("mainResultFolder", new TaskParameterEntry("C:\\WindowsMainFolder\\Result","Main Result Folder"));
//		parameters.put("scenario", new TaskParameterEntry("Pos","Scenario Name"));
//		parameters.put("eshopDispatcherName", new TaskParameterEntry("eshop.us-west-2.compute.internal","Dispatcher Name of Eshop"));
//		parameters.put("dispatcherName", new TaskParameterEntry("ip-172-31-0-57.us-west-2.compute.internal","Dispatcher Name for OCC"));
//		parameters.put("rumpup", new TaskParameterEntry("240","Rumpup Time(s)"));
//		parameters.put("thinkTime", new TaskParameterEntry("0","Think Time(s)"));
//		parameters.put("threadNumber", new TaskParameterEntry("8","Total Thread #"));
//		parameters.put("threadNumberList", new TaskParameterEntry("[[\"10\",\"10\"]]","Run Cases Thread Number"));
//		parameters.put("remoteHostList", new TaskParameterEntry("172.31.7.243","Remote Host List, e.g: 192.168.0.1,192.168.0.2"));
//				
//		parameters.put("duration", new TaskParameterEntry("4200","Test Duration(s)"));
//		parameters.put("csmMariaDBFull", new TaskParameterEntry("172.31.3.247:3306","Full CSM MariaDB URL"));
//		parameters.put("startSchemaNumber", new TaskParameterEntry("21","Start Schema Number"));
//		parameters.put("endSchemaNumber", new TaskParameterEntry("500","End Schema Number"));
//		parameters.put("maxUserNumberPerTenant", new TaskParameterEntry("30","Max User Number per Tenant"));
//		parameters.put("version", new TaskParameterEntry("7.0.0","OCC version"));
//		parameters.put("hanaInstanceFull", new TaskParameterEntry("172.31.1.141:30015","Full Hana Instance URL"));
//		parameters.put("duplicateThreadNumber", new TaskParameterEntry("10","Parrally Duplicate DBTemplate Thread Number"));
//		parameters.put("schemaNumberPerDuplicateThread", new TaskParameterEntry("50","Schema Number Per Duplicate thread"));
//
//		parameters.put("userNumberPerTenant", new TaskParameterEntry("30","Thread # per Tenant"));
//				
//		TaskParameterMap template = new TaskParameterMap();
//		template.setParameters(parameters);
//				
//		Map<String, TaskParameterEntry> hostsParameter = new HashMap<String, TaskParameterEntry>();
//		List<Host> CSMhostList = new ArrayList<Host>();
//		CSMhostList.add(new Host("ip-172-31-3-247.us-west-2.compute.internal",
//				"172.31.3.247", "root", "Initial0", "CSM Server"));
//		hostsParameter.put("csmServer", new TaskParameterEntry(CSMhostList,
//				"Host List for CSM,SP,IDP,etc."));
//		List<Host> occhostList = new ArrayList<Host>();
//		occhostList.add(new Host("ip-172-31-0-57.us-west-2.compute.internal",
//				"172.31.0.57", "root", "Initial0", "OCC Server 1"));
//		// occhostList.add(new Host("cnpvgvb1od036.pvgl.sap.corp", "10.58.9.86",
//		// "root",
//		// "Initial0", "OCC Server 2"));
//		// occhostList.add(new Host("cnpvgvb1od037.pvgl.sap.corp", "10.58.9.87",
//		// "root",
//		// "Initial0", "OCC Server 3"));
//		hostsParameter.put("occServer", new TaskParameterEntry(occhostList,
//				"Host List for OCC Machine"));
//		List<Host> jobhostList = new ArrayList<Host>();
//		jobhostList.add(new Host("ip-172-31-0-56.us-west-2.compute.internal",
//				"172.31.0.56", "root", "Initial0", "Job Server 1"));
//		hostsParameter.put("jobServer", new TaskParameterEntry(jobhostList,
//				"Host List for Job Machine"));
//		List<Host> eshophostList = new ArrayList<Host>();
//		eshophostList.add(new Host("ip-172-31-4-48.us-west-2.compute.internal",
//				"172.31.4.48", "root", "Initial0", "Eshop Server"));
//		// eshophostList.add(new Host("cnpvgvb1od043.pvgl.sap.corp",
//		// "10.58.9.93", "root",
//		// "Initial0", "Eshop Server 2"));
//		hostsParameter.put("eshopServer", new TaskParameterEntry(eshophostList,
//				"Host List for Eshop Machine"));
//		List<Host> WindowshostList = new ArrayList<Host>();
//		WindowshostList.add(new Host(
//				"ip-172-31-7-243.us-west-2.compute.internal", "172.31.7.243",
//				"administrator", "Initial0", "Windows Server"));
//		hostsParameter.put("windowsServer", new TaskParameterEntry(
//				WindowshostList, "Host List for WindowsMachine"));
//		List<Host> etcdhostList = new ArrayList<Host>();
//		etcdhostList.add(new Host("ip-172-31-8-66.us-west-2.compute.internal",
//				"172.31.8.66", "root", "Initial0", "ETCD Server"));
//		hostsParameter.put("etcdServer", new TaskParameterEntry(etcdhostList,
//				"Host List for ETCD Machine "));
//		List<Host> sharefolderhostList = new ArrayList<Host>();
//		sharefolderhostList.add(new Host(
//				"ip-172-31-3-126.us-west-2.compute.internal", "172.31.3.126",
//				"root", "Initial0", "Share Folder Server"));
//		hostsParameter.put("shareFolderServer", new TaskParameterEntry(
//				sharefolderhostList, "Host List for Share Folder Machine"));
//		List<Host> hanahostList = new ArrayList<Host>();
//		hanahostList.add(new Host("ip-172-31-1-141.us-west-2.compute.internal",
//				"172.31.1.141", "root", "Initial0", "HANA Server"));
//		hostsParameter.put("hanaServer", new TaskParameterEntry(hanahostList,
//				"Host List for HANA Machine"));
//		List<Host> mariadbhostList = new ArrayList<Host>();
////		mariadbhostList.add(new Host(
////				"ip-172-31-4-49.us-west-2.compute.internal", "172.31.4.49",
////				"root", "Initial0", "Mariadb Server"));
//		hostsParameter.put("mariadbServer", new TaskParameterEntry(
//				mariadbhostList, "Host List for MariaDB Machine"));
//		List<Host> centralServerList = new ArrayList<Host>();
//		centralServerList.add(new Host(
//				"ip-172-31-14-36.us-west-2.compute.internal", "172.31.14.36",
//				"admin", "Initial0", "Central Windows Server"));
//		hostsParameter.put("centralServer", new TaskParameterEntry(
//				centralServerList, "Host List for Central Windows"));
//		TaskParameterMap hostTemplate = new TaskParameterMap();
//		hostTemplate.setParameters(hostsParameter);
		

//		parameters.put("filePath", new TaskParameterEntry("C:\\Users\\I311112\\Desktop\\DEBUG IMPORT\\","Schema Number Per Duplicate thread"));
//		parameters.put("executionId", new TaskParameterEntry("2","Schema Number Per Duplicate thread"));
		parameters.put("resultSessionId", new TaskParameterEntry("1","Result Session ID"));	
		//parameters.put("startDate", new TaskParameterEntry("2016-01-01 00:00:00","Start DateTime (Optional)"));
		//parameters.put("endDate", new TaskParameterEntry("2016-09-09 00:00:00","End DateTime (Optional)"));
		parameters.put("jmeterRequestList", new TaskParameterEntry("StateGroup,UserProperty,LightChannelService@getPOS","Jmeter Request List (Optional)"));
		
		
		// List<Host> test = (List<Host>)
		// hostTemplate.getParameters().get("mariadbServer").getValue();
		// System.out.println(test.size());
		// DeployPart
		try {
			Result r = Result.Pass;
			List<Task> taskList = new ArrayList<Task>();
			taskList.add(new Debug());
//			taskList.add(new CleanEnvironment());
//			taskList.add(new ShellScriptCopy());
//			taskList.add(new Deployment());
//			taskList.add(new PackagePrepare());
//			taskList.add(new SchemaPrepareAndUpgrade());
//			taskList.add(new DuplicateSchema());
//			taskList.add(new CreateDBPool());
//			taskList.add(new AddTenant());
//			taskList.add(new AddUser());
//			taskList.add(new HanaInstanceBackup());
			
//			taskList.add(new KillMonitor());
//			taskList.add(new RemoveMonitorFile());
//			taskList.add(new RestartService());
//			taskList.add(new StartMonitor());
//			taskList.add(new OCCSizingSingleCase());
//			taskList.add(new KillMonitor());
//			taskList.add(new LogCollection());
//			taskList.add(new HanaInstanceRecovery());

			// taskList.add(new UploadCertification());
//			taskList.add(new UpdateGlobalSetting());
//			taskList.add(new OCCSizingTest());
//			taskList.add(new DataImport());
//			taskList.add(new DataAnalysis());
			for (ITask t : taskList) {
				if (r == Result.Pass) {
					t.setHosts(hostTemplate);
					t.setParameters(template);
					Logger logger = Logger.getLogger(this.getClass());
					logger.setLevel(Level.DEBUG);
					t.setLogger(logger);
					
					r = t.execute();
					System.out.println(r);
					System.out.println();
				} else {
					LogHelper.error("Deployment Failed");
					throw new Exception();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
