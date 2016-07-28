package ca.corefacility.bioinformatics.irida.ria.unit.web.datatables;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export.ExportFormatException;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export.ProjectSamplesTableExport;

import com.github.dandelion.datatables.core.export.ReservedFormat;
import com.github.dandelion.datatables.core.html.HtmlTable;
import com.github.dandelion.datatables.extras.spring3.i18n.SpringMessageResolver;
import com.google.common.collect.ImmutableList;

/**
 * Created by josh on 2016-07-27.
 */

public class ProjectSamplesTableExportTest {

	@Autowired
	private WebApplicationContext wac;

	private MockServletContext mockServletContext;
	private MockHttpServletRequest request;
	private SpringMessageResolver springMessageResolver;

	@Before
	public void setUp() {
		mockServletContext = new MockServletContext();
		request = new MockHttpServletRequest(mockServletContext);
		request.addPreferredLocale(Locale.US);

		springMessageResolver = new SpringMessageResolver(request);
	}

	@Test(expected = ExportFormatException.class)
	public void shouldThrowAnErrorIfWrongFormat() throws ExportFormatException {
		ProjectSamplesTableExport tableExport = new ProjectSamplesTableExport("fred", "fred");
	}

	@Test
	public void testProjectTableExport() throws ExportFormatException {

		Sample sample = new Sample("fred");
		sample.setId(10L);

		List<Sample> samples = ImmutableList.of(sample);
		ProjectSamplesTableExport tableExport = new ProjectSamplesTableExport(ReservedFormat.XLSX, "project2");
		HtmlTable table = tableExport.generateHtmlTable(samples, request);
	}
}