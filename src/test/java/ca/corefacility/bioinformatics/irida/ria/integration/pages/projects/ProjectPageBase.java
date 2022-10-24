package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Contains navigation components found on all Projects pages.
 */
public class ProjectPageBase extends AbstractPage {
	@FindBy(xpath = "//li[contains(@class, 'ant-menu-item-selected')]/span[contains(@class, 'ant-menu-title-content')]")
	private WebElement activePageTab;

	@FindBy(className = "t-project-name")
	private WebElement projectName;

	public ProjectPageBase(WebDriver driver) {
		super(driver);
	}

	public String getActivePage() {
		return activePageTab.getText();
	}

	public String getProjectName() {
		return projectName.getText();
	}
}
