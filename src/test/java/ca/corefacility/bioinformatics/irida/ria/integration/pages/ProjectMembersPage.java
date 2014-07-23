package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
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

	public void clickEditButton() {
		logger.trace("clicking edit button");
		WebElement editMembersButton = driver.findElement(By.id("editMembers"));
		editMembersButton.click();
	}

	public boolean roleSelectDisplayed() {
		boolean present = false;
		try {
			WebElement findElement = driver.findElement(By.className("select-role"));
			present = findElement.isDisplayed();
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}
	
	public boolean roleSpanDisplayed() {
		boolean present = false;
		try {
			WebElement findElement = driver.findElement(By.className("display-role"));
			present = findElement.isDisplayed();
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}

	public void setRoleForUser(Long id, String roleValue) {
		logger.trace("Setting user " + id + " role to " + roleValue);
		WebElement findElement = driver.findElement(By.id(id + "-role"));
		Select roleSelect = new Select(findElement);
		roleSelect.selectByValue(roleValue);
		waitForAjax();
	}

	public boolean notySuccessDisplayed() {
		logger.trace("Checking if noty success");
		boolean present;
		try {
			driver.findElement(By.className("noty_type_success"));
			present = true;
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
