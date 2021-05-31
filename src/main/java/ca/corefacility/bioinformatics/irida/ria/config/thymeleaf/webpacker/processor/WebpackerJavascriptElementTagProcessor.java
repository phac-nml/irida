package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.WebpackerDialect;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerTagType;

/**
 * Thymeleaf Tag Processor for webpacker elements.
 *
 * This processor will:
 * - determine which js chunks need to be loaded from the webpack manifest file.
 * - create new script element with the correct path to the files and add them to the template.
 */
public class WebpackerJavascriptElementTagProcessor extends AbstractElementTagProcessor {
	private static final WebpackerTagType TAG_TYPE = WebpackerTagType.JS;
	private static final int PRECEDENCE = 1000;
	private static final String JAVASCRIPT_TAG = "script";
	private static final String REQUEST_CHUNKS = "runtime_chunk";
	private static final String INTERNATIONALIZATION_PREFIX = "i18n";
	private static final String INTERNATIONALIZATION_ATTR = "th:replace";
	private static final String INTERNATIONALIZATION_TAG = "th:block";
	private static final String JAVASCRIPT_ATTR = "th:src";

	public WebpackerJavascriptElementTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, TAG_TYPE.toString(), true, null, false, PRECEDENCE);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {

		/*
		 * Since multiple entry points can be added to a single page (e.g. base template > page template > samples template),
		 * we need to ensure that the same link is not added more than once.  Keeping a set of the existing chunks
		 * allows us to ensure that a chunk is only added once to the page.
		 */
		Set<String> existingChunks = getExistingChunksFromRequest(context);

		/*
		 * Read the 'entry' attribute from the tag.
		 */
		String entry = tag.getAttributeValue(WebpackerDialect.ENTRY_ATTR);

		if (entry != null) {
			IModelFactory modelFactory = context.getModelFactory();
			IModel model = modelFactory.createModel();

			/*
			 * Each javascript entry may require its own internationalization messages.  There is a custom webpack
			 * plugin (i18nThymeleafWebpackPlugin) which creates Thymeleaf fragments for these messages, and adds
			 * the path to the webpack manifest.  We will get Thymeleaf to insert the fragments above the entry
			 * script.
			 */
			ServletContext servletContext = ((WebEngineContext) context).getServletContext();
			List<String> htmlResources = WebpackerManifestParser.getChunksForEntryType(servletContext, entry,
					WebpackerTagType.HTML);
			if (htmlResources != null) {
				htmlResources.forEach(file -> {
					if (file.contains(INTERNATIONALIZATION_PREFIX)) {
						model.add(modelFactory.createOpenElementTag(INTERNATIONALIZATION_TAG, INTERNATIONALIZATION_ATTR,
								String.format("templates/i18n/%s :: i18n", entry), false));
						model.add(modelFactory.createCloseElementTag(INTERNATIONALIZATION_TAG));
					}
				});
			}

			/*
			 * Add all javascript chunks for this entry to the page.
			 */
			List<String> jsResources = WebpackerManifestParser.getChunksForEntryType(servletContext, entry,
					WebpackerTagType.JS);
			if (jsResources != null) {
				jsResources.forEach(chunk -> {
					if (!existingChunks.contains(chunk)) {
						existingChunks.add(chunk);
						model.add(modelFactory.createOpenElementTag(JAVASCRIPT_TAG, JAVASCRIPT_ATTR,
								String.format("@{/dist/%s}", chunk)));
						model.add(modelFactory.createCloseElementTag(JAVASCRIPT_TAG));
					}
				});
			}
			structureHandler.replaceWith(model, true);
		}

		setExistingChunksInRequest(context, existingChunks);
	}

	/**
	 * Get a {@link Set} of javascript chunks currently on the page.  This is done
	 * to prevent any given chunk to be added multiple times if entry points on the page
	 * have common chunks.
	 * <p>
	 * This has a suppressed warning for unchecked because anything stored into a request is
	 * an Object and we KNOW we set it as a Set.
	 *
	 * @param context - {@link ITemplateContext} for the current template.
	 * @return {@link Set} of all javascript chunks currently added to the template.
	 */
	@SuppressWarnings("unchecked")
	private Set<String> getExistingChunksFromRequest(ITemplateContext context) {
		WebEngineContext webEngineContext = (WebEngineContext) context;
		HttpServletRequest request = webEngineContext.getRequest();
		Object chunksObject = request.getAttribute(REQUEST_CHUNKS);
		return chunksObject != null ? (Set<String>) chunksObject : new HashSet<>();
	}

	/**
	 * Update the request with the all javascript chunks which are now loaded into the template.
	 *
	 * @param context - {@link ITemplateContext} for the current template.
	 * @param chunks  - {@link Set} of all javascript chunks currently added to the template.
	 */
	private void setExistingChunksInRequest(ITemplateContext context, Set<String> chunks) {
		WebEngineContext webEngineContext = (WebEngineContext) context;
		HttpServletRequest request = webEngineContext.getRequest();
		request.setAttribute(REQUEST_CHUNKS, chunks);
	}
}
