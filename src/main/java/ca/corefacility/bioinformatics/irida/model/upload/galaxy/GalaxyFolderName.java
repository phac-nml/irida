package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

/**
 * A name for a Galaxy project (Library) used for checking the validity
 * of the name.  Differs from GalaxyFolderPath in that a folder path
 * contains '/' separating different folders.  Both paths and names
 * are used by blend4j for different purposes.
 * 
 * 
 */
public class GalaxyFolderName implements UploadFolderName {
	@NotNull(message = "{galaxy.object.notnull}")
	@Size(min = 2, message = "{galaxy.object.size}")
	@ValidSampleName
	private String sampleName;

	/**
	 * Builds a new GalaxySampleName with the given name.
	 * @param sampleName  The name of the Galaxy sample.
	 */
	public GalaxyFolderName(String sampleName) {
		this.sampleName = sampleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return sampleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return sampleName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(sampleName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalaxyFolderName other = (GalaxyFolderName) obj;
		
		return Objects.equals(this.sampleName, other.sampleName);
	}
}
