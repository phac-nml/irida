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
	@FindBy(id = "rf-notice")
	private WebElement noFileNotice;

	@FindBy(id = "files-table")
	private WebElement filesTable;

	@FindBy(className = "removeBtn")
	List<WebElement> removeBtns;

	@FindBy(className = "ref-file-row")
	List<WebElement> fileRows;

	@FindBy(className = "uploadRefBtn")
	private List<WebElement> uploadRefBtn;

	public ProjectReferenceFilePage(WebDriver driver) {
		super(driver);
	}

	public static ProjectReferenceFilePage goTo(WebDriver driver, Long projectId) {
		get(driver, "projects/" + projectId + "/referenceFiles");
		waitForTime(500);
		return PageFactory.initElements(driver, ProjectReferenceFilePage.class);
	}

	public boolean isNoFileNoticeDisplayed() {
		return noFileNotice.isDisplayed();
	}

	public boolean isNoFileNoticeOwner() {
		return noFileNotice.getAttribute("class").contains("rf-owner");
	}

	public boolean isFilesTableDisplayed() {
		return filesTable.isDisplayed();
	}

	public boolean areRemoveFileBtnsAvailable() {
		return removeBtns.size() > 0;
	}

	public int numRefFiles() {
		return fileRows.size();
	}

	public void removeFirstRefFile() {
		removeBtns.get(0).click();
		By deleteModalLocator = By.id("delete-modal");
		waitForElementVisible(deleteModalLocator);
		driver.findElement(By.id("deleteBtn")).click();
		waitForElementInvisible(deleteModalLocator);
	}

	public boolean isUploadReferenceFileBtnPresent() {
		return uploadRefBtn.size() > 0;
	}
}
