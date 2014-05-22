package ca.corefacility.bioinformatics.irida.ria.dialect.onsen;

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
public class OnsenAttributeMatcherTest {
	private OnsenAttributeMatcher onsenDialectMatcher;

	@Before
	public void setUp() {
		this.onsenDialectMatcher = new OnsenAttributeMatcher();
	}

	@Test
	public void testAppliesTo() throws Exception {
		assertEquals(NestableAttributeHolderNode.class, onsenDialectMatcher.appliesTo());
	}

	@Test
	public void testMatcher() throws Exception {
		Element elm = new Element("div");
		elm.setAttribute("ons:page", "@{/views/dashboard}");
		ProcessorMatchingContext processorMatchingContext = new ProcessorMatchingContext(new OnsenAttributeDialect(),
				"ons");
		assertTrue("matcher matches", onsenDialectMatcher.matches(elm, processorMatchingContext));
	}
}
