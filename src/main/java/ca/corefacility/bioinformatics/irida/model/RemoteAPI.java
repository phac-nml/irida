package ca.corefacility.bioinformatics.irida.model;

import java.net.URI;
import java.util.Objects;

public class RemoteAPI {
	private Long id;
	private URI serviceURI;
	private String description;
	
	public RemoteAPI(URI serviceURI, String description){
		this.serviceURI = serviceURI;
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public URI getServiceURI() {
		return serviceURI;
	}
	public void setServiceURI(URI serviceURI) {
		this.serviceURI = serviceURI;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "RemoteAPI [" + serviceURI + ", " + description + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RemoteAPI) {
			RemoteAPI p = (RemoteAPI) other;
			return Objects.equals(serviceURI, p.serviceURI);
		}

		return false;
	}
	
}
