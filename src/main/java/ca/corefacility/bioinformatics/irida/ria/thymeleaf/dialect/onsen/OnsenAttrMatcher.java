package ca.corefacility.bioinformatics.irida.ria.thymeleaf.dialect.onsen;

import java.util.Collection;
import java.util.Map;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;

/**
 * Process matcher for the {@link OnsenDialect}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenAttrMatcher implements IAttributeNameProcessorMatcher {

	@Override
	public String[] getAttributeNames(ProcessorMatchingContext context) {
		String[] attrNames = { "ons:page" };
		return attrNames;
	}

	/**
	 * <p>
	 * Try to match the node, using the specified matching context.
	 * </p>
	 * 
	 * @param node
	 *            the node to be matched
	 * @param context
	 *            the matching context
	 * @return true if the node matches, false if not.
	 */
	@Override
	public boolean matches(Node node, ProcessorMatchingContext context) {
		NestableAttributeHolderNode element = (NestableAttributeHolderNode) node;

		if (context.getDialect() instanceof OnsenDialect) {
			String dialectPrefix = context.getDialectPrefix();
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

	/**
	 * <p>
	 * Returns the type of Node this matcher applies to (and therefore the type
	 * of Node that processors with this matcher will apply to).
	 * </p>
	 * 
	 * @return the type of node (subclass of Node) this matcher applies to.
	 */
	@Override
	public Class<? extends NestableAttributeHolderNode> appliesTo() {
		return NestableAttributeHolderNode.class;
	}
}
