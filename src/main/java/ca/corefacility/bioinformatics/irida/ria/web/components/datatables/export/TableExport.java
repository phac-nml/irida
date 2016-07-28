package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export;

import com.github.dandelion.datatables.core.export.CsvExport;
import com.github.dandelion.datatables.core.export.ExportConf;
import com.github.dandelion.datatables.core.export.ReservedFormat;
import com.github.dandelion.datatables.extras.export.poi.XlsxExport;

/**
 * Created by josh on 2016-07-27.
 */
abstract class TableExport {
	protected ExportConf exportConf;

	public TableExport(String exportFormat, String fileName) throws ExportFormatException {
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
