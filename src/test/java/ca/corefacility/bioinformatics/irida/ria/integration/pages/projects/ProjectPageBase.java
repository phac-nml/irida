package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * Contains navigation components found on all Projects pages.
 */
public class ProjectPageBase extends AbstractPage {
	@FindBy(css = ".ant-menu li.ant-menu-item-selected a")
	private WebElement activePageTab;

	public ProjectPageBase(WebDriver driver) {
		super(driver);
	}

	public String getActivePage() {
		return activePageTab.getText();
	}
}
