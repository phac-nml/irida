package ca.corefacility.bioinformatics.irida.model.genomeFile;

import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * A class which stores an assembled genome which can be used for different
 * pipelines.
 */
public interface AssembledGenome extends IridaThing {

	/**
	 * Gets the assembly file.
	 * @return The assembly file.
	 */
	public Path getFile();
	
	/**
	 * Gets the length of the assembled genome.
	 * @return The length of the assembled genome.
	 */
	public Long getFileLength();
}
