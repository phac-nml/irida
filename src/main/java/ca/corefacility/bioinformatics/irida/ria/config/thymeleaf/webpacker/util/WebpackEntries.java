package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data type to help a map of all the names of webpack entries with their corresponding assets.
 */
public class WebpackEntries extends HashMap<String, WebpackEntry>  {
	public WebpackEntries(Map<String, Map<String, Map<String, List<String>>>> entrypoints) {
		Set<String> entries = entrypoints.keySet();
			entries.forEach(entry -> {
				Map<String, List<String>> assets = entrypoints.get(entry)
						.get("assets");
				this.put(entry, new WebpackEntry(assets.get("js"), assets.get("css"), assets.get("html")));
			});
	}
}
