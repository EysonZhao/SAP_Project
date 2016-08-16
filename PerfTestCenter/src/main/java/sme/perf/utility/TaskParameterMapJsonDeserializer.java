package sme.perf.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sme.perf.entity.Host;
import sme.perf.task.TaskParameterEntry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


public class TaskParameterMapJsonDeserializer extends StdDeserializer<Map<String,TaskParameterEntry>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5365081495688000435L;

	protected TaskParameterMapJsonDeserializer(){
		super(Map.class);
	}
	
	protected TaskParameterMapJsonDeserializer(Class<Map<String,TaskParameterEntry>> vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, TaskParameterEntry> deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		Map<String, TaskParameterEntry> result = new HashMap<String, TaskParameterEntry>();
		
		JsonNode rootNode = jp.getCodec().readTree(jp);
		Iterator<JsonNode> it = rootNode.get("entries").iterator();
		while(it.hasNext()){
			JsonNode node = it.next();
			JsonNode valueNode = node.get("value");
			if(valueNode.isArray()){
				Iterator<JsonNode> vIt = valueNode.elements();
				List<Host> list = new ArrayList<Host>();
				while(vIt.hasNext()){
					JsonNode vNode = vIt.next();
					list.add(new Host(vNode.get("hostName").asText(),
										vNode.get("ip").asText(), 
										vNode.get("userName").asText(), 
										vNode.get("userPassword").asText(), 
										vNode.get("description").asText()));
					result.put(node.get("name").asText(), new TaskParameterEntry(list, node.get("description").asText()));
				}
			}
			else if(valueNode.isTextual()){
				result.put(node.get("name").asText(), new TaskParameterEntry(valueNode.asText(), node.get("description").asText()));
			}
		}
		
		return result;
	}

}
