package ca.corefacility.bioinformatics.irida.ria.dialects;

import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons.IridaIconElementProcessor;

import com.google.common.collect.ImmutableSet;

/**
 * Thymeleaf dialect specifically for components of the IRIDA UI.
 */
public class IridaDialect extends AbstractDialect {
	private static final Set<IProcessor> PROCESSORS = ImmutableSet.of(new IridaIconElementProcessor());

	@Override public String getPrefix() {
		return "irida";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IProcessor> getProcessors() {
		return PROCESSORS;
	}
}
