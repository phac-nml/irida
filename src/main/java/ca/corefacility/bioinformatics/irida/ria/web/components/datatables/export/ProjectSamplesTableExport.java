package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export.models.ProjectSampleModel;

import com.github.dandelion.datatables.core.export.HtmlTableBuilder;
import com.github.dandelion.datatables.core.html.HtmlTable;
import com.google.common.collect.ImmutableList;

/**
 * Export project samples table.
 */
public class ProjectSamplesTableExport extends TableExport {
	/**
	 * Attributes on the {@link Sample}
	 */
	private static List<String> attributes = ImmutableList.of(
			"id",
			"sampleName",
			"projectName",
			"createdDate",
			"modifiedDate",
			"description",
			"organism",
			"isolate",
			"strain",
			"collectedBy",
			"collectionDate",
			"geographicLocationName",
			"isolationSource",
			"latitude",
			"longitude"
	);

	public ProjectSamplesTableExport(String exportFormat, String fileName, MessageSource messageSource, Locale locale) throws ExportFormatException {
		super(exportFormat, fileName, messageSource, locale);
	}

	public HtmlTable generateHtmlTable(Page<ProjectSampleJoin> page, HttpServletRequest request) {

		List<ProjectSampleModel> samples = page.getContent().stream().map(ProjectSampleModel::new)
				.collect(Collectors.toList());

		HtmlTableBuilder.ColumnStep steps = new HtmlTableBuilder<ProjectSampleModel>()
				.newBuilder("samples", samples, request, exportConf);

		for (String attr : attributes) {
			steps.column().fillWithProperty(attr).title(messageSource.getMessage("sample." + attr, new Object[]{}, locale));
		}

		return steps.column().fillWith("").title("").build();
	}
}
