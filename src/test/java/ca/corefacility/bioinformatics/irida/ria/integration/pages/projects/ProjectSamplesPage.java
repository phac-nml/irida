package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * <p>
 * Page Object to represent the project samples page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSamplesPage {
    public static final String URL = "http://localhost:8080/projects/1/samples";
    private WebDriver driver;

    public ProjectSamplesPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTitle() {
        return driver.findElement(By.tagName("h1")).getText();
    }

    public int getDisplayedSampleCount() {
        return driver.findElements(By.cssSelector("tbody tr")).size();
    }

    public int getCountOfSamplesWithFiles() {
        return driver.findElements(By.className("glyphicon-chevron-right")).size();
    }

    public int getSelectedSampleCount() {
        return driver.findElements(By.cssSelector("tbody input[type=\"checkbox\"]:checked")).size();
    }

    public boolean isSelectAllInIndeterminateState() {
        String exists = driver.findElement(By.id("selectAll")).getAttribute("indeterminate");
        if (Strings.isNullOrEmpty(exists)) {
            return false;
        }
        return true;
    }

    public boolean isSelectAllSelected() {
        return driver.findElement(By.id("selectAll")).getAttribute("checked").equals("true");
    }

    public boolean isFilesAreaDisplayed() {
        return driver.findElements(By.cssSelector("tbody tr.details + tr")).size() == 1;
    }

    // Events
    public void clickSelectAllCheckbox() {
        driver.findElement(By.id("selectAll")).click();
    }

    public void clickFirstSampleCheckbox() {
        driver.findElement(By.cssSelector("tbody > tr input[type=\"checkbox\"]")).click();
    }

    public void openFilesView() {
        driver.findElement(By.className("glyphicon-chevron-right")).click();
    }
}
