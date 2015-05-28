package ca.corefacility.bioinformatics.irida.ria.dialects;

import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons.FontAwesomeIconElementProcessor;

import com.google.common.collect.ImmutableSet;

/**
 * Thymeleaf dialect specifically for components of the IRIDA UI.
 */
public class FontAwesomeDialect extends AbstractDialect {
	private static final String FONT_AWESOME_TAG_PREFIX = "fa";
	private static final Set<IProcessor> PROCESSORS = ImmutableSet.of(new FontAwesomeIconElementProcessor());

	@Override
	public String getPrefix() {
		return FONT_AWESOME_TAG_PREFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IProcessor> getProcessors() {
		return PROCESSORS;
	}
}
