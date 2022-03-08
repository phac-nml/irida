package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleSequencingObjectFileModel;

/**
 * Represents a sample within the cart on the pipeline launch page.
 */
public class LaunchSample {
	private final Long id;
	private final String label;
	private final Project project;
	private final SequencingObject defaultSequencingObject;

	/*
	 * This is for the type of files that are allowed in the pipeline.
	 */
	private List<SampleSequencingObjectFileModel> files;

	public LaunchSample(Sample sample, ca.corefacility.bioinformatics.irida.model.project.Project project,
			SequencingObject defaultSequencingObject) {
		this.id = sample.getId();
		this.label = sample.getLabel();
		this.project = new Project(project);
		this.defaultSequencingObject = defaultSequencingObject;
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

	public List<SampleSequencingObjectFileModel> getFiles() {
		return files;
	}

	public void setFiles(List<SampleSequencingObjectFileModel> files) {
		this.files = files;
	}

	public SequencingObject getDefaultSequencingObject() {
		return defaultSequencingObject;
	}

	/**
	 * Pair down a project to exactly what is needed in the UI.
	 */
	static class Project {
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
