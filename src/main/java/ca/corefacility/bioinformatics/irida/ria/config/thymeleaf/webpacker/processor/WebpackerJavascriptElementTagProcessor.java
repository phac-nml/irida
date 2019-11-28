package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerUtilities;

import com.google.common.base.Strings;

public class WebpackerJavascriptElementTagProcessor extends AbstractElementTagProcessor {
	private static final String TAG_NAME = "js";
	private static final int PRECEDENCE = 1000;
	private static final String ELEMENT_NAME = "script";
	private static final String REQUEST_ATTR_NAME = "chunks";
	private static final String INTERNATIONALIZATION = "i18n";
	private static final String INTERNATIONALIZATION_ELEMENT = "th:block";
	private Map<String, Map<String, List<String>>> entryMap;

	public WebpackerJavascriptElementTagProcessor(final String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, TAG_NAME, true, null, false, PRECEDENCE);
		entryMap = WebpackerUtilities.getEntryMap();
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		WebEngineContext webEngineContext = (WebEngineContext) context;
		HttpServletRequest request = webEngineContext.getRequest();

		/*
		 * See if the Runtime Chunk is already on the page.
		 * Since all pages points can have more than one entry point,
		 * we only need to add the Runtime Chunk once.
		 */
		Object requestChunks = request.getAttribute(REQUEST_ATTR_NAME);
		Set<String> chunks = requestChunks != null ? (Set<String>) requestChunks : new HashSet<>();

		/*
		 * Check for internationalization.
		 */

		/*
		 * Read the 'entry' attribute from the tag.
		 */
		final String entry = tag.getAttributeValue("entry");
		if (!Strings.isNullOrEmpty(entry) && entryMap.containsKey(entry)) {
			final IModelFactory modelFactory = context.getModelFactory();
			final IModel model = modelFactory.createModel();

			// i18n
			final List<String> htmlResources = entryMap.get(entry).get("html");
			if (htmlResources != null) {
				htmlResources.forEach(file -> {
					if (file.startsWith(INTERNATIONALIZATION)) {
						model.add(
								modelFactory.createOpenElementTag(INTERNATIONALIZATION_ELEMENT, "th:replace", String.format("../dist/%s", file), false));
						model.add(modelFactory.createCloseElementTag(INTERNATIONALIZATION_ELEMENT));
					}
				});
			}

			List<String> resources = entryMap.get(entry).get("js");
			resources.forEach(chunk -> {
				if (!chunks.contains(chunk)) {
					chunks.add(chunk);
					model.add(modelFactory
							.createOpenElementTag(ELEMENT_NAME, "th:src", String.format("@{/dist/%s}", chunk)));
					model.add(modelFactory.createCloseElementTag(ELEMENT_NAME));
				}
			});
			structureHandler.replaceWith(model, true);
		}

		request.setAttribute(REQUEST_ATTR_NAME, chunks);
	}
}
