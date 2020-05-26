package ca.corefacility.bioinformatics.irida.ria.integration.pages.admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AdminPage extends AbstractPage {
	public static final String RELATIVE_URL = "admin/";

	@FindBy(tagName="body")
	private WebElement body;

	public AdminPage(WebDriver driver) { super(driver); }

	/**
	 * Initialize the page so that the default {@Link WebElement} has been found.
	 *
	 * @param driver	{@Link WebDriver}
	 * @return The initialized {@Link AdminPage}
	 */
	public static AdminPage initPage(WebDriver driver) {
		get(driver, RELATIVE_URL);
		return PageFactory.initElements(driver, AdminPage.class);
	}

	/**
	 * Compares the expected page title to the actual page title.
	 *
	 * @return {@link Boolean}
	 */
	public boolean comparePageTitle(String pageTitle) {
		int titleFound = body.findElements(By.xpath("//h1[contains(text(),'" + pageTitle + "')]"))
				.size();

		return titleFound > 0;
	}
}
