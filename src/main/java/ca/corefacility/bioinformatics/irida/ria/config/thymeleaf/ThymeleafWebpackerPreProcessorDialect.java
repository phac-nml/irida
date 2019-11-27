package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.util.Set;

import org.thymeleaf.dialect.IPreProcessorDialect;
import org.thymeleaf.preprocessor.IPreProcessor;
import org.thymeleaf.preprocessor.PreProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.handlers.WebpackerTagHandler;

import com.google.common.collect.ImmutableSet;

public class ThymeleafWebpackerPreProcessorDialect implements IPreProcessorDialect {
	static final int PRECEDENCE = 100;

	@Override
	public int getDialectPreProcessorPrecedence() {
		return PRECEDENCE;
	}

	@Override
	public Set<IPreProcessor> getPreProcessors() {
		return ImmutableSet.of(new PreProcessor(TemplateMode.HTML, WebpackerTagHandler.class, PRECEDENCE));
	}

	@Override
	public String getName() {
		return "Thymeleaf Webpack Processor Dialect";
	}
}
