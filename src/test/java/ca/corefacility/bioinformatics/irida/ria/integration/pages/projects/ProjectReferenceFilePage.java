package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;

/**
 */
public class ProjectReferenceFilePage extends AbstractPage {
	@FindBy(className = "ant-alert-info")
	private List<WebElement> noFileNotice;

	@FindBy(className = "t-rf-owner")
	private List<WebElement> noFileNoticeOwner;

	@FindBy(className = "t-rf-user")
	private List<WebElement> noFileNoticeUser;

	@FindBy(className = "t-files-table")
	private List<WebElement> filesTable;

	@FindBy(className = "t-remove-btn")
	List<WebElement> removeBtns;

	@FindBy(className = "t-download-btn")
	List<WebElement> downloadBtns;

	@FindBy(className = "t-remove-confirm")
	List<WebElement> removeConfirmBtns;

	@FindBy(className = "ref-file-row")
	List<WebElement> fileRows;

	@FindBy(className = "ant-upload-btn")
	private List<WebElement> uploadRefBtn;

	public ProjectReferenceFilePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectReferenceFilePage goTo(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/settings/referenceFiles");
		waitForTime(1000);
		return PageFactory.initElements(driver, ProjectReferenceFilePage.class);
	}

	public boolean isNoFileNoticeDisplayed() {
		return noFileNotice.size() > 0 && noFileNotice.get(0).isDisplayed();
	}

	public boolean isNoFileNoticeOwner() {
		return noFileNoticeOwner.size() > 0 && noFileNoticeOwner.get(0).isDisplayed();
	}

	public boolean isNoFileNoticeUser() {
		return noFileNoticeUser.size() > 0 && noFileNoticeUser.get(0).isDisplayed();
	}

	public boolean isFilesTableDisplayed() {
		return filesTable.size() > 0 && filesTable.get(0).isDisplayed();
	}

	public boolean areRemoveFileBtnsAvailable() {
		return removeBtns.size() > 0;
	}

	public boolean areDownloadFileBtnsAvailable() {
		return downloadBtns.size() > 0;
	}

	public void removeFirstRefFile() {
		removeBtns.get(0).click();
		waitForTime(500);
		removeConfirmBtns.get(0).click();
		waitForTime(500);
	}

	public boolean isUploadReferenceFileBtnPresent() {
		return uploadRefBtn.size() > 0;
	}

	public int numRefFiles() {
		return driver.findElements(By.cssSelector("table tbody tr")).size();
	}
}
