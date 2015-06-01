package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectDetailsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Integration test to ensure that the Project Details Page.
 * </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectDetailsPageIT extends AbstractIridaUIIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDetailsPageIT.class);
	public static final Long PROJECT_ID = 1L;
	public static final String PROJECT_NAME = "project";
	public static final String PROJECT_OWNER = "Mr. Manager";
	public static final String PROJECT_CREATED_DATE = "12 Jul 2013";
	public static final String PROJECT_ORGANISM = "E. coli";

	private ProjectDetailsPage detailsPage;

	@Before
	public void setUpTest() {
		detailsPage = new ProjectDetailsPage(driver());
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void hasCorrectMetaData() {
		detailsPage.goTo(PROJECT_ID);
		logger.debug("Testing: hasCorrectMetaDate");
		assertEquals("Page should show correct title", PROJECT_NAME, detailsPage.getPageTitle());
		assertEquals("Should have the organism displayed", PROJECT_ORGANISM, detailsPage.getOrganism());
		assertEquals("Should have the correct date format for creation date", PROJECT_CREATED_DATE,
				detailsPage.getCreatedDate());
	}

	@Test
	public void testDisplaysProjectEvent() {
		List<WebElement> events = detailsPage.getEvents();
		assertEquals(1, events.size());
		WebElement next = events.iterator().next();
		String className = next.getAttribute("class");
		assertTrue(className.contains("user-role-event"));
	}
}
