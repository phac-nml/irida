package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.*;

/**
 * I18nHandler is a TemplateHandler that adds in translations dynamically which are required by JS Bundles
 */
public class I18nHandler extends AbstractTemplateHandler {

	private static final Logger logger = LoggerFactory.getLogger(I18nHandler.class);
	private IModelFactory modelFactory;
	private final String BUNDLE_PATTERN = "dist/js/(.*).bundle.js";
	private final Pattern pattern = Pattern.compile(BUNDLE_PATTERN);

	public I18nHandler() {
		super();
	}

	@Override
	public void setContext(ITemplateContext context) {
		super.setContext(context);
		this.modelFactory = context.getModelFactory();
	}

	@Override
	public void handleOpenElement(final IOpenElementTag openElementTag) {
		if (openElementTag.getElementCompleteName().equals("script")) {
			if (openElementTag.hasAttribute("th:src")) {
				Matcher matcher = pattern.matcher(openElementTag.getAttributeValue("th:src"));
				if (matcher.find()) {
					String bundle = matcher.group(1);
					if (doesTranslationFileExist(bundle)) {
						String path = "../dist/i18n/" + bundle + " :: i18n";
						super.handleOpenElement(modelFactory.createOpenElementTag("th:block", "th:replace", path, false));
						super.handleCloseElement(modelFactory.createCloseElementTag("th:block"));
					}
				}
			}
		}
		super.handleOpenElement(openElementTag);
	}

	private boolean doesTranslationFileExist(String filename) {
		try {
			return ResourceUtils.getFile("file:src/main/webapp/dist/i18n/" + filename + ".html").exists();
		} catch (FileNotFoundException e) {
			return false;
		}
	}
}
