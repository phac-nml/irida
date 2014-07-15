package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectCollaboratorsPage {
	private WebDriver driver;

	public ProjectCollaboratorsPage(WebDriver driver, Long projectId) {
		this.driver = driver;
		driver.get("http://localhost:8080/projects/" + projectId + "/collaborators");
	}

	public String getTitle() {
        return driver.findElement(By.tagName("h1")).getText();
    }

    public List<String> getProjectCollaboratorsNames() {
        List<WebElement> els = driver.findElements(By.cssSelector("a.col-names"));
        return els.stream().map(WebElement::getText).collect(Collectors.toList());
    }
}
