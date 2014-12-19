package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

import com.google.common.base.Strings;

public class EditClientPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(EditClientPage.class);

	@FindBy(id = "scope_write")
	private WebElement cbScropeWrite;

	@FindBy(id = "scope_read")
	private WebElement cbScopeRead;

	@FindBy(id = "new_secret")
	private WebElement cbNewSecret;

	@FindBy(id = "authorizedGrantTypes")
	private WebElement authorizedGrantTypes;

	@FindBy(id = "edit-client-submit")
	private WebElement editClientSubmit;

	public static String SUCCESS_PAGE = "clients/\\d+";

	public EditClientPage(WebDriver driver) {
		super(driver);
	}

	public static EditClientPage goToEditPage(WebDriver driver, Long id) {
		String url = "clients/" + id + "/edit";
		logger.trace("Going to edit page for client " + id);
		get(driver, url);
		waitForTime(400);
		return PageFactory.initElements(driver, EditClientPage.class);
	}

	public void editClient(String grant, boolean scope_read, boolean scope_write, boolean generateClientSecret) {

		if (!Strings.isNullOrEmpty(grant)) {
			authorizedGrantTypes.sendKeys(grant);
		}

		if (generateClientSecret) {
			cbNewSecret.click();
		}

		setCheckbox(scope_read, cbScopeRead);

		setCheckbox(scope_write, cbScropeWrite);

		editClientSubmit.click();
	}

	public boolean checkSuccess() {
		if (driver.getCurrentUrl().matches(BASE_URL + SUCCESS_PAGE)) {
			return true;
		} else {
			return false;
		}
	}

	private void setCheckbox(boolean value, WebElement box) {
		if (value) {
			if (!box.isSelected()) {
				box.click();
			}
		} else {
			if (box.isSelected()) {
				box.click();
			}
		}
	}
}
