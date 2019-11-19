package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * I18nPreProcessor which uses I18nHandler to dynamically add in translations required by JS bundles
 */
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
