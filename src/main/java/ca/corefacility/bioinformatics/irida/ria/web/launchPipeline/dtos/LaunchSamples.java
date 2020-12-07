package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleFiles;

public class LaunchSamples {
	private final Long id;
	private final String label;
	private final Project project;
	private final SampleFiles files;

	public LaunchSamples(Sample sample, ca.corefacility.bioinformatics.irida.model.project.Project project, SampleFiles files) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.project = new Project(project);
		this.files = files;
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

	public SampleFiles getFiles() {
		return files;
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
