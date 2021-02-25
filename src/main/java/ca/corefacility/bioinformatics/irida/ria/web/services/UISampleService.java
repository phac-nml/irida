package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleFiles;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * UI Service for samples
 */
@Component
public class UISampleService {
	private final SampleService sampleService;
	private final ProjectService projectService;
	private final UpdateSamplePermission updateSamplePermission;
	private final SequencingObjectService sequencingObjectService;
	private final GenomeAssemblyService genomeAssemblyService;
	private final UICartService cartService;

	@Autowired
	public UISampleService(SampleService sampleService, ProjectService projectService, UpdateSamplePermission updateSamplePermission,
			SequencingObjectService sequencingObjectService, GenomeAssemblyService genomeAssemblyService, UICartService cartService) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.updateSamplePermission = updateSamplePermission;
		this.sequencingObjectService = sequencingObjectService;
		this.genomeAssemblyService = genomeAssemblyService;
		this.cartService = cartService;
	}

	/**
	 * Get full details, including metadata for a {@link Sample}
	 *
	 * @param id Identifier for a {@link Sample}
	 * @return {@link SampleDetails}
	 */
	public SampleDetails getSampleDetails(Long id) {
		Sample sample = sampleService.read(id);
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		boolean isModifiable = updateSamplePermission.isAllowed(authentication, sample);
		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);
		return new SampleDetails(sample, isModifiable, metadataForSample, cartService.isSampleInCart(id));
	}

	/**
	 * Get the sequence files associated with a sample
	 *
	 * @param sampleId  Identifier for a sample
	 * @param projectId Identifier for the project the sample belong to
	 * @return All the sequencing files associated with the sample
	 */
	public SampleFiles getSampleFiles(Long sampleId, Long projectId) {
		Sample sample = sampleService.read(sampleId);
		// get the project if available
		Project project = null;
		if (projectId != null) {
			project = projectService.read(projectId);
		}

		List<SequencingObject> filePairs = getPairedSequenceFilesForSample(sample, project);
		List<SequencingObject> singles = getSingleEndSequenceFilesForSample(sample, project);
		List<SequencingObject> fast5 = getFast5FilesForSample(sample);
		List<GenomeAssembly> genomeAssemblies = getGenomeAssembliesForSample(sample);

		return new SampleFiles(singles, filePairs, fast5, genomeAssemblies);
	}

	/**
	 * Get a list of paired end sequence files for a sample
	 *
	 * @param sample  the {@link Sample} to get the files for.
	 * @param project the {@link Project} the sample belongs to
	 * @return list of paired end sequence files
	 */
	public List<SequencingObject> getPairedSequenceFilesForSample(Sample sample, Project project) {
		Collection<SampleSequencingObjectJoin> filePairJoins = sequencingObjectService.getSequencesForSampleOfType(
				sample, SequenceFilePair.class);
		// add project to qc entries and filter any unavailable entries
		List<SequencingObject> filePairs = new ArrayList<>();
		for (SampleSequencingObjectJoin join : filePairJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			filePairs.add(obj);
		}

		return filePairs;
	}

	/**
	 * Get a list of single end sequence files for a sample
	 *
	 * @param sample  the {@link Sample} to get the files for.
	 * @param project the {@link Project} the sample belongs to
	 * @return list of single end sequence files
	 */
	public List<SequencingObject> getSingleEndSequenceFilesForSample(Sample sample, Project project) {
		Collection<SampleSequencingObjectJoin> singleFileJoins = sequencingObjectService.getSequencesForSampleOfType(
				sample, SingleEndSequenceFile.class);

		List<SequencingObject> singles = new ArrayList<>();
		for (SampleSequencingObjectJoin join : singleFileJoins) {
			SequencingObject obj = join.getObject();
			enhanceQcEntries(obj, project);
			singles.add(obj);
		}

		return singles;
	}

	/**
	 * Get a list of fast5 sequence files for a sample
	 *
	 * @param sample the {@link Sample} to get the files for.
	 * @return list of fast5 sequence files
	 */
	public List<SequencingObject> getFast5FilesForSample(Sample sample) {
		Collection<SampleSequencingObjectJoin> fast5FileJoins = sequencingObjectService.getSequencesForSampleOfType(
				sample, Fast5Object.class);
		return fast5FileJoins.stream()
				.map(SampleSequencingObjectJoin::getObject)
				.collect(Collectors.toList());
	}

	/**
	 * Get any genome assemblies that are available for a sample
	 *
	 * @param sample the {@link Sample} to get the assemblies for
	 * @return a list of genome assembly files
	 */
	public List<GenomeAssembly> getGenomeAssembliesForSample(Sample sample) {
		Collection<SampleGenomeAssemblyJoin> genomeAssemblyJoins = genomeAssemblyService.getAssembliesForSample(sample);

		return genomeAssemblyJoins.stream()
				.map(SampleGenomeAssemblyJoin::getObject)
				.collect(Collectors.toList());
	}

	/**
	 * Adds the {@link Project} to any {@link QCEntry} within a
	 * {@link SequencingObject}. If the {@link QCEntry} reports as
	 * {@link QCEntry.QCEntryStatus#UNAVAILABLE} after being enhanced it is removed from
	 * the list
	 *
	 * @param obj     the {@link SequencingObject} to enhance
	 * @param project the {@link Project} to add
	 */
	private void enhanceQcEntries(SequencingObject obj, Project project) {
		Set<QCEntry> availableEntries = new HashSet<>();
		if (obj.getQcEntries() != null) {
			for (QCEntry q : obj.getQcEntries()) {
				q.addProjectSettings(project);
				if (!q.getStatus()
						.equals(QCEntry.QCEntryStatus.UNAVAILABLE)) {
					availableEntries.add(q);
				}
			}
		}

		obj.setQcEntries(availableEntries);
	}
}
