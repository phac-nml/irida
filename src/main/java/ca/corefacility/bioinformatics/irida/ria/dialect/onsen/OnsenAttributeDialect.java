package ca.corefacility.bioinformatics.irida.ria.dialect.onsen;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Thymeleaf Dialect for the AngularJS Onsen Mobile Framework
 * (http://onsenui.io/)
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenAttributeDialect extends AbstractDialect {
	/**
	 * Get the prefix used in the html document to refer to this dialect.
	 * 
	 * @return The prefix to use on the html attribute.
	 */
	@Override
	public String getPrefix() {
		return "ons";
	}

	/**
	 * Get the processors for this Thymeleaf Dialect.
	 *
	 * @return The onsen processor.
	 */
	@Override
	public Set<IProcessor> getProcessors() {
		HashSet<IProcessor> processors = new HashSet<>();
		processors.add(new OnsenProcessor());
		return processors;
	}
}
