package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
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
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 */
public class ProjectMembersPage extends AbstractPage {
	public static final String RELATIVE_URL = "projects/1/settings/members";
	public static final String GROUPS_URL = "projects/1/settings/groups";

	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersPage.class);

	public ProjectMembersPage(WebDriver driver) {
		super(driver);
	}

	public void goToPage() {
		get(driver, RELATIVE_URL);
	}

	public void goToGroupsPage() {
		get(driver, GROUPS_URL);
	}

	public String getTitle() {
		return driver.findElement(By.tagName("h1"))
				.getText();
	}

	public List<String> getProjectMembersNames() {
		List<WebElement> els = driver.findElements(By.cssSelector("td:first-child a"));
		return els.stream()
				.map(WebElement::getText)
				.collect(Collectors.toList());
	}

	public void clickRemoveUserButton(Long id) {
		logger.debug("Clicking remove user button for " + id);
		WebElement row = waitForElementVisible(By.cssSelector("[data-user='" + id + "']"));
		WebElement removeUserButton = row.findElement(By.className("remove-btn"));
		removeUserButton.click();
	}

	public void clickModalPopupButton() {
		logger.debug("Confirming user removal");
		WebElement myDynamicElement = (new WebDriverWait(driver, 10)).until(
				ExpectedConditions.elementToBeClickable(By.id("remove-member-button")));

		myDynamicElement.click();
		waitForAjax();
	}

	public void setRoleForUser(Long id, String roleValue) {
		logger.debug("Setting user " + id + " role to " + roleValue);
		WebElement findElement = driver.findElement(By.id(id + "-role-select"));
		Select roleSelect = new Select(findElement);
		roleSelect.selectByValue(roleValue);
		waitForAjax();
	}

	public boolean checkSuccessNotification() {
		PageUtilities utilities = new PageUtilities(driver);
		return utilities.checkSuccessNotification();
	}

	public boolean addGroupButtonDisplayed() {
		logger.debug("Checking if add group button is displayed");
		boolean present = false;

		if (!driver.findElements(By.id("add-members-button"))
				.isEmpty()) {
			present = true;
		}

		return present;
	}

	public void clickAddMember() {
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement addMembers = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-members-button")));
		addMembers.click();
		waitForAjax();
	}

	public void addUserToProject(final String username, final ProjectRole role) {
		WebElement userElement = waitForElementVisible(By.className("select2-selection"));
		userElement.click();
		WebElement userField = waitForElementVisible(By.className("select2-search__field"));
		// we're using select2 on the user element so it ends up being made into
		// an input box rather than a select.
		userField.sendKeys(username);
		WebElement selection = waitForElementVisible(By.className("select2-results__option--highlighted"));
		selection.click();

		WebElement roleElement = driver.findElement(By.id("add-member-role"));
		Select roleSelect = new Select(roleElement);
		roleSelect.selectByValue(role.toString());

		WebElement submit = driver.findElement(By.id("submitAddMember"));
		submit.click();
		waitForAjax();
	}

	public void clickGroupsLink() {
		driver.findElement(By.id("project-groups-link"))
				.click();
	}

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
