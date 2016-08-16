package sme.perf.utility;

import java.io.IOException;
import java.util.Map;
import sme.perf.task.TaskParameterEntry;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class TaskParameterMapJsonSerializer extends StdSerializer<Map<String, TaskParameterEntry>> {

	protected TaskParameterMapJsonSerializer(){
		super(Map.class, false);
	}
	
	protected TaskParameterMapJsonSerializer(Class<Map<String, TaskParameterEntry>> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void serialize(Map<String, TaskParameterEntry> map, JsonGenerator jgen,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		jgen.writeStartObject();
		jgen.writeArrayFieldStart("entries");
		//jgen.writeStartArray();
		for(Map.Entry<String, TaskParameterEntry> entry : map.entrySet()){
			jgen.writeStartObject();
			jgen.writeStringField("name", entry.getKey());
			jgen.writeObjectField("value", entry.getValue().getValue());
			jgen.writeStringField("description", entry.getValue().getDescription());
			jgen.writeEndObject();
		}
	
		jgen.writeEndArray();
		jgen.writeEndObject();
	}

	

}
