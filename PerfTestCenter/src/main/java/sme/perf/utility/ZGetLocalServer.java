package sme.perf.utility;

public class ZGetLocalServer {
	private static String addr;
	private static int port;
	public static String getAddr() {
		return addr;
	}
	public static void setAddr(String addr) {
		ZGetLocalServer.addr = addr;
	}
	public static int getPort() {
		return port;
	}
	public static void setPort(int port) {
		ZGetLocalServer.port = port;
	}
	
}
