package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch;

/**
 * Information required to launch a IRIDA Workflow Pipeline
 */
public class LaunchRequest {
	/**
	 * Custom name so it is easier to search for the particular pipeline at a later point
	 */
	private String name;

	/**
	 * General text to provide more context for the pipeline.  No restrictions.
	 */
	private String description;

	public LaunchRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
