package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.io.FileNotFoundException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.preprocessor.PreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * I18nPreProcessorDialect adds in a I18nPreProcessor to dynamically add in translations for JS Bundles
 */
public class I18nPreProcessorDialect implements IPreProcessorDialect {
	static final int PRECEDENCE = 0;

	@Override
	public int getDialectPreProcessorPrecedence() {
		return PRECEDENCE;
	}

	@Override
	public Set<IPreProcessor> getPreProcessors() {
		return Set.of(new PreProcessor(TemplateMode.HTML, I18nHandler.class, PRECEDENCE));
	}

	@Override
	public String getName() {
		return "I18n Pre Processor Dialect";
	}

	/**
	 * I18nHandler dynamically adds in translations required by JS bundles
	 */
	protected static class I18nHandler extends AbstractTemplateHandler {

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

		/**
		 * Processes each openElementTag and adds a translation block for script tags that load JS bundles,
		 * which have a translations file
		 *
		 * @param openElementTag
		 */
		@Override
		public void handleOpenElement(final IOpenElementTag openElementTag) {
			// only process script tags
			if (openElementTag.getElementCompleteName().equals("script")) {
				if (openElementTag.hasAttribute("th:src")) {
					Matcher matcher = pattern.matcher(openElementTag.getAttributeValue("th:src"));
					if (matcher.find()) {
						String bundle = matcher.group(1);
						if (doesTranslationsFileExist(bundle)) {
							String path = "../dist/i18n/" + bundle + " :: i18n";

							// Add in translations block for js bundle
							super.handleOpenElement(modelFactory.createOpenElementTag("th:block", "th:replace", path, false));
							super.handleCloseElement(modelFactory.createCloseElementTag("th:block"));
						}
					}
				}
			}
			super.handleOpenElement(openElementTag);
		}

		/**
		 * Check if a translation file exists for a given JS bundle
		 *
		 * @param bundleName Name of the JS bundle
		 * @return true if the {@param bundleName} has a translation file
		 */
		private boolean doesTranslationsFileExist(String bundleName) {
			try {
				return ResourceUtils.getFile("file:src/main/webapp/dist/i18n/" + bundleName + ".html").exists();
			} catch (FileNotFoundException e) {
				return false;
			}
		}
	}
}
