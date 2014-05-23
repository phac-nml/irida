package ca.corefacility.bioinformatics.irida.ria.thymeleaf.dialect.onsen;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Processor class for the {@link OnsenDialect}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenAttrProcessor extends AbstractAttrProcessor {

	private final static int ONSEN_PROCESSOR_PRECEDENCE = 10000;
ß
	public OnsenAttrProcessor(IAttributeNameProcessorMatcher matcher) {
		super(matcher);
	}

	/**
	 * Process attribute that belong to the {@link OnsenDialect}
	 * 
	 * @param arguments
	 *            Contains all the required arguments for template processing.
	 * @param element
	 *            Thymeleaf DOM element containing the attribute.
	 * @param attributeName
	 *            Name of the attribute.
	 * @return The result of the process.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
		final String attributeValue = element.getAttributeValue(attributeName);

		// Configure the normal thymeleaf standard expressions.
		Configuration configuration = arguments.getConfiguration();
		final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);
		IStandardExpression expression = expressionParser.parseExpression(arguments.getConfiguration(), arguments,
				attributeValue);
		final Object result = expression.execute(configuration, arguments);

		// Get the actual tag name that we want (e.g. ons:page => page)
		String unprefixedAttributeName = Attribute.getUnprefixedAttributeName(attributeName);

		if (result != null) {
			element.setAttribute(unprefixedAttributeName, result.toString());
		}

		// Remove the initial value since we do not want it anymore and I do not
		// want it in the template!
		element.removeAttribute(attributeName);

		return ProcessorResult.OK;
	}

	@Override
	public int getPrecedence() {
		return ONSEN_PROCESSOR_PRECEDENCE;
	}
}
