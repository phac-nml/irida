package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

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
 * <p>
 * Page Object to represent the project details page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectMembersPage {
	private WebDriver driver;
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersPage.class);

	public ProjectMembersPage(WebDriver driver, Long projectId) {
		this.driver = driver;
		driver.get("http://localhost:8080/projects/" + projectId + "/members");
	}

	public String getTitle() {
		return driver.findElement(By.tagName("h1")).getText();
	}

	public List<String> getProjectMembersNames() {
		List<WebElement> els = driver.findElements(By.cssSelector("a.col-names"));
		return els.stream().map(WebElement::getText).collect(Collectors.toList());
	}

	public void clickRemoveUserButton(Long id) {
		logger.debug("Clicking remove user button for " + id);
		WebElement removeUserButton = driver.findElement(By.id("remove-user-" + id));
		removeUserButton.click();
	}

	public void clickModialPopupButton() {
		logger.debug("Confirming user removal");
		WebElement myDynamicElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("modial-remove-user")));

		myDynamicElement.click();
		waitForAjax();
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
