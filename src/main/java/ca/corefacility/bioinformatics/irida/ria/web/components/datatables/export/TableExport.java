package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export;

import java.util.Locale;

import org.springframework.context.MessageSource;

import com.github.dandelion.datatables.core.export.CsvExport;
import com.github.dandelion.datatables.core.export.ExportConf;
import com.github.dandelion.datatables.core.export.ReservedFormat;
import com.github.dandelion.datatables.extras.export.poi.XlsxExport;

/**
 * Abstract class to support exporting items from datatables into excel or csv.
 */
abstract class TableExport {
	protected ExportConf exportConf;
	protected MessageSource messageSource;
	protected Locale locale;

	public TableExport(String exportFormat, String fileName, MessageSource messageSource, Locale locale)
			throws ExportFormatException {
		this.messageSource = messageSource;
		this.locale = locale;

		// Create the correct export format based on the type required.
		if (exportFormat.equals(ReservedFormat.XLSX)) {
			this.exportConf = new ExportConf.Builder(ReservedFormat.XLSX)
					.header(true)
					.exportClass(new XlsxExport())
					.fileName(fileName)
					.build();
		} else if (exportFormat.equals(ReservedFormat.CSV)) {
			this.exportConf = new ExportConf.Builder(ReservedFormat.CSV)
					.header(true)
					.exportClass(new CsvExport())
					.fileName(fileName)
					.build();
		} else {
			throw new ExportFormatException(exportFormat);
		}
	}

	public ExportConf getExportConf() {
		return this.exportConf;
	}
}
