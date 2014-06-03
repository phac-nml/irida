package ca.corefacility.bioinformatics.irida.ria.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit Tests for {@link FilesViewController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class FilesViewControllerTest {
	private FilesViewController controller = new FilesViewController();

	@Test
	public void testGetFilesMainView() {
		assertEquals("views/files", controller.getFilesView());
	}
}
