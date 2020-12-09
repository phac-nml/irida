package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Collection of different files that can be on a sample
 */
public class SampleFiles extends AjaxResponse {
	private final List<SequencingObject> singles;
	private final List<SequencingObject> paired;
	private final List<SequencingObject> fast5;
	private final List<GenomeAssembly> assemblies;

	public SampleFiles(List<SequencingObject> singles, List<SequencingObject> paired, List<SequencingObject> fast5,
			List<GenomeAssembly> assemblies) {
		this.singles = singles;
		this.paired = paired;
		this.fast5 = fast5;
		this.assemblies = assemblies;
	}

	public List<SequencingObject> getSingles() {
		return singles;
	}

	public List<SequencingObject> getPaired() {
		return paired;
	}

	public List<SequencingObject> getFast5() {
		return fast5;
	}

	public List<GenomeAssembly> getAssemblies() {
		return assemblies;
	}

}
