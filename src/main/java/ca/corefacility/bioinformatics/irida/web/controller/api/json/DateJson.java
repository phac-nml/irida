package ca.corefacility.bioinformatics.irida.web.controller.api.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Custom JSON serializer to format date as yyyy-MM-DD.
	 */
	public static class DateSerializer extends StdSerializer<Date> {

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
	 * Custom JSON deserializer date from format yyyy-MM-DD.
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
			try {
				return DATE_FORMAT.parse(p.getText());
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
