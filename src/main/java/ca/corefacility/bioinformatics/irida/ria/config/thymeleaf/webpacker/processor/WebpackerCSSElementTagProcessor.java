package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.List;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.WebpackerDialect;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerTagType;

import com.google.common.collect.ImmutableMap;

/**
 * Thymeleaf Tag Processor for elements of the form: <code><webpack:css entry="entry_name" /></code>
 *
 * This processor will:
 * - determine which css files need to be loaded from the webpack manifest file.
 * - create new link elements with the correct path to the files and add them to the template.
 */
public class WebpackerCSSElementTagProcessor extends AbstractElementTagProcessor {
	private static final WebpackerTagType TAG_TYPE = WebpackerTagType.CSS;
	private static final String ELEMENT_NAME = "link";
	private static final int PRECEDENCE = 1000;

	public WebpackerCSSElementTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, TAG_TYPE.toString(), true, null, false, PRECEDENCE);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		final String entry = tag.getAttributeValue(WebpackerDialect.ENTRY_ATTR);

		/*
		 * Look into the manifests and pull out all the chunks for the given entry and type.
		 */
		List<String> chunks = WebpackerManifestParser.getChunksForEntryType(entry, TAG_TYPE);

		if (chunks != null) {
			final IModelFactory modelFactory = context.getModelFactory();
			final IModel model = modelFactory.createModel();

			/*
			 * For each chunk we are going to create a new link formatted for thymeleaf to
			 * ensure the link is created properly for the container.
			 */
			chunks.forEach(chunk -> {
				model.add(modelFactory.createOpenElementTag(ELEMENT_NAME,
						ImmutableMap.of("th:href", String.format("@{/dist/%s}", chunk), "rel", "stylesheet"),
						AttributeValueQuotes.DOUBLE, false));
				model.add(modelFactory.createCloseElementTag(ELEMENT_NAME));
			});
			structureHandler.replaceWith(model, true);
		}
	}
}
