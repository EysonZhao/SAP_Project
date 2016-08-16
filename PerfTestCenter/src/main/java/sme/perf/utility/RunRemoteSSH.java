package sme.perf.utility;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import sme.perf.utility.LogHelper;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;


public class RunRemoteSSH {
	private Connection conn;
	private String ipAddress;
	private String userName;
	private String password;
	private static final int TIME_OUT = 600000;


	public RunRemoteSSH(String ipAddress, String userName, String password) {
		this.ipAddress = ipAddress;
		this.userName = userName;
		this.password = password;
	}

	private boolean login() throws IOException {
		conn = new Connection(ipAddress);
		conn.connect();
		return conn.authenticateWithPassword(userName, password);
	}

	public int exec(String cmds, boolean isIgnore, int retry, int timeOut) throws Exception {
		InputStream stdOut = null;
		InputStream stdErr = null;
		String outStr = "";
		String outErr = "";
		int returncode = -1;
		try {
			if (login()) {
				logger.info("Start to execute command \"" + cmds + "\"." + " @" + ipAddress);
				for (int i=0;i<=retry;i++){
					Session session = conn.openSession();
					session.execCommand(cmds);

					stdOut = session.getStdout();
					stdErr = session.getStderr();
					byte[] buffer = new byte[65536];
					while (true) {
						if ((stdOut.available() == 0)) {
							int conditions = session.waitForCondition(
									ChannelCondition.STDOUT_DATA
											| ChannelCondition.STDERR_DATA
											| ChannelCondition.EOF, timeOut);
							if ((conditions & ChannelCondition.TIMEOUT) != 0) {
								logger.info("Time out and exit shell session.");
								break;
							}
							if ((conditions & ChannelCondition.EOF) != 0) {
								if ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0) {
									break;
								}
							}
						}
						while (stdOut.available() > 0) {
							int len = stdOut.read(buffer);
							if (len > 0) {
								String tempOut = new String(buffer, 0, len);
								logger.debug(tempOut);
								outStr = outStr + tempOut;
							}
						}
						while (stdErr.available() > 0) {
							int len = stdErr.read(buffer);
							if (len > 0) {
								String tempErr = new String(buffer, 0, len);
								logger.debug(tempErr);
								outErr = outErr + tempErr;
							}
						}
					}
					session.waitForCondition(ChannelCondition.EXIT_STATUS, 10000);
					returncode = session.getExitStatus();
					if (returncode > 0) {
						//fix return 259 issue
						if(returncode==259){
							retry=retry+1;
						}
						if(i==retry){
							if (!isIgnore) {
								logger.error("Execute command \"" + cmds
										+ "\" with Error." + " @" + ipAddress);
								logger.error("Return Code: " + returncode);
								throw new Exception("ShellCmdException.");
							}else{
								logger.info("Execute command \"" + cmds
										+ "\" with Error."  + " @" + ipAddress);
								logger.info("Return Code: " + returncode);
							}
						}
					} else {
						logger.info("Execute command \"" + cmds
								+ "\" successfully."  + " @" + ipAddress);
						break;
					}
				}
			} else {
				logger.error("Login remote linux machine " + ipAddress
						+ " fail.");
				throw new Exception("LoginException.");
			}
		} catch (Exception e) {
			if (!isIgnore) {
				throw e;
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return returncode;
	}

	
	public int execute(String cmd) throws Exception {
		return execute(cmd,false,0,TIME_OUT);
	}

	public int execute(String cmd, boolean isIgnore) throws Exception {
		return execute(cmd, isIgnore,0,TIME_OUT);
	}
	
	public int execute(String cmd, int retry) throws Exception {
		return execute( cmd, false,retry,TIME_OUT);
	}
	
	public int execute(String cmd, boolean isIgnore, int retry,int timeOut) throws Exception {
		return exec(cmd,isIgnore,retry,timeOut);
	}

	public String execWithReturnString(String cmds, boolean isIgnore, int retry, int timeOut)
			throws Exception {
		InputStream stdOut = null;
		InputStream stdErr = null;
		String outStr = "";
		String outErr = "";
		int returncode = -1;
		try {
			if (login()) {
				logger.info("Start to execute command \"" + cmds + "\"."  + " @" + ipAddress);
				for (int i=0;i<=retry;i++){
					Session session = conn.openSession();
					session.execCommand(cmds);
					
					stdOut = session.getStdout();
					stdErr = session.getStderr();
					byte[] buffer = new byte[65536];  
					while (true) {  
					    if ((stdOut.available() == 0)) {  
					      int conditions = session.waitForCondition(ChannelCondition.STDOUT_DATA |   
					            ChannelCondition.STDERR_DATA | ChannelCondition.EOF, timeOut);  
					        if ((conditions & ChannelCondition.TIMEOUT) != 0) {  
					        	logger.info("Time out and exit shell session.");
								break;
					        }  
					      if ((conditions & ChannelCondition.EOF) != 0) {  
					        if ((conditions & (ChannelCondition.STDOUT_DATA |   
					                ChannelCondition.STDERR_DATA)) == 0) {  
					                break;  
					            }  
					        }  
					    }  
					    while (stdOut.available() > 0) {  
						    int len = stdOut.read(buffer);  
						    if (len > 0){  
						    	String tempOut=new String(buffer, 0, len);
						    	logger.debug(tempOut);  
						    	outStr=outStr+tempOut;
						    }  
						  }                                    
						  while (stdErr.available() > 0) {  
						        int len = stdErr.read(buffer);  
						        if (len > 0){  
							    	String tempErr=new String(buffer, 0, len);
							    	logger.debug(tempErr);  
							    	outErr=outErr+tempErr;
						        }  
						    }  
						}  
					session.waitForCondition(ChannelCondition.EXIT_STATUS, 10000);
					returncode = session.getExitStatus();
					if (returncode > 0) {
						//fix return 259 issue
						if(returncode==259){
							retry=retry+1;
						}
						if(i==retry){
							if (!isIgnore) {
								logger.error("Execute command \"" + cmds
										+ "\" with Error."  + " @" + ipAddress);
								logger.error("Return Code: " + returncode);
								throw new Exception("ShellCmdException.");
							}else{
								logger.info("Execute command \"" + cmds
										+ "\" with Error."  + " @" + ipAddress);
								logger.info("Return Code: " + returncode);
							}
						}
					} else {
						logger.info("Execute command \"" + cmds
								+ "\" successfully."  + " @" + ipAddress);
						break;
					}
				}
			} else {
				logger.error("Login remote linux machine " + ipAddress
						+ " fail.");
				throw new Exception("LoginException.");
			}
		} catch (Exception e) {
			if (!isIgnore) {
				throw e;
			}
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
		return outStr;
	}

	public String executeWithReturnString(String cmd) throws Exception {
		return executeWithReturnString(cmd, false, 0, TIME_OUT);
	}

	public String executeWithReturnString(String cmd, boolean isIgnore)
			throws Exception {
		return executeWithReturnString(cmd, isIgnore, 0, TIME_OUT);
	}

	public String executeWithReturnString(String cmd, int retry)
			throws Exception {
		return executeWithReturnString(cmd, false, retry, TIME_OUT);
	}

	public String executeWithReturnString(String cmd, boolean isIgnore,
			int retry, int timeOut) throws Exception {
		return execWithReturnString(cmd, isIgnore, retry, timeOut);
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	private Logger logger;
	
}
