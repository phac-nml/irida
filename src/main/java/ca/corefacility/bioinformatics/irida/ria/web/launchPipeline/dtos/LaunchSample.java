package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

public class LaunchSamples {
	private final Long id;
	private final String label;
	private final Project project;
	private List<SequencingObject> singles;
	private List<SequencingObject> paired;
	private List<SequencingObject> fast5;
	private List<GenomeAssembly> assemblies;

	public LaunchSamples(Sample sample, ca.corefacility.bioinformatics.irida.model.project.Project project) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.project = new Project(project);
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public Project getProject() {
		return project;
	}

	public List<SequencingObject> getSingles() {
		return singles;
	}

	public void setSingles(List<SequencingObject> singles) {
		this.singles = singles;
	}

	public List<SequencingObject> getPaired() {
		return paired;
	}

	public void setPaired(List<SequencingObject> paired) {
		this.paired = paired;
	}

	public List<SequencingObject> getFast5() {
		return fast5;
	}

	public void setFast5(List<SequencingObject> fast5) {
		this.fast5 = fast5;
	}

	public List<GenomeAssembly> getAssemblies() {
		return assemblies;
	}

	public void setAssemblies(List<GenomeAssembly> assemblies) {
		this.assemblies = assemblies;
	}

	private static class Project {
		private final Long id;
		private final String label;

		public Project(ca.corefacility.bioinformatics.irida.model.project.Project project) {
			this.id = project.getId();
			this.label = project.getLabel();
		}

		public Long getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}
	}
}
