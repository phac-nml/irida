package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Each webpack entry with have at least one JavaScript file, and possibly css files and html (translatons)
 */
public class WebpackEntry {
	private List<String> javascript;
	private List<String> css;
	private List<String> html;

	public WebpackEntry(JsonNode jsonNode) {
		this.javascript = mapNodeToList(jsonNode.get("js"));
		this.css = mapNodeToList(jsonNode.get("css"));
		this.html = mapNodeToList(jsonNode.get("html"));
	}

	private List<String> mapNodeToList(JsonNode jsonNode) {
		List<String> entries = new ArrayList<>();
		if (jsonNode != null && jsonNode.isArray()) {
			for (JsonNode node : jsonNode) {
				entries.add(node.textValue());
			}
		}
		return entries;
	}

	public List<String> getJavascript() {
		return javascript;
	}

	public List<String> getCss() {
		return css;
	}

	public List<String> getHtml() {
			return html;
		}
}
