package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleFilesConcatenatePage;

/**
 * Test for concatenating sequence files page
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
public class SampleFilesConcatenatePageIT extends AbstractIridaUIITChromeDriver {
	public SampleFilesConcatenatePage page;

	@Before
	public void setUp() {
		LoginPage.loginAsManager(driver());
		page = SampleFilesConcatenatePage.goToConcatenatePage(driver(), 1L);
	}

	@Test
	public void testSelection() {
		page.selectPairs();

		assertEquals("Should be 1 selected sequence", 1, page.getSelectedCount());
		assertFalse("submit should be disabled as only 1 sequence selected", page.isSubmitEnabled());

		page.selectSingles();

		assertEquals("Should be 3 selected sequences", 3, page.getSelectedCount());
		assertFalse("submit should be disabled different types selected", page.isSubmitEnabled());

		page.uncheckAll();
		assertFalse("submit should be disabled as no samples selected", page.isSubmitEnabled());

		page.selectSingles();
		assertEquals("Should be 2 selected sequences", 2, page.getSelectedCount());
		assertTrue("submit should be enabled as 2 same type sequences selected", page.isSubmitEnabled());
	}
}
