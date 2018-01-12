package ca.corefacility.bioinformatics.irida.ria.integration.samples;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.samples.SampleDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.Assert.assertEquals;

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

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		page = new SampleDetailsPage(driver());
	}

	@Test
	public void testPageSetup() {
		page.gotoPage(SAMPLE_ID);
		assertEquals("Display the correct title.", "Sample - sample1", page.getPageTitle());
		assertEquals("Displays the correct organism", SAMPLE_ORGANISM, page.getOrganismName());
		assertEquals("Displays the created date", SAMPLE_CREATED_DATE, page.getCreatedDate());
		assertEquals("Displays the latitude", SAMPLE_LATITUDE, page.getLatitude());
		assertEquals("Displays the longitude", SAMPLE_LONGITUDE, page.getLongitude());
		assertEquals("Displays who collected the sample", SAMPLE_COLLECTED_BY, page.getCollectedBy());
		assertEquals("Displays the isolation source", SAMPLE_ISOLATION_SOURCE, page.getIsolationSource());
		assertEquals("Displays the geographic location name", SAMPLE_GEOGRAPHIC_LOCATION_NAME,
				page.getGeographicLocationName());
		assertEquals("Displays the strain", SAMPLE_STRAIN, page.getStrain());
		assertEquals("Displays the isolate", SAMPLE_ISOLATE, page.getIsolate());
		assertEquals("Sidebar displays the id", SAMPLE_ID, page.getSampleId());
	}
}
