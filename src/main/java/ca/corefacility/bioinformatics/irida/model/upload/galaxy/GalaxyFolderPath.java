package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A name for a Galaxy folder path (eg. /illumina_reads/sample_name) used for
 * checking the validity of the path.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class GalaxyFolderPath {
	
	@NotNull
	@Size(min = 2)
	@Pattern.List({ @Pattern(regexp = "^[^\\?]+$"),
		@Pattern(regexp = "^[^\\(]+$"),
		@Pattern(regexp = "^[^\\)]+$"),
		@Pattern(regexp = "^[^\\[]+$"),
		@Pattern(regexp = "^[^\\]]+$"),
		
		/** keep forward slash since this is a path name **/ 
		//@Pattern(regexp = "^[^\\/]+$", message = "{irida.name.invalid.forward.slash}"),
		
		@Pattern(regexp = "^[^\\\\]+$"),
		@Pattern(regexp = "^[^\\=]+$"),
		@Pattern(regexp = "^[^\\+]+$"),
		@Pattern(regexp = "^[^\\<]+$"),
		@Pattern(regexp = "^[^\\>]+$"),
		@Pattern(regexp = "^[^\\:]+$"),
		@Pattern(regexp = "^[^\\;]+$"),
		@Pattern(regexp = "^[^\\\"]+$"),
		@Pattern(regexp = "^[^\\,]+$"),
		@Pattern(regexp = "^[^\\*]+$"),
		@Pattern(regexp = "^[^\\^]+$"),
		@Pattern(regexp = "^[^\\|]+$"),
		@Pattern(regexp = "^[^\\&]+$"),
		@Pattern(regexp = "^[^\\']+$"),
		@Pattern(regexp = "^[^\\.]+$"),
		@Pattern(regexp = "^[^ ]+$") })
	private String pathName;

	/**
	 * Builds a new Galaxy folder path with the given name.
	 * @param pathName  The name of the folder path.
	 */
	public GalaxyFolderPath(String pathName) {
		this.pathName = pathName;
	}

	/**
	 * Gets the name of this GalaxyFolderPath
	 * @return  The name of this folder path.
	 */
	public String getName() {
		return pathName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return pathName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(pathName);
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
		GalaxyFolderPath other = (GalaxyFolderPath) obj;
		
		return Objects.equals(this.pathName, other.pathName);
	}
}
