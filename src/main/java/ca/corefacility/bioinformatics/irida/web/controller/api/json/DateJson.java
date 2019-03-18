package ca.corefacility.bioinformatics.irida.web.controller.api.json;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Class containing custom JSON serializers/deserializers for Dates.
 */
public class DateJson {

	/**
	 * Custom JSON serializer to format date as YYYY-MM-DD.
	 */
	public static class DateSerializer extends StdSerializer<Date> {

		private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

		private static final long serialVersionUID = 5016991815188473911L;

		protected DateSerializer() {
			super(Date.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeString(DATE_FORMAT.format(value));
		}
	}

	/**
	 * Custom JSON deserializer date from format YYYY-MM-DD.
	 */
	public static class DateDeserializer extends StdDeserializer<Date> {

		private static final long serialVersionUID = 3866202191160764291L;

		protected DateDeserializer() {
			super(Date.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			return Date.valueOf(p.getText());
		}
	}
}
