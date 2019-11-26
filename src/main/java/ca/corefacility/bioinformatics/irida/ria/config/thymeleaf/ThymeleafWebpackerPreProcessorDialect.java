package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.util.Set;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.ImmutableSet;

public class ThymeleafWebpackerPreProcessorDialect implements IPreProcessorDialect {
	@Override
	public int getDialectPreProcessorPrecedence() {
		return 1;
	}

	@Override
	public Set<IPreProcessor> getPreProcessors() {
		return ImmutableSet.of(new WebpackerPreProcessor());
	}

	@Override
	public String getName() {
		return "Thymeleaf Webpack Processor Dialect";
	}

	protected static class WebpackerPreProcessor implements IPreProcessor {
		@Override
		public TemplateMode getTemplateMode() {
			return TemplateMode.HTML;
		}

		@Override
		public int getPrecedence() {
			return 0;
		}

		@Override
		public Class<? extends ITemplateHandler> getHandlerClass() {
			return ThymeleafWebpackerPreProcessorDialect.WebpackerHandler.class;
		}
	}

	protected static class WebpackerHandler extends AbstractTemplateHandler {
		private IModelFactory modelFactory;

		public WebpackerHandler(){super();}

		@Override
		public void handleStandaloneElement(IStandaloneElementTag standaloneElementTag) {
			if (standaloneElementTag.getElementCompleteName()
					.equals("webapcker:js")) {
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
