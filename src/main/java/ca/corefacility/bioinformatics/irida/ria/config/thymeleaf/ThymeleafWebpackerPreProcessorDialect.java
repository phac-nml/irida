package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.util.Set;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.preprocessor.PreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.ImmutableSet;

public class ThymeleafWebpackerPreProcessorDialect implements IPreProcessorDialect {
	static final int PRECEDENCE = 100;

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

	protected static class WebpackerHandler extends AbstractTemplateHandler {
		private IModelFactory modelFactory;

		public WebpackerHandler(){super();}

		@Override
		public void handleStandaloneElement(IStandaloneElementTag standaloneElementTag) {
			if (standaloneElementTag.getElementCompleteName()
					.equals("webpacker:js")) {
				if (standaloneElementTag.hasAttribute("entry")) {
					String entry = standaloneElementTag.getAttributeValue("entry");
					IOpenElementTag openElementTag = modelFactory.createOpenElementTag("script", "th:src",
							"@{/dist/js/" + entry + ".bundle.js}");
					ICloseElementTag closeElementTag = modelFactory.createCloseElementTag("script");
					super.handleOpenElement(openElementTag);
					super.handleCloseElement(closeElementTag);
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
	}
}
