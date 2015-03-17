package ca.corefacility.bioinformatics.irida.repositories.remote.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation of {@code JsonDeserializer<RESTLinks>} to deserialize json links into
 * a {@link RESTLinks} object
 * 
 *
 */
public class RESTLinksDeserializer extends JsonDeserializer<RESTLinks> {
	private static final Logger logger = LoggerFactory.getLogger(RESTLinksDeserializer.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RESTLinks deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonNode tree = jp.getCodec().readTree(jp);
		
		logger.trace("Deserializing links");

		if (!tree.isArray()) {
			throw new JsonMappingException("Array of links expected.");
		}

		RESTLinks restLinks = new RESTLinks();

		for (JsonNode node : tree) {
			logger.trace("Deserializing link: " + node.toString());

			String rel = node.get("rel").asText();
			String href = node.get("href").asText();

			restLinks.setHrefForRel(rel, href);
		}

		return restLinks;
	}

}
