package ca.corefacility.bioinformatics.irida.ria.integration.pages.announcements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page object to represent the Announcements Detail page
 */
public class AnnouncementDetailsComponent extends AbstractPage {

    @FindBy(css = "form.ant-form input#title")
    private WebElement input;

    @FindBy(css = "form.ant-form textarea.mde-text")
    private WebElement textarea;

    @FindBy(css = "form.ant-form input#priority")
    private WebElement checkbox;

    @FindBy(css = "form.ant-form button.t-submit-announcement")
    private WebElement submitButton;

    public AnnouncementDetailsComponent(WebDriver driver) {
        super(driver);
    }

    public static AnnouncementDetailsComponent goTo(WebDriver driver) {
        return PageFactory.initElements(driver, AnnouncementDetailsComponent.class);
    }

    public String getTitle() {
        return input.getAttribute("value");
    }

    public String getMessage() {
        return textarea.getText();
    }

    public boolean getPriority() {
        return checkbox.isSelected();
    }

    public void enterAnnouncement(String title, String message, Boolean priority) {
        input.sendKeys(Keys.chord(Keys.CONTROL,"a", Keys.DELETE));
        input.sendKeys(title);

        textarea.clear();
        textarea.sendKeys(message);


        if(priority && !checkbox.isSelected()) {
            checkbox.click();
        } else if (!priority && checkbox.isSelected()) {
            checkbox.click();
        }

        submitButton.click();
        waitForTime(400);
    }

    public int getTableDataSize() {
        WebElement table = driver.findElement(By.cssSelector("table"));
        return table.findElements(By.cssSelector("tbody>tr.ant-table-row")).size();
    }

    public void clickCancelButton() {
        WebElement cancelButton = driver.findElement(By.cssSelector("button.ant-drawer-close"));
        cancelButton.click();
    }

    public void clickViewTab() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement createButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rc-tabs-0-tab-2")));
        createButton.click();
    }
}
