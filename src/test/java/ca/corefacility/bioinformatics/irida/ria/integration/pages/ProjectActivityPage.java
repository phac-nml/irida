package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * <p>
 * Page Object to represent the project details page.
 * </p>
 * 
 */
public class ProjectActivityPage extends AbstractPage {

	@FindBy(className = "t-activity")
	private List<WebElement> activities;

	@FindBy(className = "t-load-more")
	private WebElement loadMoreButton;

	public ProjectActivityPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectActivityPage goTo(WebDriver driver) {
		get(driver, "projects/1/activity");
		return PageFactory.initElements(driver, ProjectActivityPage.class);
	}

	public int getNumberOfActivities() {
		return activities.size();
	}

	public String getActivityTypeForActivity(int index) {
		return activities.get(index).findElement(By.cssSelector(".ant-avatar.ant-avatar-circle")).getAttribute("data-activity");
	}

	public boolean isLoadMoreButtonEnabled() {
		return loadMoreButton.isEnabled();
	}
}
