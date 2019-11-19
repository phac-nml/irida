package ca.corefacility.bioinformatics.irida.ria.web.template.preprocessors;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.web.template.handlers.I18nHandler;

public class I18nPreProcessor implements IPreProcessor {
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
		return I18nHandler.class;
	}
}
