package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.List;

import org.thymeleaf.context.ITemplateContext;
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

	public WebpackerScriptAttributeTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		List<String> jsResources = WebpackerManifestParser.getChunksForEntryType(attributeValue, WebpackerTagType.JS);

		if (jsResources != null) {
			// Since this is specifically for un-chunked, we only need the second item in the array
			// First item is always the runtime scripts which should already be on the page.
			String path = String.format("@{/dist/%s}", jsResources.get(1));
			structureHandler.setAttribute("data:script", path);
		}
	}
}
