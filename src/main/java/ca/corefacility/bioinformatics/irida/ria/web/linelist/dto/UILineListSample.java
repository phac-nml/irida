package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public class UILineListSample {
	private Long id;

	public UILineListSample(Sample sample) {
		this.id = sample.getId();
	}
}
