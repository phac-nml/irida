package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

	@BeforeEach
	public void setUp() {
		LoginPage.loginAsManager(driver());
		page = SampleFilesConcatenatePage.goToConcatenatePage(driver(), 1L);
	}

	@Test
	public void testSelection() {
		page.selectPairs();

		assertEquals(1, page.getSelectedCount(), "Should be 1 selected sequence");
		assertFalse(page.isSubmitEnabled(), "submit should be disabled as only 1 sequence selected");

		page.selectSingles();

		assertEquals(3, page.getSelectedCount(), "Should be 3 selected sequences");
		assertFalse(page.isSubmitEnabled(), "submit should be disabled different types selected");

		page.uncheckAll();
		assertFalse(page.isSubmitEnabled(), "submit should be disabled as no samples selected");

		page.selectSingles();
		assertEquals(2, page.getSelectedCount(), "Should be 2 selected sequences");
		assertTrue(page.isSubmitEnabled(), "submit should be enabled as 2 same type sequences selected");
	}
}
