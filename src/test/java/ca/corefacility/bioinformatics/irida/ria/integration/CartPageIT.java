package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.components.SampleDetailsViewer;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/CartView.xml")
public class CartPageIT extends AbstractIridaUIITChromeDriver {
	@Test
	public void testCartPage() {
		LoginPage.loginAsUser(driver());

		// Add some samples to the cart and test to see if they get displayed/
		ProjectSamplesPage samplesPage = ProjectSamplesPage.gotToPage(driver(), 1);
		samplesPage.selectSample(0);
		samplesPage.selectSample(2);
		samplesPage.selectSample(3);
		samplesPage.addSelectedSamplesToCart();

		// Make sure 3 samples are in the cart
		CartPage page = CartPage.goToCart(driver());
		checkTranslations(page, ImmutableList.of("cart"), null);
		int count = page.getNavBarSamplesCount();
		assertEquals("Should be 3 samples displayed in navbar", 3, count);
		assertEquals("Should be 3 samples in the cart", 3, page.getNumberOfSamplesInCart());
		assertTrue("Should be directed to pipelines view", page.onPipelinesView());

		/*
		Test the sample details within the cart
		 */
		final String sampleName = "sample554sg5";
		page.viewSampleDetailsFor(sampleName);
		SampleDetailsViewer sampleDetailsViewer = SampleDetailsViewer.getSampleDetails(driver());
		assertEquals("Should be viewing the proper sample", sampleName, sampleDetailsViewer.getSampleName());
		assertEquals("Should display the correct created date", "Jul 19, 2013, 2:18 PM", sampleDetailsViewer.getCreatedDateForSample());
		assertEquals("Should have the proper number of metadata entries", 4, sampleDetailsViewer.getNumberOfMetadataEntries());
		assertEquals("Should be able to diplay the proper metadata", "AB-1003", sampleDetailsViewer.getValueForMetadataField("symptom"));
		sampleDetailsViewer.closeDetails();

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals("Should be 2 samples in the cart", 2, page.getNumberOfSamplesInCart());

		// Test removing the entire project
		page.removeProjectFromCart();
	}
}