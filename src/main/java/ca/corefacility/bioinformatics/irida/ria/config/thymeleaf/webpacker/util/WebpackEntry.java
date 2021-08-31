package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Each webpack entry with have at least one JavaScript file, and possibly css files and html (translatons)
 */
public class WebpackEntry {
	@JsonProperty("js")
	List<String> javascript;

	@JsonProperty("css")
	List<String> css;

	@JsonProperty("html")
	List<String> html;

	@Retention(RetentionPolicy.RUNTIME)
	@JacksonAnnotation
	public @interface SkipAssetsWrapper {
		String value();
	}

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
