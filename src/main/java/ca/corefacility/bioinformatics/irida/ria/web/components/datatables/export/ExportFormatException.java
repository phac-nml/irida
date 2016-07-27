package ca.corefacility.bioinformatics.irida.ria.web.components.datatables.export;

/**
 * {@link Exception} thrown when attempting to export a table with an unknown format.
 */
public class ExportFormatException extends Exception {
	public ExportFormatException(String format) {
		super("Attempting to export via an unknown format [" + format + "]");
	}
}
