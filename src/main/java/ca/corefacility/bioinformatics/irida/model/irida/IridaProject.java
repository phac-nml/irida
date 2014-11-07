package ca.corefacility.bioinformatics.irida.model.irida;

public interface IridaProject {

	public Long getId();

	public String getName();

	public String getProjectDescription();

	public String getRemoteURL();

	public String getOrganism();
}
