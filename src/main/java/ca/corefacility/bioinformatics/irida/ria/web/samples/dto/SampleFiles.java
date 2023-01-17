package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Collection of different files that can be on a sample
 */
public class SampleFiles extends AjaxResponse {
	private final List<SampleSequencingObjectFileModel> singles;
	private final List<SampleSequencingObjectFileModel> paired;
	private final List<SampleSequencingObjectFileModel> fast5;
	private final List<SampleGenomeAssemblyFileModel> assemblies;

	public SampleFiles(List<SampleSequencingObjectFileModel> singles, List<SampleSequencingObjectFileModel> paired,
			List<SampleSequencingObjectFileModel> fast5, List<SampleGenomeAssemblyFileModel> assemblies) {
		this.singles = singles;
		this.paired = paired;
		this.fast5 = fast5;
		this.assemblies = assemblies;
	}

	public List<SampleSequencingObjectFileModel> getSingles() {
		return singles;
	}

	public List<SampleSequencingObjectFileModel> getPaired() {
		return paired;
	}

	public List<SampleSequencingObjectFileModel> getFast5() {
		return fast5;
	}

	public List<SampleGenomeAssemblyFileModel> getAssemblies() {
		return assemblies;
	}

}
