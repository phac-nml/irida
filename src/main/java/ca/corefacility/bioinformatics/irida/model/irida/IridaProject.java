package ca.corefacility.bioinformatics.irida.model.irida;

/**
 * Defines what must be exposed by a Project in IRIDA
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface IridaProject {

	/**
	 * Get the local numerical identifier for this project
	 * 
	 * @return
	 */
	public Long getId();

	/**
	 * Get the name of the project
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get a text description of the project
	 * 
	 * @return
	 */
	public String getProjectDescription();

	/**
	 * Get a URL that information for the project might be found. This might be
	 * a wiki page or discussion board.
	 * 
	 * @return
	 */
	public String getRemoteURL();

	/**
	 * Get the name of the organism being studied in this project
	 * 
	 * @return
	 */
	public String getOrganism();
}
