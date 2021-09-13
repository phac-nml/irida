package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Deserializer for the webpack assets-manifest file
 */
public class WebpackAssetsManifestDeserializer extends StdDeserializer<WebpackAssetsManifest> {
	private static final Logger logger = LoggerFactory.getLogger(WebpackAssetsManifestDeserializer.class);

	public WebpackAssetsManifestDeserializer() {
		super(WebpackAssetsManifest.class);
	}

	@Override
	public WebpackAssetsManifest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(WebpackEntry.class, new WebpackEntryDeserializer());
		mapper.registerModule(module);

		TreeNode treeNode = jsonParser.readValueAsTree();

		Map<String, WebpackEntry> entries = new HashMap<>();
		final TreeNode entrypoints = treeNode.get("entrypoints");
		entrypoints
				.fieldNames()
				.forEachRemaining(name -> {
					try {
						WebpackEntry entry = mapper.readValue(entrypoints.get(name)
								.toString(), WebpackEntry.class);
						entries.put(name, entry);
					} catch (JsonProcessingException e) {
						logger.error("Cannot parse webpack entry, please check Webpack asset-manifest.json file", e);
					}
				});

		return new WebpackAssetsManifest(entries);
	}

}
