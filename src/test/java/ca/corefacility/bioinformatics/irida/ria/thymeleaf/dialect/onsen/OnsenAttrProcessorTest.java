package ca.corefacility.bioinformatics.irida.ria.thymeleaf.dialect.onsen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Unit test for {@link OnsenAttrProcessor}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Arguments.class, StandardExpressions.class })
public class OnsenAttrProcessorTest {
	private final String PAGE_ATTR = "ons:page";
	private OnsenAttrProcessor processor;

	@Before
	public void setUp() {
		processor = new OnsenAttrProcessor(new OnsenAttrMatcher());
	}

	@Test
	public void testProcessAttribute() throws Exception {
		Element element = new Element("div");
		element.setAttribute(PAGE_ATTR, "@{/views/dashboard}");
		Arguments arguments = PowerMockito.mock(Arguments.class);
		mockStandardExpression("/views/dashboard");
		processor.processAttribute(arguments, element, PAGE_ATTR);
		assertFalse("No 'ons:page' attribute is left", element.hasAttribute("ons:page"));
		assertTrue("'ons:page' was replaced with 'page'", element.hasAttribute("page"));
		assertEquals("'page' attribute has the value of '/views/dashboard'", element.getAttributeValue("page"),
				"/views/dashboard");
	}

	@Test
	public void testGetPrecedence() throws Exception {
		assertEquals(processor.getPrecedence(), 10000);
	}

	private void mockStandardExpression(final String parseResult) {

		final IStandardExpression expression = Mockito.mock(
				IStandardExpression.class, (Answer<Object>) invocation -> parseResult);
		final IStandardExpressionParser expressionParser = Mockito.mock(
				IStandardExpressionParser.class, (Answer<Object>) invocation -> expression
		);
		PowerMockito.mockStatic(StandardExpressions.class,
				invocation -> expressionParser);
	}
}
