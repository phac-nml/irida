package ca.corefacility.bioinformatics.irida.web.assembler.resource;

/**
 * The resource for displaying the API project response.
 *
 * @param <Type> The type of the object to be displayed in the project response
 */
public class ResponseProjectResource<Type> {
	private Type projectResources;

	public ResponseProjectResource(Type projectResources) {
		this.projectResources = projectResources;
	}

	public Type getProjectResources() {
		return projectResources;
	}

}