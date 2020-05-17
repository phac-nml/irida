package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.ProjectActivityPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/ProjectsPageIT.xml")
public class ProjectActivityPageIT extends AbstractIridaUIITChromeDriver {
	public static final Long PROJECT_ID = 1L;
	public static final String PROJECT_NAME = "project";
	public static final String PROJECT_OWNER = "Mr. Manager";
	public static final String PROJECT_CREATED_DATE = "12 Jul 2013";
	public static final String PROJECT_ORGANISM = "E. coli";

	private ProjectActivityPage detailsPage;

	@Before
	public void setUpTest() {
		detailsPage = new ProjectActivityPage(driver());
		LoginPage.loginAsManager(driver());
	}

	@Test
	public void testDisplaysProjectEvent() {
		detailsPage.goTo(PROJECT_ID);
		List<WebElement> events = detailsPage.getEvents();
		assertEquals(1, events.size());
		WebElement next = events.iterator().next();
		String className = next.getAttribute("class");
		assertTrue(className.contains("user-role-event"));
	}
}
