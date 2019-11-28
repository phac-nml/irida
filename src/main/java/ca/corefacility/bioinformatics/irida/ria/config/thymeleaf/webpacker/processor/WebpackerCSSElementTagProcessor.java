package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.List;
import java.util.Map;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerUtilities;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

public class WebpackerCSSElementTagProcessor extends AbstractElementTagProcessor {
	private static final String TAG_NAME = "css";
	private static final String ELEMENT_NAME = "link";
	private static final int PRECEDENCE = 1000;

	public WebpackerCSSElementTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		Map<String, Map<String, List<String>>> entryMap = WebpackerUtilities.getEntryMap();

		final String entry = tag.getAttributeValue("entry");
		if (!Strings.isNullOrEmpty(entry) && entryMap.containsKey(entry)) {
			final IModelFactory modelFactory = context.getModelFactory();
			final IModel model = modelFactory.createModel();

			List<String> resources = entryMap.get(entry).get("css");
			resources.forEach(chunk -> {
				model.add(modelFactory.createOpenElementTag(ELEMENT_NAME,
						ImmutableMap.of("th:href", String.format("@{/dist/%s}", chunk), "rel", "stylesheet"),
						AttributeValueQuotes.DOUBLE, false));
				model.add(modelFactory.createCloseElementTag(ELEMENT_NAME));
			});
			structureHandler.replaceWith(model, true);
		}
	}
}
