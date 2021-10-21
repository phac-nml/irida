package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.util.Map;

/**
 * Container for the assets manifest file deserialization
 */
public class WebpackAssetsManifest {

	public Map<String, WebpackEntry> entries;

	public WebpackAssetsManifest(Map<String, WebpackEntry> entries) {
		this.entries = entries;
	}

	public Map<String, WebpackEntry> getEntries() {
		return entries;
	}
}
