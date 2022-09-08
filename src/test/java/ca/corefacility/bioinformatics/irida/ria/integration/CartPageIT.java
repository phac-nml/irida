package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.components.SampleDetailsViewer;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/CartView.xml")
public class CartPageIT extends AbstractIridaUIITChromeDriver {
	@Test
	public void testCartPage() {
		LoginPage.loginAsUser(driver());

		// Add some samples to the cart and test to see if they get displayed/
		ProjectSamplesPage samplesPage = ProjectSamplesPage.goToPage(driver(), 1);
		samplesPage.selectSampleByName("sample5fg44");
		samplesPage.selectSampleByName("sample5fdgr");
		samplesPage.selectSampleByName("sample554sg5");
		samplesPage.addSelectedSamplesToCart();

		// Make sure 3 samples are in the cart
		CartPage page = CartPage.goToCart(driver());
		checkTranslations(page, ImmutableList.of("cart"), null);
		int count = page.getNavBarSamplesCount();
		assertEquals(3, count, "Should be 3 samples displayed in navbar");
		assertEquals(3, page.getNumberOfSamplesInCart(), "Should be 3 samples in the cart");
		assertTrue(page.onPipelinesView(), "Should be directed to pipelines view");

		/*
		Test the sample details within the cart
		 */
		final String sampleName = "sample554sg5";
		page.viewSampleDetailsFor(sampleName);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());
		assertEquals(sampleName, sampleDetailsViewer.getSampleName(), "Should be viewing the proper sample");
		assertEquals("Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample(),
				"Should display the correct created date");
		assertEquals(4, sampleDetailsViewer.getNumberOfMetadataEntries(),
				"Should have the proper number of metadata entries");
		assertEquals("AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"),
				"Should be able to diplay the proper metadata");
		sampleDetailsViewer.closeDetails();

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals(2, page.getNumberOfSamplesInCart(), "Should be 2 samples in the cart");

		// Test removing the entire project
		page.removeProjectFromCart();
	}
}