package ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Resource wrapper for {@link SequenceFile}. This file is required because the
 * sequencing run id is passed as a parameter of SequenceFile when a file is
 * added to a run.
 * 
 */
public class SequenceFileResource {

	private Long miseqRunId;

	private SequenceFile resource;


	public SequenceFileResource() {
		resource = new SequenceFile();
		resource.setStorageType(IridaFiles.getStorageType());
	}

	public SequenceFileResource(SequenceFile sequenceFile) {
		this.resource = sequenceFile;
	}

	@JsonUnwrapped
	public SequenceFile getResource() {
		return resource;
	}

	@JsonIgnore
	public Long getMiseqRunId() {
		return miseqRunId;
	}

	@JsonProperty
	public void setMiseqRunId(Long miseqRunId) {
		this.miseqRunId = miseqRunId;
	}

}
