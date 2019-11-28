package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf;

import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.google.common.collect.ImmutableSet;

public class WebpackerDialect extends AbstractProcessorDialect {
	private static final String DIALECT_NAME = "Webpacker Dialect";
	private static final String DIALECT_PREFIX = "webpacker";

	public WebpackerDialect() {
		super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		return ImmutableSet.of(new WebpackerAttributeTagProcessor(dialectPrefix));
	}
}
