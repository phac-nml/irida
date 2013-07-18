package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * This is an extension of {@link Resource} for types that have an identifier
 * (specifically, types other than join types).
 * 
 * @author Franklin Bristow
 * 
 * @param <Type>
 *            the type of resource exposed by this class.
 */
public abstract class IdentifiableResource<Type extends IridaThing> extends Resource<Type> {
	/**
	 * Construct an instance of an {@link IdentifiableResource}.
	 * 
	 * @param resource
	 *            the resource to be serialized to the client.
	 */
	public IdentifiableResource(Type resource) {
		super(resource);
	}

	/**
	 * Get the identifier of the resource to be serialized.
	 */
	@Override
	public String getIdentifier() {
		return resource.getId().toString();
	}
}
