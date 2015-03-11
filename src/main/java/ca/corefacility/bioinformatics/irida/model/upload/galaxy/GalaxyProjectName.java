package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;

/**
 * A name for a Galaxy project (Library) used for checking the validity
 * of the name.
 * 
 * 
 */
public class GalaxyProjectName implements UploadProjectName {
	@NotNull(message = "{galaxy.object.notnull}")
	@Size(min = 2, message = "{galaxy.object.size}")
	@ValidProjectName
	private String objectName;

	/**
	 * Builds a new GalaxyObjectName with the given name.
	 * @param objectName  The name of the Galaxy object.
	 */
	public GalaxyProjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return objectName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return objectName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(objectName);
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
		GalaxyProjectName other = (GalaxyProjectName) obj;
		
		return Objects.equals(this.objectName, other.objectName);
	}
}
