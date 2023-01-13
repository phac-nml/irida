package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page object to represent the view announcements modal
 */
public class ViewAnnouncementComponent extends AbstractPage {

	@FindBy(css = ".ant-modal-content .ant-table-content table")
	private WebElement table;

	@FindBy(css = ".ant-modal-content .ant-table-content table tr.ant-table-row")
	private List<WebElement> rows;

	@FindBy(css = "button.ant-modal-close")
	private WebElement cancelButton;

	public ViewAnnouncementComponent(WebDriver driver) {
		super(driver);
	}

	public static ViewAnnouncementComponent goTo(WebDriver driver) {
		return PageFactory.initElements(driver, ViewAnnouncementComponent.class);
	}

	public WebElement getTable() {
		return table;
	}

	public List<WebElement> getRows() {
		return rows;
	}

	public int getTableDataSize() {
		return rows.size();
	}

	public void clickCancelButton() {
		cancelButton.click();
	}

}
