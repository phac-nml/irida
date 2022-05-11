package ca.corefacility.bioinformatics.irida.web.controller.api.json;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.apache.commons.lang3.time.DateUtils;

/**
 * Serialization class for java Date objects. We use DATETIME types for storing
 * our timestamps in mysql. DATETIME only stores at seconds precision by
 * default. In the REST api when we create an object the timestamps will have
 * milliseconds precision but on subsequent fetchs the timestamps will only have
 * seconds precision. This enforces the timestamp fields to only ever have
 * seconds precision.
 */
public class TimestampJson {

	/**
	 * Default serializer for {@link Date} objects.
	 */
	public static class TimestampSerializer extends StdSerializer<Date> {

		public TimestampSerializer() {
			super(Date.class);
		}

		@Override
		public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeNumber(DateUtils.truncate(value, Calendar.SECOND).getTime());
		}

	}
}
