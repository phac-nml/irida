package ca.corefacility.bioinformatics.irida.model.upload;

/**
 * Defines the name of an object (file) to be uploaded to a remote site.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface UploadObjectName
{
	/**
	 * The name of the object to upload.
	 * @return  The name of the object to upload.
	 */
	public abstract String getName();
}