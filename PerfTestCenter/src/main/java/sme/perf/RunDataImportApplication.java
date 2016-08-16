package sme.perf;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import sme.perf.result.entity.ResultSession;
import sme.perf.task.impl.common.DataImport;

public class RunDataImportApplication {

	private static Logger logger = Logger.getLogger("RunDataImportApplication");
	/**
	 * @param args
	 *            args0=FilePath args1=SessionName args2=UserNumber
	 *            args3=Scenario args4=Branch args5=BuildInfo
	 */

	public static void main(String[] args) {

//		try {
//			logger.setLevel(Level.DEBUG);
//
//			String branch = "N/A";
//			String buildinfo = "N/A";
//			String scenario = "Add Sales Order";
//			if (args.length >=3 && args.length <= 6) {
//				String filePath=args[0];
//				String sessionName=args[1];
//				int userNumber=Integer.parseInt(args[2]);
//				if(args.length>=4){
//					scenario = args[3];
//				}
//				if(args.length>=5){
//					branch = args[4];
//				}
//				if(args.length>=6){
//					buildinfo = args[5];
//				}
//				DataImport dataImport=new DataImport();
//				dataImport.setLogger(logger);
//				ResultSession newResultSession = dataImport.createNewResultSession(sessionName,
//						userNumber,0,1,scenario,branch,buildinfo,3600);
//				dataImport.importFolderByType("io", filePath, newResultSession);
//				dataImport.importFolderByType("top", filePath, newResultSession);
//				dataImport.importFolderByType("transaction", filePath, newResultSession);
//				dataImport.importFolderByType("javamelody", filePath, newResultSession);
//				dataImport.importFolderByType("jmeter", filePath, newResultSession);
//				dataImport.importFolderByType("dockerstats", filePath, newResultSession);
//				dataImport.importFolderByType("machine", filePath, newResultSession);
//				logger.info(String.format("Data Import Succeed. retCode=%d", 0));
//			} else {
//				logger.fatal(String.format("Wrong Parameters. retCode=%d", -2));
//			}	
//		} catch (Exception ex) {
//			logger.fatal(String.format("Data Import Fail. retCode=%d", -1), ex);
//		}
	}

}
