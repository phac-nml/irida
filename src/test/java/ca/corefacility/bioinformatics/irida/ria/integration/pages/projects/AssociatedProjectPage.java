package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class AssociatedProjectPage {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectPage.class);
	
	private static final String URL = BasePage.URL + "/projects/1/associated";
	private WebDriver driver;

	public AssociatedProjectPage(WebDriver driver) {
		this.driver = driver;
		driver.get(URL);
		waitForAjax();
	}
	
	public List<String> getAssociatedProjects() {
		logger.debug("Getting associated projects");
        List<WebElement> divs = driver.findElements(By.className("associated-project"));
        logger.debug("Got " + divs.size() + " projects");
        return divs.stream().map(WebElement::getText).collect(Collectors.toList());
    }

	public String getProjectWithNoRights() {
		logger.debug("Getting unauthorized projects");
		WebElement unauthorized = driver.findElement(By.cssSelector(".unauthorized.project-name"));
		return unauthorized.getText();
	}

    public List<String> getProjectsWithRights() {
    	logger.debug("Getting authorized projects");
        List<WebElement> authorized = driver.findElements(By.cssSelector(".authorized.project-name"));
        return authorized.stream().map(WebElement::getText).collect(Collectors.toList());
    }

	// ************************************************************************************************
	// UTILITY METHODS
	// ************************************************************************************************

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}
}
