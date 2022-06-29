package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Represents a sample within the cart on the pipeline launch page.
 */
public class LaunchSample {
	private final Long id;
	private final String label;
	private final Project project;

	/*
	 * This is for the type of files that are allowed in the pipeline.
	 */
	private List<SequencingObject> files;

	private List<GenomeAssembly> assemblyFiles;

	public LaunchSample(Sample sample, ca.corefacility.bioinformatics.irida.model.project.Project project) {
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

	public List<SequencingObject> getFiles() {
		return files;
	}

	public void setFiles(List<SequencingObject> files) {
		this.files = files;

	}

	public List<GenomeAssembly> getAssemblyFiles() {
		return assemblyFiles;
	}

	public void setAssemblyFiles(List<GenomeAssembly> assemblyFiles) {
		this.assemblyFiles = assemblyFiles;
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
