package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.*;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.utilities.WebpackerUtilities;

import com.google.common.collect.ImmutableMap;

public class WebpackerTagHandler extends AbstractTemplateHandler {
	private IModelFactory modelFactory;
	private final String WEBPACKER_TAG_PATTERN = "webpacker:(css|js)";
	private final Pattern webpacker_pattern = Pattern.compile(WEBPACKER_TAG_PATTERN);
	private final Map<String, String> typeToElementMap = ImmutableMap.of("css", "link", "js", "script");
	private Map<String, Map<String, List<String>>> entryMap;
	private HttpServletRequest request;
	private List<String> addedChunks;

	public WebpackerTagHandler() {
		super();
		entryMap = WebpackerUtilities.getEntryMap();
	}

	@Override
	public void handleStandaloneElement(IStandaloneElementTag standaloneElementTag) {
		final String elementName = standaloneElementTag.getElementCompleteName();
		Matcher matcher = webpacker_pattern.matcher(elementName);
		if (matcher.find()) {
			String type = matcher.group(1);
			if (standaloneElementTag.hasAttribute("entry")) {
				String entry = standaloneElementTag.getAttributeValue("entry");
				if (entryMap.containsKey(entry) && entryMap.get(entry).containsKey(type)) {
					List<String> chunks = entryMap.get(entry).get(type);

					/*
					If this is a javascript entry, check to see if there are translations available.
					 */
					if (type.equals("js") && WebpackerUtilities.doesTranslationsFileExist(entry)) {
						String path = "../dist/i18n/" + entry + " :: i18n";

						// Add in translations block for js bundle
						super.handleOpenElement(modelFactory.createOpenElementTag("th:block", "th:replace", path, false));
						super.handleCloseElement(modelFactory.createCloseElementTag("th:block"));
					}

					/*
					Add new elements for each chunk.
					 */
					chunks.forEach(chunk -> {

						if (!addedChunks.contains(chunk)) {
							IOpenElementTag openElementTag = modelFactory
									.createOpenElementTag(typeToElementMap.get(type), getAttributesForType(type, chunk), AttributeValueQuotes.DOUBLE, false);
							ICloseElementTag closeElementTag = modelFactory.createCloseElementTag("script");
							super.handleOpenElement(openElementTag);
							super.handleCloseElement(closeElementTag);
							addedChunks.add(chunk);
						}

					});
				} else {
					IOpenElementTag warningDiv = modelFactory
							.createOpenElementTag("div", "style", "background-color: 'red'; ");
				}
			}
		} else {
			super.handleStandaloneElement(standaloneElementTag);
		}
	}

	@Override
	public void handleTemplateEnd(ITemplateEnd templateEnd) {
		request.setAttribute("added-chunks", addedChunks);
		super.handleTemplateEnd(templateEnd);
	}

	@Override
	public void setContext(ITemplateContext context) {
		super.setContext(context);
		this.modelFactory = context.getModelFactory();
		this.request = ((WebEngineContext) context).getRequest();

		this.addedChunks = (List<String>) this.request.getAttribute("added-chunks");
		if (this.addedChunks == null) {
			this.addedChunks = new ArrayList<>();
		}
	}

	private Map<String, String> getAttributesForType(String type, String chunk) {
		if (type.equals("css")) {
			return ImmutableMap.of("th:href", "@{/dist/" + chunk + "}", "rel", "stylesheet");
		} else if (type.equals("js")) {
			return ImmutableMap.of("th:src", "@{/dist/" + chunk + "}");
		} else {
			return ImmutableMap.of();
		}
	}
}
