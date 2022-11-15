package ca.corefacility.bioinformatics.irida.ria.integration.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Result page for global search
 */
public class SearchResultPage extends AbstractPage {

    public SearchResultPage(WebDriver driver) {
        super(driver);
    }

    public String getSearchInputQuery() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class, 't-search-input')]//input")));
        return searchInput.getAttribute("value");
    }

    public void enterNewSearchQuery(String query) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(@class, 't-search-input')]//input")));
        searchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        searchInput.sendKeys(query);
        wait.until(ExpectedConditions.urlContains(query));
    }

    public int getTotalNumberOfProjectsInTable() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement tbody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".t-search-projects tbody")));
        return tbody.findElements(By.tagName("tr")).size();
    }

    public int getTotalNumberOfProjectsByBadge() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement projectBadge;
        try {
            // This only works if there is an actual number greater than 0
            projectBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".t-search-nav [data-menu-id*=projects] .current")));

        } catch (Exception e) {
            projectBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".t-search-nav [data-menu-id*=projects] .t-count-badge sup")));
        }
        return Integer.parseInt(projectBadge.getText());
    }

    public int getTotalNumberOfSamplesByBadge() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement projectBadge;
        try {
            // This only works if there is an actual number greater than 0
            projectBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".t-search-nav [data-menu-id*=samples] .current")));

        } catch (Exception e) {
            projectBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".t-search-nav [data-menu-id*=samples] .t-count-badge sup")));
        }
        return Integer.parseInt(projectBadge.getText());
    }

    public boolean isAdminSearchTypeDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-admin-search-type")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void selectAdminPersonalProject() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-admin-search-type")));
        dropdown.click();
        WebElement personalButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".rc-virtual-list [title='PERSONAL']")));
        personalButton.click();
    }
}
