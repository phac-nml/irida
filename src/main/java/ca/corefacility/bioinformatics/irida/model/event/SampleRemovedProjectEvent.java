package ca.corefacility.bioinformatics.irida.model.event;

import java.util.Objects;

import javax.persistence.Entity;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Event for when a sample is removed from a project. Note this won't link to
 * the sample and only show the sample name because users may not have access to
 * the sample anymore.
 */
@Entity
public class SampleRemovedProjectEvent extends ProjectEvent {
	String sampleName;

	public SampleRemovedProjectEvent() {
	}

	public SampleRemovedProjectEvent(Project project, String sampleName) {
		super(project);
		this.sampleName = sampleName;
	}

	@Override
	public String getLabel() {
		return "Sample " + sampleName + " removed from project " + getProject();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SampleRemovedProjectEvent) {
			SampleRemovedProjectEvent p = (SampleRemovedProjectEvent) other;
			return super.equals(other) && Objects.equals(sampleName, p.sampleName);
		}

		return false;
	}

	public String getSampleName() {
		return sampleName;
	}

}
