package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.List;

import javax.servlet.ServletContext;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerTagType;

/**
 * Thymeleaf Tag Processor for elements with the attr `webpack:script="entry_name"`
 *
 * This is a special case handler for when the link to an entry is within an attribute on an
 * element.  This only exists on the project samples page to handling opening modals directly.
 */
public class WebpackerScriptAttributeTagProcessor extends AbstractAttributeTagProcessor {
	private static final String ATTR_NAME = "script";
	private static final int PRECEDENCE = 10000;
	private final WebpackerManifestParser parser;

	public WebpackerScriptAttributeTagProcessor(String dialectPrefix, WebpackerManifestParser parser) {
		super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
		this.parser = parser;
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		ServletContext servletContext = ((WebEngineContext) context).getServletContext();
		List<String> jsResources = parser.getChunksForEntryType(servletContext, attributeValue,
				WebpackerTagType.JS);

		if (jsResources != null) {
			// Since this is specifically for un-chunked, we only need the second item in the array
			// In development this is at index 0 because all are unchunked, in production it is at index 1,
			// but in both cases this is n -1.
			String path = String.format("@{/dist/%s}", jsResources.get(jsResources.size() - 1));
			structureHandler.setAttribute("data:script", path);
		}
	}
}
