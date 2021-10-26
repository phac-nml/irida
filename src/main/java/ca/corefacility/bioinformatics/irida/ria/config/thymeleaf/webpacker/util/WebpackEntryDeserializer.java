package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.WebpackParserException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Deserializer for the webpack manifest file entry.
 */
public class WebpackEntryDeserializer extends StdDeserializer<WebpackEntry> {

	public WebpackEntryDeserializer() {
		super(WebpackEntry.class);
	}

	@Override
	public WebpackEntry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException {
		JsonNode entryNode = jsonParser.getCodec()
				.readTree(jsonParser);
		JsonNode assetsNode = entryNode.get("assets");

		if (assetsNode == null || assetsNode.isEmpty()) {
			throw new WebpackParserException("Could not find assets");
		}

		WebpackEntry entry = new WebpackEntry();
		if (!assetsNode.get("js")
				.isEmpty()) {
			entry.setJavascript(getPathsFromNode(assetsNode.get("js")));
		} else {
			throw new WebpackParserException("No JavaScript assets could found for entry");
		}
		if (assetsNode.get("css") != null) {
			entry.setCss(getPathsFromNode(assetsNode.get("css")));
		}
		if (assetsNode.get("html") != null) {
			entry.setHtml(getPathsFromNode(assetsNode.get("html")));
		}

		return entry;
	}

	private List<String> getPathsFromNode(JsonNode node) {
		List<String> paths = new ArrayList<>();
		for (JsonNode jsonNode : node) {
			paths.add(jsonNode.textValue());
		}
		return paths;
	}
}
