package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 * <p> Project Metadata Page </p>
 *
 */
public class ProjectDetailsPage extends AbstractPage {
	@FindBy(className = "t-project-name")
	private WebElement projectNameLabel;

	@FindBy(className = "t-project-desc")
	private WebElement projectDescLabel;

	@FindBy(css = ".t-project-desc .ant-typography-edit")
	private WebElement editDescriptionButton;

	@FindBy(className = "t-project-id")
	private WebElement projectIdLabel;

	@FindBy(className = "t-project-organism")
	private WebElement projectOrganismLabel;


	public ProjectDetailsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectDetailsPage goTo(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/settings");
		return PageFactory.initElements(driver, ProjectDetailsPage.class);
	}

	public static ProjectDetailsPage initElements(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectDetailsPage.class);
	}

	public String getProjectName() {
		return projectNameLabel.getText();
	}

	public String getProjectDescription() {
		return projectDescLabel.getText();
	}

	public Long getProjectId() {
		return Long.parseLong(projectIdLabel.getText());
	}

	public String getProjectOrganism() {
		return projectOrganismLabel.getText();
	}
}
