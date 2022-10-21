package ca.corefacility.bioinformatics.irida.ria.integration.pages.analysis;

import ca.corefacility.bioinformatics.irida.ria.integration.analysis.AnalysisDetailsPageIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AnalysisDetailsPage extends AbstractPage {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);

    public static final String RELATIVE_URL = "analysis/";

    @FindBy(className = "ant-page-header-heading-title")
    private List<WebElement> pageTitles;

    @FindBy(className = "t-file-header")
    private List<WebElement> files;

    @FindBy(className = "t-file-name")
    private List<WebElement> fileNames;

    @FindBy(css = "th.t-galaxy-parameter")
    private List<WebElement> galaxyParameters;

    @FindBy(className = "t-tool-name")
    private List<WebElement> toolList;

    @FindBy(className = "t-paired-end")
    private List<WebElement> pairedEndElements;

    @FindBy(id = "root")
    private WebElement rootDiv;

    @FindBy(className = "ant-popover-inner-content")
    private WebElement confirmDiv;

    @FindBy(className = "t-delete-analysis-btn")
    private WebElement deleteButton;

    @FindBy(css = ".t-sample-search-input input")
    private WebElement searchInput;

    @FindBy(className = "t-download-all-files-btn")
    private List<WebElement> downloadAllFilesButton;

    @FindBy(css = ".t-download-all-files-btn button.ant-dropdown-trigger")
    private List<WebElement> downloadIndividualFilesMenuButton;

    @FindBy(className = "t-download-individual-files-menu")
    private List<WebElement> downloadIndividualFilesMenu;

    @FindBy(className = "ant-steps")
    private List<WebElement> analysisSteps;

    @FindBy(className = "ant-alert-message")
    private List<WebElement> warningAlerts;

    @FindBy(className = "t-reference-file-download-btn")
    private List<WebElement> referenceFileDownloadButton;

    @FindBy(className = "t-download-output-file-btn")
    private List<WebElement> downloadOutputFileButtons;

    @FindBy(className = "ant-descriptions-view")
    private List<WebElement> descriptionViewDivs;

    @FindBy(className = "t-analysis-menu")
    private List<WebElement> horizontalTabMenus;

    @FindBy(className = "ant-layout-has-sider")
    private List<WebElement> verticalTabMenus;

    @FindBy(className = "ant-list-item-meta-description")
    private List<WebElement> listDescriptionValues;

    @FindBy(className = "ant-list-item-meta-title")
    private List<WebElement> listTitleValues;

    @FindBy(className = "ant-checkbox")
    private List<WebElement> checkBoxes;

    @FindBy(className = "ant-checkbox-checked")
    private List<WebElement> checkedCheckBoxes;

    @FindBy(css = "button[title='Select tree shape']")
    private WebElement treeShapeTrigger;

    @FindBy(css = "[title='Select metadata fields to display']")
    private WebElement metadataTrigger;

    @FindBy(className = "t-metadata-select")
    private WebElement metadataTemplateSelect;

    @FindBy(css = "[title='Download tree, SVG, or PNG files']")
    private WebElement downloadTrigger;

    @FindBy(className = "t-phylocanvas-wrapper")
    private WebElement phylocanvasWrapper;

    @FindBy(id = "phyloCanvasDiv__canvas")
    private WebElement phyloTree;

    @FindBy(className = "t-citation")
    private WebElement citation;

    @FindBy(className = "ant-menu-title-content")
    private List<WebElement> menuItems;

    @FindBy(xpath = "//div[contains(text(), 'Legend')]")
    private WebElement layoutLegendButton;

    @FindBy(className = "t-fields-all")
    private WebElement toggleAllMetadataFields;

    public AnalysisDetailsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Initialize the page so that the default {@link WebElement} have been
     * found.
     *
     * @param driver     {@link WebDriver}
     * @param analysisId Id the the analysis page to view.
     * @param tabKey     Tab to be active on load
     * @return The initialized {@link AnalysisDetailsPage}
     */
    public static AnalysisDetailsPage initPage(WebDriver driver, long analysisId, String tabKey) {
        get(driver, RELATIVE_URL + analysisId + "/" + tabKey);
        return PageFactory.initElements(driver, AnalysisDetailsPage.class);
    }

    /**
     * Adds shared project
     */
    public void addSharedProjects() {
        checkBoxes.get(0).click();
    }

    /**
     * Determines if the actual and expected analysis details are identical
     *
     * @return {@link Boolean}
     */
    public boolean analysisDetailsEqual(String[] expectedDetails) {
        boolean expectedEqualsActual = true;
        String[] actualDetails = new String[7];

        int index = 0;
        for (WebElement item : listDescriptionValues) {
            actualDetails[index] = item.getText();
            index++;
        }

        for (int i = 0; i < expectedDetails.length; i++) {
            if (!expectedDetails[i].equals(actualDetails[i])) {
                expectedEqualsActual = false;
                logger.debug(
                        "Analysis detail not equal. Expected: " + expectedDetails[i] + " actual: " + actualDetails[i]);
            }
        }
        return expectedEqualsActual;
    }

    /**
     * Determines if analysis steps are displayed
     *
     * @return {@link Boolean}
     */
    public boolean analysisStepsVisible() {
        return analysisSteps.size() > 0;
    }

    /**
     * Determines if citation is
     * displayed on citation page
     *
     * @return {@link Boolean}
     */
    public boolean citationVisible() {
        return citation.isDisplayed();
    }

    /**
     * Clicks on the pagination button specified
     */
    public void clickPagination(int pageNum) {
        rootDiv.findElements(By.className("ant-pagination-item-" + pageNum)).get(0).click();
        waitForTime(500);
    }

    /**
     * Compares the expected page title to the actual
     *
     * @return {@link Boolean}
     */
    public String getTabContentTitle() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className("ant-page-header-heading-title"), 2));
        return pageTitles.get(1).getText();
    }

    /**
     * Compares the expected tab title to the actual
     *
     * @return {@link Boolean}
     */
    public boolean compareTabTitle(String pageTitle) {

        waitForElementsVisible(By.cssSelector("span[title='" + pageTitle + "']"));
        boolean titleFound = rootDiv.findElement(By.cssSelector("span[title='" + pageTitle + "']")).isDisplayed();

        return titleFound;
    }

    /**
     * Clicks the delete button, waits, then clicks the confirm
     * button within the popover div
     */
    public void deleteAnalysis() {
        deleteButton.click();
        waitForTime(500);
        waitForElementVisible(By.className("ant-popover-inner-content"));
        confirmDiv.findElements(By.className("ant-btn-sm")).get(1).click();
    }

    /**
     * Determines if delete button exists
     *
     * @return {@link Boolean}
     */
    public boolean deleteButtonExists() {
        return deleteButton.isDisplayed();
    }

    /**
     * Clicks on the tool to display its execution params
     */
    public void displayToolExecutionParameters() {
        toolList.get(0).click();
        descriptionViewDivs.get(0).findElements(By.className("t-galaxy-parameter")).size();
    }

    /**
     * Determines if download all files button is
     * visible on output file preview page
     *
     * @return {@link Boolean}
     */
    public boolean downloadAllFilesButtonVisible() {
        return downloadAllFilesButton.size() == 1;
    }

    public boolean downloadIndividualFilesMenuButtonVisible() {
        return downloadIndividualFilesMenuButton.size() == 1;
    }

    public boolean downloadIndividualFilesMenuVisible() {
        downloadIndividualFilesMenuButton.get(0).click();
        waitForTime(500);
        return downloadIndividualFilesMenu.get(0).isDisplayed();
    }

    /**
     * Determines if download file button is
     * visible on output file preview page for
     * individual files
     *
     * @return {@link Boolean}
     */
    public boolean downloadOutputFileButtonVisible(int numBtnsExpected) {
        return downloadOutputFileButtons.size() == numBtnsExpected;
    }

    /**
     * Determines if email pipeline section is visible
     *
     * @return {@link Boolean}
     */
    public boolean emailPipelineResultStatusSelectVisible() {
        return !driver.findElements(By.className("t-email-pipeline-result-select"))
                .isEmpty();
    }

    /**
     * Determines if the number of list items (titles)
     * equals to the expected number of list items
     *
     * @return {@link Integer}
     */
    public boolean expectedNumberOfListItemsEqualsActual(int expectedNumber) {
        return listTitleValues.size() == expectedNumber;
    }

    /**
     * Filters samples based on search string and waits
     * before returning
     */
    public void filterSamples(String searchStr) {
        searchInput.sendKeys(searchStr);
        waitForTime(500);
    }

    /**
     * Gets provenance for file selected
     */
    public void getFileProvenance(int fileNum) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement firstTool = null;

        if (toolList.size() > 0) {
            firstTool = toolList.get(0);
        }

        files.get(fileNum).click();

        if (firstTool != null) {
            wait.until(ExpectedConditions.invisibilityOf(firstTool));
        }
    }

    /**
     * Determines number of galaxy parameters for a tool
     *
     * @return {@link Integer}
     */
    public int getGalaxyParametersCount() {
        return galaxyParameters.size();
    }

    /**
     * Determine the number of files created.
     *
     * @return {@link Integer}
     */
    public int getNumberOfFilesDisplayed() {
        return fileNames.size();
    }

    /**
     * Determines the number of list items (titles)
     *
     * @return {@link Integer}
     */
    public int getNumberOfListItems() {
        return listTitleValues.size();
    }

    /**
     * Determines the number of list items (descriptions)
     *
     * @return {@link Boolean}
     */
    public int getNumberOfListItemValues() {
        return listDescriptionValues.size();
    }


    /**
     * Determine the number of samples used by analysis.
     *
     * @return {@link Integer}
     */
    public int getNumberOfSamplesInAnalysis() {
        return pairedEndElements.size();
    }


    /**
     * Determines number of output files for provenance
     *
     * @return {@link Integer}
     */
    public int getProvenanceFileCount() {
        return files.size();
    }

    /**
     * Determines number of tools used by analysis
     *
     * @return {@link Integer}
     */
    public int getToolCount() {
        return toolList.size();
    }

    /**
     * Gets the warning alert text and returns
     *
     * @return {@link String}
     */
    public String getWarningAlertText() {
        return warningAlerts.get(0).getText();
    }

    /**
     * Determines if there is a horizontal tab menu
     *
     * @return {@link Boolean}
     */
    public boolean hasHorizontalTabLinks() {
        return horizontalTabMenus.size() == 1;
    }

    /**
     * Determines if project has shared projects
     *
     * @return {@link Boolean}
     */
    public boolean hasSharedWithProjects() {
        return checkedCheckBoxes.size() > 0;
    }

    /**
     * Determines if there is a sider for tabs
     *
     * @return {@link Boolean}
     */
    public boolean hasSideBarTabLinks() {
        return verticalTabMenus.size() == 1;
    }

    /**
     * Determines if there is a job error warning alert
     *
     * @return {@link Boolean}
     */
    public boolean jobErrorAlertVisible() {

        return !driver.findElements(By.className("ant-alert-warning"))
                .isEmpty();
    }

    public boolean priorityEditVisible() {
        return !driver.findElements(By.className("t-priority-edit"))
                .isEmpty();
    }

    public boolean menuIncludesItem(String menuItem) {
        for (WebElement element : menuItems) {
            if (element.getText().equals(menuItem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines reference file download button is
     * displayed on samples page
     *
     * @return {@link Boolean}
     */
    public boolean referenceFileDownloadButtonVisible() {
        return referenceFileDownloadButton.size() == 1;
    }


    /**
     * Removes shared projects
     */
    public void removeSharedProjects() {
        checkedCheckBoxes.get(0).click();
    }

    /**
     * Determines if phylogenetic tree wrapper is
     * not displayed on tree preview page
     *
     * @return {@link Boolean}
     */
    public boolean phylocanvasWrapperNotFound() {
        return driver.findElements(By.className("t-phylocanvas-wrapper")).size() == 0;
    }

    /**
     * Determines if phylogenetic tree wrapper is
     * displayed on tree preview page
     *
     * @return {@link Boolean}
     */
    public boolean phylocanvasWrapperVisible() {
        return phylocanvasWrapper.isDisplayed();
    }

    /**
     * Determines if tree shape tools aren't
     * displayed on tree preview page
     *
     * @return {@link Boolean}
     */
    public boolean treeToolsNotFound() {
        return driver.findElements(By.className("t-tree-shape-tools")).size() == 0;
    }

    public void openTreeShapeDropdown() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.elementToBeClickable(treeShapeTrigger));
        treeShapeTrigger.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-shape-dd")));
    }

    public boolean areAllTreeShapeOptionsDisplayed() {
        return driver.findElements(By.cssSelector(".t-shape-dd li")).size() == 5;
    }

    public String getCurrentTreeShapeTitleAttr() {
        WebElement menu = driver.findElement(By.className("t-shape-dd"));
        WebElement menuItem = menu.findElement(By.cssSelector(".ant-dropdown-menu-item-disabled .ant-dropdown-menu-title-content"));
        return menuItem.getText();
    }

    public String getCurrentlyDisplayedTreeShapeIcon() {
        WebElement button = treeShapeTrigger.findElement(By.tagName("span"));
        return button.getAttribute("data-shape");
    }

    public void updateTreeShape(String shape) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        List<WebElement> buttons = driver.findElement(By.className("t-shape-dd")).findElements(By.tagName("li"));
        for (WebElement button : buttons) {
            if (button.findElement(By.cssSelector("[role='img']")).getAttribute("data-shape").equals(shape)) {
                button.click();
                wait.until(ExpectedConditions.attributeContains(treeShapeTrigger.findElement(By.tagName("span")), "data-shape", shape));
                return;
            }
        }
    }

    public void openMetadataDropdown() {
        metadataTrigger.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-shape-dd")));
    }

    public void selectedMetadataTemplate(String templateName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        metadataTemplateSelect.click();
        List<WebElement> templates = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("t-template-field")));
        for (WebElement template : templates) {
            if (template.getAttribute("title").equals(templateName)) {
                template.click();
                wait.until(ExpectedConditions.attributeContains(template, "aria-selected", "true"));
            }
        }
    }

    public List<String> getSelectedMetadataFields() {
        List<WebElement> items = driver.findElements(By.cssSelector(".t-metadata-field"));
        List<String> selectedFields = new ArrayList<>();
        for (WebElement item : items) {
            if (!item.findElements(By.className("ant-checkbox-checked")).isEmpty()) {
                String label = item.findElement(By.className("t-field-title")).getText();
                selectedFields.add(label);
            }
        }
        return selectedFields;
    }

    public void toggleAllMetadataFields() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement switchElement = toggleAllMetadataFields.findElement(By.className("ant-switch"));
        String toggleValue = switchElement.getAttribute("aria-checked");
        toggleAllMetadataFields.click();
        wait.until(ExpectedConditions.attributeContains(switchElement, "aria-checked", toggleValue.equals("true") ? "false" : "true"));
    }

    public int getNumberOfMetadataFields() {
        WebElement menu = driver.findElement(By.className("t-metadata-dd"));
        List<WebElement> fields = menu.findElements(By.className("t-metadata-field"));
        return fields.size();
    }

    public void openDownloadDropdown() {
        downloadTrigger.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-download-dd")));
    }

    public void downloadTreeSVG() {
        WebElement menu = driver.findElement(By.className("t-download-dd"));
        WebElement svgLink = menu.findElement(By.xpath("//span[contains(text(), 'Download as SVG')]"));
        svgLink.click();
    }

    /**
     * Determines if galaxy history id is
     * displayed on error page
     *
     * @return {@link Boolean}
     */
    public boolean galaxyHistoryIdVisible() {
        return driver.findElements(By.id("t-galaxy-history-id")).size() == 1;
    }

    public void openLegend() {
        layoutLegendButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("t-legend")));
    }

    public boolean legendContainsCorrectAmountOfMetadataFields() {
        WebElement legend = driver.findElement(By.className("t-legend"));
        List<WebElement> metadataRegions = legend.findElements(By.className("ant-list-item-meta-title"));
        return metadataRegions.size() == 4;
    }

	public boolean saveToSamplesVisible() {
		try {
			return driver.findElement(By.className("t-save-results-to-samples")).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}
}
