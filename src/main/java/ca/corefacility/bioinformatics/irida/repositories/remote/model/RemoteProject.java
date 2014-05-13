package ca.corefacility.bioinformatics.irida.repositories.remote.model;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.RemoteResource;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A project read from a remote Irida instance
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RemoteProject extends Project implements RemoteResource{
	protected List<Map<String,String>> links;
	
	public String getIdentifier(){
		return this.getId().toString();
	}
	
	public void setIdentifier(String identifier){
		this.setId(Long.parseLong(identifier));
	}

	public List<Map<String, String>> getLinks() {
		return links;
	}

	public void setLinks(List<Map<String, String>> links) {
		this.links = links;
	}
	
	public void setDateCreated(Date dateCreated){
		this.setTimestamp(dateCreated);
	}
	
	public Date getDateCreated(){
		return getTimestamp();
	}
}
