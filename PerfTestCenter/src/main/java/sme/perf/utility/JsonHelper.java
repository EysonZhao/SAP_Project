package sme.perf.utility;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper<T> {
	public T deserialObject(String json, Class<T> type) {
		T o = null;
		try {
			o = new ObjectMapper().readValue(json,  type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogHelper.error("deserialObject failed! JSON: " + json);
		}
		return o;
	}
	
	public String serializeObject(T o){
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			LogHelper.info(e.getMessage());
		}
		return "";
	}
}
