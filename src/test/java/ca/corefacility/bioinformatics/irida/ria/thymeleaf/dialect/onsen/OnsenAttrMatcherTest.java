package ca.corefacility.bioinformatics.irida.ria.thymeleaf.dialect.onsen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableAttributeHolderNode;
import org.thymeleaf.processor.ProcessorMatchingContext;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class OnsenAttrMatcherTest {
	private OnsenAttrMatcher matcher;

	@Before
	public void setUp() {
		matcher = new OnsenAttrMatcher();
	}

	@Test
	public void testAppliesTo() {
		assertEquals(NestableAttributeHolderNode.class, matcher.appliesTo());
	}

	@Test
	public void testMatcher() {
		Element element = new Element("div");
		element.setAttribute("ons:page", "@{/views/dashbaord}");
		ProcessorMatchingContext context = new ProcessorMatchingContext(new OnsenDialect(), "ons");
		assertTrue("matcher matches", matcher.matches(element, context));
	}
}
