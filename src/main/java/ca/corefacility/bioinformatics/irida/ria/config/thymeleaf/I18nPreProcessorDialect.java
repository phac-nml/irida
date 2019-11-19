package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.util.Set;

import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.preprocessor.IPreProcessor;

/**
 * I18nPreProcessorDialect adds in a I18nPreProcessor to dynamically add in translations for JS Bundles
 */
public class I18nPreProcessorDialect implements IPreProcessorDialect {
	@Override
	public int getDialectPreProcessorPrecedence() {
		return 0;
	}

	@Override
	public Set<IPreProcessor> getPreProcessors() {
		return Set.of(new I18nPreProcessor());
	}

	@Override
	public String getName() {
		return "I18n Pre Processor Dialect";
	}
}
