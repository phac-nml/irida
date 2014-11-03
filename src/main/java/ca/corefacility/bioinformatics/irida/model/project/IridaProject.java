package ca.corefacility.bioinformatics.irida.model.project;


public interface IridaProject {

	public Long getId();

	public void setId(Long id);

	public String getName();

	public void setName(String name);

	public String getProjectDescription();

	public void setProjectDescription(String projectDescription);

	public String getRemoteURL();

	public void setRemoteURL(String remoteURL);

	public String getOrganism();

	public void setOrganism(String organism);
}
