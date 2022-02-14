package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p> Integration test to ensure that the Sample Details Page. </p>
 *
 */
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/samples/SamplePagesIT.xml")
public class SampleDetailsPageIT extends AbstractIridaUIITChromeDriver {
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

	private SampleDetailsPage page;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SampleDetailsPage(driver());
	}

	@Test
	public void testPageSetup() {
		page.gotoPage(SAMPLE_ID);
		assertEquals("Sample - sample1", page.getPageTitle(), "Display the correct title.");
		assertEquals(SAMPLE_ORGANISM, page.getOrganismName(), "Displays the correct organism");
		assertEquals(SAMPLE_CREATED_DATE, page.getCreatedDate(), "Displays the created date");
		assertEquals(SAMPLE_LATITUDE, page.getLatitude(), "Displays the latitude");
		assertEquals(SAMPLE_LONGITUDE, page.getLongitude(), "Displays the longitude");
		assertEquals(SAMPLE_COLLECTED_BY, page.getCollectedBy(), "Displays who collected the sample");
		assertEquals(SAMPLE_ISOLATION_SOURCE, page.getIsolationSource(), "Displays the isolation source");
		assertEquals( SAMPLE_GEOGRAPHIC_LOCATION_NAME, page.getGeographicLocationName(),
				"Displays the geographic location name");
		assertEquals(SAMPLE_STRAIN, page.getStrain(), "Displays the strain");
		assertEquals(SAMPLE_ISOLATE, page.getIsolate(), "Displays the isolate");
		assertEquals(SAMPLE_ID, page.getSampleId(), "Sidebar displays the id");
	}
}
