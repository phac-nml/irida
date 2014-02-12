package ca.corefacility.bioinformatics.irida.model.upload;

/**
 * Defines the name of a project to be uploaded to a remote site.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public interface UploadProjectName {
	/**
	 * The name of the object to upload.
	 * 
	 * @return The name of the object to upload.
	 */
	public String getName();
}