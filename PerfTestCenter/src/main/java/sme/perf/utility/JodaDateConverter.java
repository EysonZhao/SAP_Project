package sme.perf.utility;

import java.sql.Timestamp;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.DateTime;

public class JodaDateConverter implements Converter {

    private static final long serialVersionUID = -1098462020159912097L;

    /**
     * Convert object type of <code>org.joda.time.DateTime</code> to data type
     * of <code>java.util.Date</code> or <code>java.util.Calendar</code>
     * 
     * @throws IllegalArgumentException
     *             if the objectValue is not of org.joda.time.DateTime type
     */
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        }
        else{
        	return new Timestamp(((DateTime)objectValue).getMillis());
        }
    }

    /**
     * Convert data type of <code>java.util.Date</code> or
     * <code>java.util.Calendar</code> to object type of
     * <code>org.joda.time.DateTime</code>
     * 
     * @throws IllegalArgumentException
     *             if the dataValue is not of java.util.Date or
     *             java.util.Calendar type
     */
    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
    	return dataValue == null ? null : new DateTime((Timestamp) dataValue);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
    }
}

