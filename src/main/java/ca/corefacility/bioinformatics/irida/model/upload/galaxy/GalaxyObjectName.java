package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;

/**
 * A name for a Galaxy object (Library, Folder) used for checking the validity
 * of the name.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class GalaxyObjectName implements UploadObjectName {
	@NotNull(message = "{galaxy.object.notnull}")
	@Size(min = 2, message = "{galaxy.object.size}")
	@Pattern(regexp = "^[A-Za-z0-9 \\-_\\.']+$", message = "{galaxy.object.invalid}")
	private String objectName;

	/**
	 * Builds a new GalaxyObjectName with the given name.
	 * @param objectName  The name of the Galaxy object.
	 */
	public GalaxyObjectName(String objectName) {
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
		GalaxyObjectName other = (GalaxyObjectName) obj;
		
		return Objects.equals(this.objectName, other.objectName);
	}
}
