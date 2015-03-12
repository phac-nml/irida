package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;

public class ResourceAdditionalProperties<Type extends IridaResourceSupport> {

	@JsonUnwrapped
	private Type resource;

	Map<String, Object> additionalProperties;

	public ResourceAdditionalProperties(Type resource) {
		this.resource = resource;
		additionalProperties = new HashMap<>();
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public void addProperty(String key, Object value) {
		additionalProperties.put(key, value);
	}
	
	public Type getResource() {
		return resource;
	}
	
	public void setResource(Type resource) {
		this.resource = resource;
	}
	
}
