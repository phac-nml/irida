package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.dialects.login;

import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.google.common.collect.ImmutableSet;

public class LoginProcessorDialect extends AbstractProcessorDialect {
	private static final String DIALECT_NAME = "Login Dialect";
	private static final String DIALECT_PREFIX = "login";

	public LoginProcessorDialect() {
		super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String s) {
		return ImmutableSet.of(new LoginAttributeTagProcessor(DIALECT_PREFIX));
	}
}
