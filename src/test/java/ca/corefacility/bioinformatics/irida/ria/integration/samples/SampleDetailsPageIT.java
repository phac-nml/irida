package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleDetailsPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * <p>
 * Integration test to ensure that the Sample Details Page.
 * </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleDetailsPageIT {
	private static final Long SAMPLE_ID = 1L;
	private static final String SAMPLE_CREATED_DATE = "18 Jul 2013";
	private static final String SAMPLE_ORGANISM = "E. coli";
	private static final String SAMPLE_LATITUDE = "49.8994";
	private static final String SAMPLE_LONGITUDE = "-97.1392";
	private static final String SAMPLE_COLLECTED_BY = "Fred Penner";
	private static final String SAMPLE_ISOLATION_SOURCE = "grass";
	private static final String SAMPLE_GEOGRAPHIC_LOCATION_NAME = "Somewhere:nowhere";
	private static final String SAMPLE_STRAIN = "O157";
	private static final String SAMPLE_ISOLATE = "54343";

	private WebDriver driver;
	private SampleDetailsPage page;

	@Before
	public void setUp() {
		driver = BasePage.initializeDriver();
		page = new SampleDetailsPage(driver);
	}

	@After
	public void destroy() {
		BasePage.destroyDriver(driver);
	}

	@Test
	public void testPageSetup() {
		page.gotoPage(SAMPLE_ID);
		assertEquals("Display the correct title.", "Sample - sample1 - Details", page.getPageTitle());
		assertEquals("Displays the correct organism", SAMPLE_ORGANISM, page.getOrganismName());
		assertEquals("Displays the created date", SAMPLE_CREATED_DATE, page.getCreatedDate());
		assertEquals("Displays the latitude", SAMPLE_LATITUDE, page.getLatitude());
		assertEquals("Displays the longitude", SAMPLE_LONGITUDE, page.getLongitude());
		assertEquals("Displays who collected the sample", SAMPLE_COLLECTED_BY, page.getCollectedBy());
		assertEquals("Displays the isolation source", SAMPLE_ISOLATION_SOURCE, page.getIsolationSource());
		assertEquals("Displays the geographic location name", SAMPLE_GEOGRAPHIC_LOCATION_NAME, page.getGeographicLocationName());
		assertEquals("Displays the strain", SAMPLE_STRAIN, page.getStrain());
		assertEquals("Displays the isolate", SAMPLE_ISOLATE, page.getIsolate());
		assertEquals("Sidebar displays the id", SAMPLE_ID, page.getSampleId());
	}
}
