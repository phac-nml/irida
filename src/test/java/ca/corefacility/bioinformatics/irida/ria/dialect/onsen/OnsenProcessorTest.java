package ca.corefacility.bioinformatics.irida.ria.dialect.onsen;

import static org.hamcrest.Matchers.instanceOf;
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
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.IProcessorMatcher;
import org.thymeleaf.processor.ProcessorMatchingContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Unit test for {@link OnsenProcessor}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Arguments.class, StandardExpressions.class })
public class OnsenProcessorTest {
	OnsenProcessor onsenProcessor;

	@Before
	public void setUp() {
		onsenProcessor = new OnsenProcessor();
	}

	@Test
	public void testGetMatcher() throws Exception {
		IProcessorMatcher<? extends Node> matcher = onsenProcessor.getMatcher();
		assertNotNull(matcher);
		assertThat(matcher, instanceOf(OnsenAttributeMatcher.class));
	}

	@Test
	public void testProcess() throws Exception {
		Element elm = new Element("div");
		elm.setAttribute("ons:page", "@{/views/dashboard}");
		ProcessorMatchingContext processorMatchingContext = new ProcessorMatchingContext(new OnsenAttributeDialect(),
				"ons");
		mockStandardExpression("/views/dashboard");
		Arguments args = PowerMockito.mock(Arguments.class);
		onsenProcessor.doProcess(args, processorMatchingContext, elm);
		assertFalse("No 'ons:page' attribute is left", elm.hasAttribute("ons:page"));
		assertTrue("'ons:page' was replaced with 'page'", elm.hasAttribute("page"));
		assertEquals("'page' attribute has the value of '/views/dashboard'", elm.getAttributeValue("page"),
				"/views/dashboard");
	}

	@Test
	public void testPrecedence() {
		assertEquals("precedence is 1200", onsenProcessor.getPrecedence(), 1200);
	}

	public void mockStandardExpression(final String parseResult) {

		final IStandardExpression expression = Mockito.mock(
				IStandardExpression.class, (Answer<Object>) invocation -> parseResult);
		final IStandardExpressionParser expressionParser = Mockito.mock(
				IStandardExpressionParser.class, (Answer<Object>) invocation -> expression
		);
		PowerMockito.mockStatic(StandardExpressions.class,
				invocation -> expressionParser);
	}
}
