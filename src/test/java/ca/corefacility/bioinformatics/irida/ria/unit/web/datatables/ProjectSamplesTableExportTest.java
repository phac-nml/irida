package ca.corefacility.bioinformatics.irida.ria.unit.web.datatables;

import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export.ExportFormatException;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export.ProjectSamplesTableExport;

/**
 * Unit Test for {@link ProjectSamplesTableExport}
 */
public class ProjectSamplesTableExportTest {

	@SuppressWarnings("unused")
	@Autowired
	private WebApplicationContext wac;

	private MessageSource messageSource;

	@Before
	public void setUp() {
		messageSource = mock(MessageSource.class);
	}

	@Test(expected = ExportFormatException.class)
	public void shouldThrowAnErrorIfWrongFormat() throws ExportFormatException {
		new ProjectSamplesTableExport("fred", "fred", messageSource, Locale.US);
	}
}