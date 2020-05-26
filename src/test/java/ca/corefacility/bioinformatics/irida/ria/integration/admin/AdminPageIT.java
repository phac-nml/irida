package ca.corefacility.bioinformatics.irida.ria.integration.admin;

import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.test.context.ActiveProfiles;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.admin.AdminPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/admin/AdminPageView.xml")
public class AdminPageIT extends AbstractIridaUIITChromeDriver {

	@Test
	public void accessPageAsAdmin() {
		LoginPage.loginAsAdmin(driver());
		AdminPage page = AdminPage.initPage(driver());
		assertTrue("Page title should be present", page.comparePageTitle("FUBAR - YOU MADE IT"));
	}

	@Test
	public void accessPageFailure() {
		LoginPage.loginAsUser(driver());
		AdminPage page = AdminPage.initPage(driver());
		assertFalse("Page title should not be present", page.comparePageTitle("FUBAR - YOU MADE IT"));
	}

	@Test
	public void adminButtonVisible() {
		LoginPage.loginAsAdmin(driver());
		assertTrue("Admin Button should be displayed", driver().findElements(By.id("admin-panel-btn")).size() > 0);
	}

	@Test
	public void adminButtonNotVisible() {
		LoginPage.loginAsUser(driver());
		assertFalse("Admin Button should not be displayed", driver().findElements(By.id("admin-panel-btn")).size() > 0);
	}
}
