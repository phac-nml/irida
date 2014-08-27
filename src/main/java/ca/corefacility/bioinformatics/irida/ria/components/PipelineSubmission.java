package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

/**
 * Component for handling data needed for pipeline submission
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Component
@Scope("session")
public class PipelineSubmission {
	private ReferenceFile referenceFile;
	private List<SequenceFile> sequenceFiles;

	/*
	 * SERVICES
	 */
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;

	@Autowired
	public PipelineSubmission(ReferenceFileService referenceFileService, SequenceFileService sequenceFileService) {
		this.referenceFileService = referenceFileService;
		this.sequenceFileService = sequenceFileService;
	}

	public void setReferenceFile(Long referenceFileId) {
		this.referenceFile = referenceFileService.read(referenceFileId);
	}

	public ReferenceFile getReferenceFile() {
		return this.referenceFile;
	}

	public void setSequenceFiles(List<Long> fileIds) {
		this.sequenceFiles = new ArrayList<>(fileIds.size());
		sequenceFiles.addAll(fileIds.stream().map(sequenceFileService::read).collect(Collectors.toList()));
	}

	public List<SequenceFile> getSequenceFiles() {
		return this.sequenceFiles;
	}
}
