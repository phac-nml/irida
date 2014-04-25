package ca.corefacility.bioinformatics.irida.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "remoteApi")
public class RemoteAPI implements Comparable<RemoteAPI>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Transient
	private URI serviceURI;
	@Column(name="serviceURI")
	private String stringServiceURI;
	
	private String description;
	
	public RemoteAPI(){
	}
	
	public RemoteAPI(URI serviceURI, String description){
		this.serviceURI = serviceURI;
		this.description = description;
	}
	
	@PostLoad
	public void postLoad() throws URISyntaxException{
		serviceURI = new URI(stringServiceURI);
	}
	
	@PrePersist
	@PreUpdate
	public void prePersist() {
		stringServiceURI = serviceURI.toString();
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

	@Override
	public int compareTo(RemoteAPI o) {
		return serviceURI.compareTo(o.serviceURI);
	}
	
    @Override
    public int hashCode() {
        return Objects.hash(serviceURI);
    }   
	
}
