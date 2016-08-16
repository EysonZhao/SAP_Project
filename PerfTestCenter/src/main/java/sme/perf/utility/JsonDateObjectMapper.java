package sme.perf.utility;

import java.text.SimpleDateFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JsonDateObjectMapper extends ObjectMapper {
	/**
	 * 
	 */
	private static final long serialVersionUID = -45209688731365090L;

	public JsonDateObjectMapper() {
        super();
        setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		registerModule(new JodaModule());
    }
}
