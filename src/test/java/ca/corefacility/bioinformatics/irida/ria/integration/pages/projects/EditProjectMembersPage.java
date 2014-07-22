package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

/**
 * Page representing edit project members page
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class EditProjectMembersPage {
	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(EditProjectMembersPage.class);

	public EditProjectMembersPage(WebDriver driver, Long projectId) {
		this.driver = driver;
		driver.get("http://localhost:8080/projects/" + projectId + "/members/edit");
		waitForAjax();
	}

	public void clickRemoveUserButton(Long id) {
		logger.debug("Clicking remove user button for " + id);
		WebElement removeUserButton = driver.findElement(By.id("remove-user-" + id));
		removeUserButton.click();
	}

	public void clickModialPopupButton() {
		logger.debug("Confirming user removal");
		WebElement myDynamicElement = (new WebDriverWait(driver, 10))
				  .until(ExpectedConditions.elementToBeClickable(By.className("modial-remove-user")));
				
		myDynamicElement.click();
	}

	public List<String> getUserNames() {
		logger.debug("Getting user names on page");
		waitForAjax();
		List<WebElement> nameElements = driver.findElements(By.className("col-names"));

		List<String> names = new ArrayList<>();
		for (WebElement ele : nameElements) {
			names.add(ele.getText());
		}

		return names;
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
