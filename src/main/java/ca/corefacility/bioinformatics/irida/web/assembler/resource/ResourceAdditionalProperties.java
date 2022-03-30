package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;

/**
 * Class to be used when returning additional properties about a model object
 * via the REST API. This class will embed a model class and add a map of
 * freeform properties. When serializing to JSON it will be transparent that
 * these properties weren't on the model object to begin with.
 * 
 * Example of this being used is adding the number of sequence files to a
 * sample.
 * 
 * @param <Type>
 *            The type of class being embedded.
 */
public class ResourceAdditionalProperties<Type extends IridaRepresentationModel> {

	@JsonUnwrapped
	private Type resource;

	Map<String, Object> additionalProperties;

	public ResourceAdditionalProperties(Type resource) {
		this.resource = resource;
		additionalProperties = new HashMap<>();
	}

	/**
	 * Get the added properties
	 * 
	 * @return A {@code Map<String,Object>}
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	/**
	 * Set the map of additional properties
	 * 
	 * @param additionalProperties
	 *            A {@code Map<String,Object>} of properties to set
	 */
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	/**
	 * Add a property to the map
	 * 
	 * @param key
	 *            The key to set
	 * @param value
	 *            the value to set
	 */
	public void addProperty(String key, Object value) {
		additionalProperties.put(key, value);
	}

	/**
	 * Get the embedded resource
	 * 
	 * @return The resource of Type
	 */
	public Type getResource() {
		return resource;
	}

	/**
	 * Set the resource of this object
	 * 
	 * @param resource
	 *            The resource to set
	 */
	public void setResource(Type resource) {
		this.resource = resource;
	}

}
