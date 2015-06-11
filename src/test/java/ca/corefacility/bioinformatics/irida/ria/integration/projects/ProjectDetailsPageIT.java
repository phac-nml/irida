package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectDetailsPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDetailsPageIT.class);
	public static final Long PROJECT_ID = 1L;
	public static final String PROJECT_NAME = "project";
	public static final String PROJECT_OWNER = "Mr. Manager";
	public static final String PROJECT_CREATED_DATE = "12 Jul 2013";
	public static final String PROJECT_ORGANISM = "E. coli";

	private List<Map<String, String>> BREADCRUMBS = ImmutableList.of(
			ImmutableMap.of(
					"href", "/projects",
					"text", "Projects"
			),
			ImmutableMap.of(
					"href", "/projects/" + PROJECT_ID,
					"text", String.valueOf(PROJECT_ID)
			),
			ImmutableMap.of(
					"href", "/projects/" + PROJECT_ID + "/activity",
					"text", "Activity"
			)
	);


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

		detailsPage.checkBreadCrumbs(BREADCRUMBS);
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
