package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.util.List;

/**
 * Each webpack entry with have at least one JavaScript file, and possibly css files and html (translatons)
 */
public class WebpackEntry {
	private List<String> javascript;

	private List<String> css;

	private List<String> html;

	public List<String> getJavascript() {
		return javascript;
	}

	public void setJavascript(List<String> javascript) {
		this.javascript = javascript;
	}

	public List<String> getCss() {
		return css;
	}

	public void setCss(List<String> css) {
		this.css = css;
	}

	public List<String> getHtml() {
		return html;
	}

	public void setHtml(List<String> html) {
		this.html = html;
	}
}
