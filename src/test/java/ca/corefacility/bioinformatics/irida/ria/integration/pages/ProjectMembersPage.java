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

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectMembersPage extends AbstractPage {
	public static final String RELATIVE_URL = "projects/1/members";
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersPage.class);

	public ProjectMembersPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
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

	public void clickEditButton(Long userid) {
		logger.debug("clicking edit button for " + userid);
		WebElement editMembersButton = driver.findElement(By.id("edit-button-" + userid));
		editMembersButton.click();
	}

	public boolean roleSelectDisplayed(Long userid) {
		logger.debug("Checking if role select is displayed");
		boolean present = false;
		try {
			WebElement findElement = driver.findElement(By.id(userid + "-role-select"));
			present = findElement.isDisplayed();
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}

	public boolean roleSpanDisplayed(Long userid) {
		logger.debug("Checking if role span is displayed");
		boolean present = false;
		try {
			WebElement findElement = driver.findElement(By.id("display-role-" + userid));
			present = findElement.isDisplayed();
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}

	public void setRoleForUser(Long id, String roleValue) {
		logger.debug("Setting user " + id + " role to " + roleValue);
		WebElement findElement = driver.findElement(By.id(id + "-role-select"));
		Select roleSelect = new Select(findElement);
		roleSelect.selectByValue(roleValue);
		waitForAjax();
	}

	public boolean notySuccessDisplayed() {
		logger.debug("Checking if noty success");
		boolean present = false;
		try {
			(new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By
					.className("noty_type_success")));
			present = true;
		} catch (NoSuchElementException e) {
			present = false;
		}

		return present;
	}

	public void clickAddMember() {
		WebElement addMembers = driver.findElement(By.id("add-members-button"));
		addMembers.click();
		waitForAjax();
	}

	public void addUserToProject(Long id, ProjectRole role) {
		WebElement userElement = driver.findElement(By.id("add-user-username"));
		// we're using select2 on the user element so it ends up being made into
		// an input box rather than a select.
		userElement.sendKeys(id.toString());

		WebElement roleElement = driver.findElement(By.id("add-user-role"));
		Select roleSelect = new Select(roleElement);
		roleSelect.selectByValue(role.toString());

		WebElement submit = driver.findElement(By.id("submitAddMember"));
		submit.click();
		waitForAjax();
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
