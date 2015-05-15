package ca.corefacility.bioinformatics.irida.model.irida;

import java.nio.file.Path;
import java.util.Map;

/**
 * Describes fields that must be made available for a Sequence File in IRIDA
 * 
 *
 */
public interface IridaSequenceFile {

	/**
	 * Get the local numerical identifier
	 * 
	 * @return the numerical identifier for the sequence file.
	 */
	public Long getId();

	/**
	 * Get the location on the file system where the sequence file is stored
	 * 
	 * @return the physical location of the file.
	 */
	public Path getFile();

	/**
	 * Get a map of optional key/value pair properties
	 * 
	 * @return any additional properties provided by the sequencer.
	 */
	public Map<String, String> getOptionalProperties();

}
