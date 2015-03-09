package ca.corefacility.bioinformatics.irida.model.upload;

/**
 * An account name for setting up permissions of uploaded files to a remote
 * endpoint.
 * 
 * 
 */
public interface UploaderAccountName {
	/**
	 * The name of this account as a string.
	 * 
	 * @return The name of this account.
	 */
	public String getName();
}