package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker;

import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor.WebpackerCSSElementTagProcessor;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor.WebpackerJavascriptElementTagProcessor;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.processor.WebpackerScriptAttributeTagProcessor;
import ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util.WebpackerManifestParser;

import com.google.common.collect.ImmutableSet;

/**
 * Webpacker Dialect.
 *
 * This is class contains the implementation of the Webpacker Dialect,
 * including all  {@code webpacker:*} processors, expression objects, etc.
 */
public class WebpackerDialect extends AbstractProcessorDialect {
	private static final String DIALECT_NAME = "Webpacker Dialect";
	private static final String DIALECT_PREFIX = "webpacker";
	public static final String ENTRY_ATTR = "entry";
	private final WebpackerManifestParser parser;

	public WebpackerDialect(boolean updatable) {
		super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
		this.parser = new WebpackerManifestParser(updatable);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		return ImmutableSet.of(new WebpackerScriptAttributeTagProcessor(dialectPrefix, parser),
				new WebpackerCSSElementTagProcessor(dialectPrefix, parser),
				new WebpackerJavascriptElementTagProcessor(dialectPrefix, parser));
	}
}
