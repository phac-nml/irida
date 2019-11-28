package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.List;
import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerUtilities;

public class WebpackerScriptAttributeTagProcessor extends AbstractAttributeTagProcessor {
	private static final String ATTR_NAME = "script";
	private static final int PRECEDENCE = 10000;
	private Map<String, Map<String, List<String>>> entryMap;

	public WebpackerScriptAttributeTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
		entryMap = WebpackerUtilities.getEntryMap();
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		if (entryMap.containsKey(attributeValue)) {
			// Since this is specifically for un-chunked, we only need the second item in the array
			// First item is always the runtime scripts which should already be on the page.
			String path = String.format("@{/dist/%s}", entryMap.get(attributeValue).get("js").get(1));
			structureHandler.setAttribute("data:script", path);
		}
	}
}
