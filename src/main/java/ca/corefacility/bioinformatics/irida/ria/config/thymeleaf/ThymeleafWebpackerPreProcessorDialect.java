package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.*;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.preprocessor.PreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ThymeleafWebpackerPreProcessorDialect implements IPreProcessorDialect {
	static final int PRECEDENCE = 100;
	private static Map<String, Map<String, List<String>>> entryMap;

	public ThymeleafWebpackerPreProcessorDialect() {
		entryMap = getEntriesMapFromManifest();
	}

	@Override
	public int getDialectPreProcessorPrecedence() {
		return PRECEDENCE;
	}

	@Override
	public Set<IPreProcessor> getPreProcessors() {
		return ImmutableSet.of(new PreProcessor(TemplateMode.HTML, WebpackerHandler.class, PRECEDENCE));
	}

	@Override
	public String getName() {
		return "Thymeleaf Webpack Processor Dialect";
	}

	private Map<String, Map<String, List<String>>> getEntriesMapFromManifest() {
		Map<String, Map<String, List<String>>> entries = new HashMap<>();
		try {
			File manifestFile = ResourceUtils.getFile("file:src/main/webapp/dist/manifest.json");
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> manifest = objectMapper
					.readValue(manifestFile, new TypeReference<Map<String, Object>>() {
					});

			entries = (Map<String, Map<String, List<String>>>) manifest.get("entrypoints");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	protected static class WebpackerHandler extends AbstractTemplateHandler {
		private IModelFactory modelFactory;
		private final String WEBPACKER_TAG_PATTERN = "webpacker:(css|js)";
		private final Pattern webpacker_pattern = Pattern.compile(WEBPACKER_TAG_PATTERN);
		private final Map<String, String> typeToElementMap = ImmutableMap.of("css", "link", "js", "script");

		public WebpackerHandler() {
			super();
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
						chunks.forEach(chunk -> {
							IOpenElementTag openElementTag = modelFactory
									.createOpenElementTag(typeToElementMap.get(type), getAttributesForType(type, chunk), AttributeValueQuotes.DOUBLE, false);
							ICloseElementTag closeElementTag = modelFactory.createCloseElementTag("script");
							super.handleOpenElement(openElementTag);
							super.handleCloseElement(closeElementTag);
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
		public void setContext(ITemplateContext context) {
			super.setContext(context);
			this.modelFactory = context.getModelFactory();
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
}
