package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


/**
 * Page object to represent the Dashboard page
 */
public class DashboardPage extends AbstractPage {

	@FindBy(className = "t-recent-activity-title")
	private List<WebElement> recentActivityTitle;

	@FindBy(className = "t-all-projects-button")
	private List<WebElement> allProjectsBtn;

	@FindBy(className = "t-your-projects-button")
	private List<WebElement> yourProjectsBtn;

	public DashboardPage(WebDriver driver) {
		super(driver);
	}

	public void goTo() {
		get(driver, "/");
	}

	public static DashboardPage initPage(WebDriver driver) {
		get(driver, "/");
		return PageFactory.initElements(driver, DashboardPage.class);
	}

	public boolean adminAllProjectsRecentActivityTitleDisplayed() {
		if(recentActivityTitle.stream().findFirst().get().getText().equals("All Projects Recent Activity")) {
			return true;
		}
		return false;
	}

	public boolean userProjectsRecentActivityTitleDisplayed() {
		if(recentActivityTitle.stream().findFirst().get().getText().equals("Your Projects Recent Activity")) {
			return true;
		}
		return false;
	}

	public boolean allProjectsButtonDisplayed() {
		return allProjectsBtn.size() > 0 && allProjectsBtn.get(0).isDisplayed();
	}

	public boolean yourProjectsButtonDisplayed() {
		return yourProjectsBtn.size() > 0 && yourProjectsBtn.get(0).isDisplayed();
	}

	public void clickAllProjectsButton() {
		allProjectsBtn.get(0).click();
		waitForTime(500);
	}

}
