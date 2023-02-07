package ca.corefacility.bioinformatics.irida.model;

import java.io.Serializable;

import javax.persistence.Version;


/**
 * An instance of a class may have a property with {@link Version} or may have
 * an internally managed version representation. This interface exposes that
 * version.
 * 
 * @param <VersionType>
 *            the type used to internally store the current version of the
 *            object (usually a number.)
 */
public interface VersionedFileFields<VersionType extends Serializable> {

	/**
	 * Get the version of the instance.
	 * 
	 * @return the version of the instance.
	 */
	public VersionType getFileRevisionNumber();

	/**
	 * Internally modify the file revision number to something new.
	 */
	public void incrementFileRevisionNumber();

}
