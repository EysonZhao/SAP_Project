package sme.perf.utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpsRequestHelper {
	
	static java.net.CookieManager cm = null;
	
	public static String PostRequest(String url, byte[] content) throws IOException{
		if(cm == null){
			cm = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(cm);
		}
		
		URL loginUrl = new URL(url);
		HttpsURLConnection loginRequest = (HttpsURLConnection) loginUrl.openConnection();
		loginRequest.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		loginRequest.setRequestMethod("POST");
		loginRequest.setDoOutput(true);
		DataOutputStream loginWriter = new DataOutputStream (loginRequest.getOutputStream());
		loginWriter.write(content);
		loginWriter.flush();
		loginWriter.close();
		//get the session data from response
		int responseCode = loginRequest.getResponseCode();
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(loginRequest.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String newLine ;
		while((newLine = responseReader.readLine()) != null){
			sb.append(newLine);
		}
		return sb.toString();
	}
	
	public static String PostRequest(String url, String content) throws UnsupportedEncodingException, IOException{
		return PostRequest(url, content.getBytes("UTF-8"));
	}
	
	public static String GetRequest(String url) throws IOException{
		if(cm == null){
			cm = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(cm);
		}
		
		URL loginUrl = new URL(url);
		HttpsURLConnection loginRequest = (HttpsURLConnection) loginUrl.openConnection();
		loginRequest.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		loginRequest.setRequestMethod("GET");

		int responseCode = loginRequest.getResponseCode();
		BufferedReader responseReader = new BufferedReader(new InputStreamReader(loginRequest.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String newLine ;
		while((newLine = responseReader.readLine()) != null){
			sb.append(newLine);
		}
		return sb.toString();
		
	}
}
