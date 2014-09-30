package ca.corefacility.bioinformatics.irida.service.remote.model;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.remote.model.resource.RESTLink;
import ca.corefacility.bioinformatics.irida.service.remote.model.resource.RemoteResource;

/**
 * A project read from a remote Irida instance
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RemoteProject extends Project implements RemoteResource{
	private List<RESTLink> links;
	
	public String getIdentifier(){
		return this.getId().toString();
	}
	
	public void setIdentifier(String identifier){
		this.setId(Long.parseLong(identifier));
	}

	public List<RESTLink> getLinks() {
		return links;
	}

	public void setLinks(List<RESTLink> links) {
		this.links = links;
	}
}
