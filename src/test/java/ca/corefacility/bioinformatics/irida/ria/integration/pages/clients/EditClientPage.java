package ca.corefacility.bioinformatics.irida.ria.integration.pages.clients;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

import com.google.common.base.Strings;

public class EditClientPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(EditClientPage.class);

	public static String SUCCESS_PAGE = "clients/\\d+";

	public EditClientPage(WebDriver driver) {
		super(driver);
	}

	public void goToEditPage(Long id) {
		String url = "clients/" + id + "/edit";
		logger.trace("Going to edit page for client " + id);
		get(driver, url);
		waitForTime(200);
	}

	public void editClient(String grant, boolean scope_read, boolean scope_write, boolean generateClientSecret) {

		if (!Strings.isNullOrEmpty(grant)) {
			WebElement grantField = driver.findElement(By.id("authorizedGrantTypes"));
			grantField.sendKeys(grant);
		}

		if (generateClientSecret) {
			driver.findElement(By.id("new_secret")).click();
		}

		WebElement readCheckbox = driver.findElement(By.id("scope_read"));
		setCheckbox(scope_read, readCheckbox);

		WebElement writeCheckbox = driver.findElement(By.id("scope_write"));
		setCheckbox(scope_write, writeCheckbox);

		WebElement submit = driver.findElement(By.id("edit-client-submit"));
		submit.click();
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
