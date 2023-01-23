package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

public class ProjectRemoteSettingsPage extends AbstractPage {

	@FindBy(className = "t-sync-frequency")
	private WebElement syncFrequencySelect;

	@FindBy(className = "t-sync-now-btn")
	private WebElement syncNowButton;

	@FindBy(className = "t-become-sync-user-btn")
	private List<WebElement> syncUserButton;

	public ProjectRemoteSettingsPage(WebDriver driver) {
		super(driver);
	}

	public static ProjectRemoteSettingsPage goTo(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/settings/remote");
		return PageFactory.initElements(driver, ProjectRemoteSettingsPage.class);
	}

	public static ProjectRemoteSettingsPage initElements(WebDriver driver) {
		return PageFactory.initElements(driver, ProjectRemoteSettingsPage.class);
	}

	// This method gets the project id from the url
	public Long getProjectId() {
		String url = driver.getCurrentUrl();
		Pattern p = Pattern.compile(".*(?:\\D|^)(\\d+)");
		Matcher m = p.matcher(url);
		Long remoteProjectId = null;

		if (m.find()) {
			remoteProjectId = Long.parseLong(m.group(1));
		}

		return remoteProjectId;
	}

	public boolean syncFrequencySelectDisplayed() {
		return syncFrequencySelect.isDisplayed();
	}

	public boolean syncNowButtonDisplayed() {
		return syncNowButton.isDisplayed();
	}

	public boolean syncNowButtonEnabled() {
		return syncNowButton.isEnabled();
	}

	public boolean syncUserButtonNotDisplayed() {
		return syncUserButton.size() == 0;
	}
}
