package ca.corefacility.bioinformatics.irida.model.project;

public interface IridaProject {

	public Long getId();

	public String getName();

	public String getProjectDescription();

	public String getRemoteURL();

	public String getOrganism();
}
