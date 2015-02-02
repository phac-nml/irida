package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class AssociatedProjectPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectPage.class);

	private static final String RELATIVE_URL = "/projects/1/associated";

	public AssociatedProjectPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
	}

	public List<String> getAssociatedProjects() {
		logger.debug("Getting associated projects");
		List<WebElement> divs = driver.findElements(By.className("associated-project"));
		logger.debug("Got " + divs.size() + " projects");
		return divs.stream().map(WebElement::getText).collect(Collectors.toList());
	}

	public List<String> getProjectsWithRights() {
		logger.debug("Getting authorized projects");
		List<WebElement> authorized = driver.findElements(By.cssSelector(".authorized.project-name"));
		return authorized.stream().map(WebElement::getText).collect(Collectors.toList());
	}
}
