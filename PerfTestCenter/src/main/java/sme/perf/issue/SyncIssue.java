package sme.perf.issue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import sme.perf.request.entity.TestIssue;
import sme.perf.utility.HttpsRequestHelper;
import sme.perf.utility.PropertyFile;

public class SyncIssue implements ISyncIssue {

	@Override
	public TestIssue sync(TestIssue issue) throws IOException {
		
		String jiraJson = getJiraJson(issue.getJiraKey());
		JsonParser jsonParser = new JsonFactory().createParser(jiraJson);
		parseIssue(issue, jsonParser, "");
		jsonParser.close();
		
		String url = PropertyFile.getValue("JiraDisplayUrl") + issue.getJiraKey();
		issue.setUrl(url);
		
		return issue;
	}
	
	private String getJiraJson(String jiraKey) throws IOException {
		//login
		loginJira(jiraKey);
		
		String jiraUrl = PropertyFile.getValue("JiraIssueFormatString");
		jiraUrl = String.format(jiraUrl, jiraKey);
		//get jira
		return HttpsRequestHelper.GetRequest(jiraUrl);
	}
	
	private void loginJira(String jiraKey) throws UnsupportedEncodingException, IOException{
		String jiraPrefix = jiraKey.substring(0, jiraKey.indexOf('-'));
		String[] opJiraPrefix = PropertyFile.getValue("JiraOPPrex").split(",");
		String loginContent = PropertyFile.getValue("JiraLoginContent");
		for(int i=0 ; i<opJiraPrefix.length ; i++){	//login with OP jira user
			if(opJiraPrefix[i].equals(jiraPrefix)){
				loginContent = String.format(loginContent, 
						PropertyFile.getValue("JiraOPUser"),
						PropertyFile.getValue("JiraOPPassword"));
				break;
			}
		}
		String[] anwJiraPrefix = PropertyFile.getValue("JiraAnwPrex").split(",");
		for(int i=0 ; i<anwJiraPrefix.length ; i++){
			if(anwJiraPrefix[i].equals(jiraPrefix)){
				loginContent = String.format(loginContent, 
						PropertyFile.getValue("JiraAnwUser"),
						PropertyFile.getValue("JiraAnwPassword"));
				break;
			}
		}
		
		HttpsRequestHelper.PostRequest(PropertyFile.getValue("JiraLoginUrl"), loginContent);
	}
	
	private void parseIssue(TestIssue testIssue, JsonParser jsonParser, String parent) throws IOException{
		String nodeName = "";
		JsonToken nextToken = jsonParser.nextToken();
		while(nextToken != JsonToken.END_OBJECT && null != nextToken) {
			if(nextToken == JsonToken.START_OBJECT){
				if("" != nodeName){
					parseIssue(testIssue, jsonParser, parent + "/" + nodeName);
				}
				else{
					parseIssue(testIssue, jsonParser, parent);
				}
			}
			else if(null != jsonParser.getCurrentName()){
				nodeName = jsonParser.getCurrentName();
				switch(nodeName){
				case "summary":
					nextToken = jsonParser.nextToken();
					testIssue.setTitle(jsonParser.getText());
					break;
				case "displayName":
					switch(parent){
					case "/fields/reporter":
						nextToken = jsonParser.nextToken();
						testIssue.setCreator(jsonParser.getText());
						break;
					case "/fields/assignee":
						nextToken = jsonParser.nextToken();
						testIssue.setProcessor(jsonParser.getText());
						break;
					}
					break;
				case "name":
					switch(parent){
					case "/fields/status":
						nextToken = jsonParser.nextToken();
						testIssue.setStatus(jsonParser.getText());
						break;
					case "/fields/priority":
						nextToken = jsonParser.nextToken();
						testIssue.setPriority(jsonParser.getText());
						break;
					}
					break;
				case "created":
					nextToken = jsonParser.nextToken();
					testIssue.setCreateDate(DateTime.parse(jsonParser.getText()));
					break;
				}
			}
			nextToken = jsonParser.nextToken();
		}
	}
}
