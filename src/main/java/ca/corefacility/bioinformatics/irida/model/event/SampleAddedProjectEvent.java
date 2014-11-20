package ca.corefacility.bioinformatics.irida.model.event;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

@Entity
@Table(name = "project_event_sample_added")
public class SampleAddedProjectEvent extends ProjectEvent {
	@ManyToOne(cascade = CascadeType.DETACH)
	@NotNull
	private Sample sample;

	public SampleAddedProjectEvent() {
	}

	public SampleAddedProjectEvent(Project project, Sample sample) {
		super(project);
		this.sample = sample;
	}

	@Override
	public String getLabel() {
		return "Sample " + sample.getLabel() + " added to project " + getProject().getLabel();
	}

	public Sample getSample() {
		return sample;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SampleAddedProjectEvent) {
			SampleAddedProjectEvent p = (SampleAddedProjectEvent) other;
			return super.equals(other) && Objects.equals(sample, p.sample);
		}

		return false;
	}

}
