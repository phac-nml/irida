package ca.corefacility.bioinformatics.irida.model.event;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

@Entity
public class DataAddedToSampleProjectEvent extends ProjectEvent {

	@ManyToOne(cascade = CascadeType.DETACH)
	@NotNull
	private Sample sample;

	public DataAddedToSampleProjectEvent() {
	}

	public DataAddedToSampleProjectEvent(Project project, Sample sample) {
		super(project);
		this.sample = sample;
	}

	@Override
	public String getLabel() {
		return "Data added to sample " + sample;
	}

	public Sample getSample() {
		return sample;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof DataAddedToSampleProjectEvent) {
			DataAddedToSampleProjectEvent p = (DataAddedToSampleProjectEvent) other;
			return super.equals(other) && Objects.equals(sample, p.sample);
		}

		return false;
	}

}
