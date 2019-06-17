package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.PageUtilities;

public class AssociatedProjectEditPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectEditPage.class);

	public AssociatedProjectEditPage(WebDriver driver) {
		super(driver);
	}

	public void goTo(Long projectId) {
		get(driver, "/projects/" + projectId + "/settings/associated/edit");
	}

	public List<String> getProjects() {
		logger.debug("Getting associated projects");
		List<WebElement> rows = driver.findElements(By.className("associated-project-row"));
		logger.debug("Got " + rows.size() + " projects");
		List<String> names = new ArrayList<>();

		for (WebElement ele : rows) {
			WebElement findElement = ele.findElement(By.className("project-id"));
			names.add(findElement.getText());
		}
		return names;
	}

	public List<String> getAssociatedProjects() {
		logger.debug("Getting associated projects");
		List<WebElement> rows = driver.findElements(By.className("associated-project-row"));

		List<String> names = new ArrayList<>();
		// get only the rows that have a btn-success
		for (WebElement ele : rows) {
			if (ele.findElement(By.className("associated-switch")).isSelected()) {
				WebElement findElement = ele.findElement(By.className("project-id"));
				names.add(findElement.getText());
			}

		}

		return names;
	}

	public void clickAssociatedButton(Long projectId) {
		List<WebElement> rows = driver.findElements(By.className("associated-project-row"));
		WebElement foundRow = null;
		for (WebElement ele : rows) {
			if (ele.findElement(By.className("project-id")).getText().equals(projectId.toString())) {
				foundRow = ele;
			}
		}

		if (foundRow == null) {
			throw new IllegalArgumentException("No row with given project ID");
		}

		foundRow.findElement(By.className("bootstrap-switch-label")).click();
		waitForAjax();
	}

	public boolean checkSuccessNotification() {
		PageUtilities utilities = new PageUtilities(driver);
		return utilities.checkSuccessNotification();
	}

	// ************************************************************************************************
	// UTILITY METHODS
	// ************************************************************************************************

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}

}
