package ca.corefacility.bioinformatics.irida.ria.thymeleaf.dialect.onsen;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

/**
 * Thymeleaf dialect for the AngularJS Onsen Mobile Framework
 * (http://onsenui.io)
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenDialect extends AbstractDialect {
	public final static String ONSEN_DIALECT_PREFIX = "ons";

	/**
	 * <p>
	 * Returns the default dialect prefix (the one that will be used if none is
	 * explicitly specified during dialect configuration).
	 * </p>
	 * 
	 * @return the dialect prefix.
	 */
	@Override
	public String getPrefix() {
		return ONSEN_DIALECT_PREFIX;
	}

	@Override
	public Set<IProcessor> getProcessors() {
		final Set<IProcessor> processors = new HashSet<>();
		processors.add(new OnsenAttrProcessor(new OnsenAttrMatcher()));
		return processors;
	}
}
