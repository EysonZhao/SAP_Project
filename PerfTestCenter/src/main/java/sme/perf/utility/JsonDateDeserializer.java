package sme.perf.utility;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateDeserializer extends JsonDeserializer<DateTime>{
    private static DateTimeFormatter dateFormatter = 
            DateTimeFormat.forPattern("yyyy-MM-dd");
    private static DateTimeFormatter dateTimeformatter = 
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	@Override
	public DateTime deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		String strDate = jsonParser.getText();
		try{
			return dateTimeformatter.parseDateTime(strDate);
		}
		catch(Exception ex){
			return dateFormatter.parseDateTime(strDate);
		}
	}

}
