package ca.corefacility.bioinformatics.irida.model.irida;

import java.nio.file.Path;


/**
 * Describes fields that must be made available for a Sequence File in IRIDA
 */
public interface IridaSequenceFile {

	/**
	 * Get the location on the file system where the sequence file is stored
	 *
	 * @return the physical location of the file.
	 */
	public Path getFile();

	/**
	 * The base name of the file
	 *
	 * @return a string base name of the file
	 */
	public String getFileName();

}
