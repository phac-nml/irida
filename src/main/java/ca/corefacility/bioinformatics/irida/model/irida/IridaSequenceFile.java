package ca.corefacility.bioinformatics.irida.model.irida;

import java.nio.file.Path;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Describes fields that must be made available for a Sequence File in IRIDA
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface IridaSequenceFile {

	/**
	 * Get the location on the file system where the sequence file is stored
	 * 
	 * @return
	 */
	@NotNull(message = "{sequencefile.file.notnull}")
	public Path getFile();

	/**
	 * Get a map of optional key/value pair properties
	 * 
	 * @return
	 */
	public Map<String, String> getOptionalProperties();

}
