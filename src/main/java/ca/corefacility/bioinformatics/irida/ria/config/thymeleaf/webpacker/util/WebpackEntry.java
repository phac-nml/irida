package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

import java.util.List;

/**
 * Each webpack entry with have at least one JavaScript file, and possibly css files and html (translatons)
 */
public class WebpackEntry {
		private final List<String> javascript;
		private final List<String> css;
		private final List<String> html;

		public WebpackEntry(List<String> javascript, List<String> css, List<String> html) {
			this.javascript = javascript;
			this.css = css;
			this.html = html;
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
