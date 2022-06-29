
package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;

/**
 * Used as a response for encapsulating analysis genome assembly sample data
 */

public class AnalysisGenomeAssemblySamples {
	private String sampleName;
	private Long sampleId;
	private Long genomeAssemblyId;
	private GenomeAssembly genomeAssembly;

	public AnalysisGenomeAssemblySamples() {
	}

	public AnalysisGenomeAssemblySamples(String sampleName, Long sampleId, Long genomeAssemblyId,
			GenomeAssembly genomeAssembly) {
		this.sampleName = sampleName;
		this.sampleId = sampleId;
		this.genomeAssemblyId = genomeAssemblyId;
		this.genomeAssembly = genomeAssembly;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public Long getAssemblyId() {
		return genomeAssemblyId;
	}

	public void setAssemblyId(Long genomeAssemblyId) {
		this.genomeAssemblyId = genomeAssemblyId;
	}

	public GenomeAssembly getGenomeAssembly() {
		return genomeAssembly;
	}

	public void setGenomeAssembly(GenomeAssembly genomeAssembly) {
		this.genomeAssembly = genomeAssembly;
	}
}