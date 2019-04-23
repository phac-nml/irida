package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.cart.CartPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

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

		int count = page.getNavBarSamplesCount();
		assertEquals("Should be 3 samples displayed in navbar", 3, count);
		assertEquals("Should be 3 samples in the cart", 3, page.getNumberOfSamplesInCart());
		assertTrue("Should be directed to pipelines view", page.onPipelinesView());

		// Test removing a sample from the project
		page.removeSampleFromCart(0);
		assertEquals("Should be 2 samples in the cart", 2, page.getNumberOfSamplesInCart());

		// Test removing the entire project
		page.removeProjectFromCart();
	}
}
