package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker;

import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor.WebpackerCSSElementTagProcessor;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor.WebpackerJavascriptElementTagProcessor;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor.WebpackerScriptAttributeTagProcessor;

import com.google.common.collect.ImmutableSet;

/**
 * Webpacker Dialect.This is the class containing the implementation of the Webpacker Dialect,
 * including all  {@code webpacker:*} processors, expression objects, etc.
 */
public class WebpackerDialect extends AbstractProcessorDialect {
	private static final String DIALECT_NAME = "Webpacker Dialect";
	private static final String DIALECT_PREFIX = "webpacker";
	public static final String ENTRY_ATTR = "entry";

	public WebpackerDialect() {
		super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		return ImmutableSet.of(new WebpackerScriptAttributeTagProcessor(dialectPrefix),
				new WebpackerCSSElementTagProcessor(dialectPrefix),
				new WebpackerJavascriptElementTagProcessor(dialectPrefix));
	}
}
