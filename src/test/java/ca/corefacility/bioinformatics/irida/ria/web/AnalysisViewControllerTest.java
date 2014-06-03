package ca.corefacility.bioinformatics.irida.ria.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit Tests for {@link AnalysisViewController}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class AnalysisViewControllerTest {
	private AnalysisViewController controller = new AnalysisViewController();

	@Test
	public void testGetAnalysisMainView() {
		assertEquals("views/analysis", controller.getAnalysisMainView());
	}
}
