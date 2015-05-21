package ca.corefacility.bioinformatics.irida.ria.dialects;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import ca.corefacility.bioinformatics.irida.ria.dialects.processors.IridaIconElementProcessor;

/**
 * Thymeleaf dialect specifically for components of the IRIDA UI.
 */
public class IridaDialect extends AbstractDialect {
	@Override public String getPrefix() {
		return "irida";
	}

	@Override public Set<IProcessor> getProcessors() {
		final Set<IProcessor> processors = new HashSet<>();
		processors.add(new IridaIconElementProcessor());
		return processors;
	}
}
