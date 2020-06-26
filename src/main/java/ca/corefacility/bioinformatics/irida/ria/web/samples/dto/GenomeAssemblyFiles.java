package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;

/**
 * Used as a response for encapsulating a genome assembly and its file size
 */

public class GenomeAssemblyFiles {
	private GenomeAssembly genomeAssembly;
	private String fileSize;

	public GenomeAssemblyFiles(GenomeAssembly genomeAssembly, String fileSize) {
		this.genomeAssembly = genomeAssembly;
		this.fileSize = fileSize;
	}

	public GenomeAssembly getGenomeAssembly() {
		return genomeAssembly;
	}

	public void setGenomeAssembly(GenomeAssembly genomeAssembly) {
		this.genomeAssembly = genomeAssembly;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}
