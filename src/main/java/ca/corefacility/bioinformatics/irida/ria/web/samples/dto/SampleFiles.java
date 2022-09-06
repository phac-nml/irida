package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile.PairedEndSequenceFileModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.sequenceFile.SingleEndSequenceFileModel;

/**
 * Collection of different files that can be on a sample
 */
public class SampleFiles extends AjaxResponse {
	private final List<SingleEndSequenceFileModel> singles;
	private final List<PairedEndSequenceFileModel> pairs;
	private final List<SequencingObject> fast5;
	private final List<GenomeAssembly> assemblies;

	public SampleFiles(List<SingleEndSequenceFileModel> singles, List<PairedEndSequenceFileModel> pairs, List<SequencingObject> fast5,
			List<GenomeAssembly> assemblies) {
		this.singles = singles;
		this.pairs = pairs;
		this.fast5 = fast5;
		this.assemblies = assemblies;
	}

	public List<SingleEndSequenceFileModel> getSingles() {
		return singles;
	}

	public List<PairedEndSequenceFileModel> getPairs() {
		return pairs;
	}

	public List<SequencingObject> getFast5() {
		return fast5;
	}

	public List<GenomeAssembly> getAssemblies() {
		return assemblies;
	}

}
