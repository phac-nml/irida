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
	@Override
	public String getPrefix() {
		return "ons";
	}

	@Override
	public Set<IProcessor> getProcessors() {
		HashSet<IProcessor> processors = new HashSet<>();
		processors.add(new OnsenProcessor());
		return processors;
	}
}
