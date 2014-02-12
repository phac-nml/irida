package ca.corefacility.bioinformatics.irida.model.upload;

/**
 * Defines the name of a folder to be created in a remote site.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public interface UploadFolderName {
	/**
	 * The name of the folder to create.
	 * 
	 * @return The name of the folder to create.
	 */
	public String getName();
}