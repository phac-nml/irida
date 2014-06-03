package ca.corefacility.bioinformatics.irida.ria.web;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for {@link PipelinesViewController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PipelinesViewControllerTest {
	private PipelinesViewController controller = new PipelinesViewController();

	@Test
	public void testGetPipelinesMainView() {
		assertEquals("views/pipelines", controller.getPipelinesView());
	}
}
