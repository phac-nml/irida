package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.github.dandelion.datatables.core.export.ExportConf;
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
			"createdDate",
			"modifiedDate",
			"sampleName",
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

	public ProjectSamplesTableExport(String exportFormat, String fileName) throws ExportFormatException {
		super(exportFormat, fileName);
	}

	public HtmlTable generateHtmlTable(List<Sample> samples, HttpServletRequest request) {
		HtmlTableBuilder.ColumnStep steps = new HtmlTableBuilder<Sample>()
				.newBuilder("samples", samples, request, exportConf);

		for (String attr : attributes) {
			steps.column().fillWithProperty(attr).title(attr);
		}

		return steps.column().fillWith("").title("").build();
	}
}
