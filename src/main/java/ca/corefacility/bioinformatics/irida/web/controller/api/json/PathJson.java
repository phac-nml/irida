package ca.corefacility.bioinformatics.irida.web.controller.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Serialization class for java Path objects.  Previously this was handled by Jackson but in an upgrade it changed to
 * serializing Paths as the full URI prepended with "file://".  That doesn't work well for our REST API so this will
 * handle it instead.
 */
public class PathJson {
	/**
	 * Serializer for Java Path objects.
	 */
	public static class PathSerializer extends StdSerializer<Path> {

		public PathSerializer() {
			super(Path.class);
		}

		@Override
		public void serialize(Path value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeString(value.toFile()
					.toString());
		}
	}
}
