package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

/**
 * Created by josh on 2015-10-27.
 */
public class ProjectsFilter {
	public String getName() {
		return name;
	}

	public String getOrganism() {
		return organism;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProjectsFilter() {

	}

	public ProjectsFilter(String name, String organism) {
		this.name = name;
		this.organism = organism;
	}

	private String name;
	private String organism;
}
