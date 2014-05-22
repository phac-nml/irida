package ca.corefacility.bioinformatics.irida.ria.dialect.onsen;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;

import java.util.Collection;
import java.util.Map;

/**
 * This is used to during the processing of html templates to match onsen attributes.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenAttributeMatcher implements IProcessorMatcher<NestableAttributeHolderNode> {
	@Override
	public boolean matches(Node node, ProcessorMatchingContext processorMatchingContext) {
		NestableAttributeHolderNode element = (NestableAttributeHolderNode) node;

		if (processorMatchingContext.getDialect() instanceof OnsenAttributeDialect) {
			String dialectPrefix = processorMatchingContext.getDialectPrefix();
			Map<String, Attribute> attributeMap = element.getAttributeMap();
			Collection<Attribute> values = attributeMap.values();
			for (Attribute attribute : values) {
				String prefixFromAttributeName = Attribute.getPrefixFromAttributeName(attribute.getNormalizedName());
				if (dialectPrefix.equals(prefixFromAttributeName)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Class<? extends NestableAttributeHolderNode> appliesTo() {
		return NestableAttributeHolderNode.class;
	}
}
