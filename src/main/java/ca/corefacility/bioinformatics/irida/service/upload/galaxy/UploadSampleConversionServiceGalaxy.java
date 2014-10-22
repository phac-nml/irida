package ca.corefacility.bioinformatics.irida.service.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.upload.UploadSampleConversionService;

/**
 * Used to convert IRIDA Samples to those that can be uploaded into Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class UploadSampleConversionServiceGalaxy implements
		UploadSampleConversionService {

	private ProjectRepository projectRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleSequenceFileJoinRepository ssfjRepository;
	private SequenceFileRepository sfRepository;

	/**
	 * Builds a new GalaxySampleConversionService for convering samples to those
	 * that can be uploaded to Galaxy.
	 * 
	 * @param projectRepository
	 *            The repository of all projects.
	 * @param ssfjRepository
	 *            The repository for joining sequence files and samples.
	 * @param sfRepository
	 *            The repository for accessing sequence files.
	 */
	@Autowired
	public UploadSampleConversionServiceGalaxy(
			ProjectRepository projectRepository,
			ProjectSampleJoinRepository psjRepository,
			SampleSequenceFileJoinRepository ssfjRepository,
			SequenceFileRepository sfRepository) {
		this.projectRepository = projectRepository;
		this.psjRepository = psjRepository;
		this.ssfjRepository = ssfjRepository;
		this.sfRepository = sfRepository;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public UploadSample convertToUploadSample(Sample sample) {

		UploadSample galaxySample;

		List<Path> sampleFiles = new LinkedList<Path>();

		String sampleName = sample.getSampleName();

		List<Join<Sample, SequenceFile>> sequenceFileJoins = ssfjRepository
				.getFilesForSample(sample);
		for (Join<Sample, SequenceFile> sequenceJoin : sequenceFileJoins) {
			SequenceFile sequenceFileObject = sequenceJoin.getObject();

			Path sequenceFilePath = sequenceFileObject.getFile();

			sampleFiles.add(sequenceFilePath);
		}

		galaxySample = new GalaxySample(new GalaxyFolderName(sampleName),
				sampleFiles);

		return galaxySample;
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public Set<UploadSample> getUploadSamplesForProject(Long projectId) {
		checkNotNull(projectId, "projectId is null");

		Set<UploadSample> galaxySamples = new HashSet<>();

		Project project = projectRepository.findOne(projectId);
		if (project == null) {
			throw new EntityNotFoundException("No such project with id "
					+ projectId + " found");
		} else {
			List<Join<Project, Sample>> sampleList = psjRepository
					.getSamplesForProject(project);
			for (Join<Project, Sample> js : sampleList) {
				Sample sample = js.getObject();

				UploadSample galaxySample = convertToUploadSample(sample);
				galaxySamples.add(galaxySample);
			}

			return galaxySamples;
		}
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public Set<UploadSample> convertToUploadSamples(Set<Sample> samples) {

		Set<UploadSample> galaxySamples = new HashSet<>();

		for (Sample sample : samples) {
			UploadSample galaxySample = convertToUploadSample(sample);
			galaxySamples.add(galaxySample);
		}

		return galaxySamples;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<UploadSample> convertToUploadSamples(Sample... samples) {
		return convertToUploadSamples(Sets.newHashSet(samples));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<UploadSample> convertSequenceFilesToUploadSamples(
			Set<SequenceFile> sequenceFiles) throws EntityNotFoundException {
		checkNotNull(sequenceFiles, "sequenceFiles are null");

		Map<Long, UploadSample> samplesMap = new HashMap<>();

		for (SequenceFile sequenceFile : sequenceFiles) {
			Join<Sample, SequenceFile> sampleSequenceFile = ssfjRepository
					.getSampleForSequenceFile(sequenceFile);
			Sample sample = sampleSequenceFile.getSubject();

			UploadSample uploadSample;
			if (samplesMap.containsKey(sample.getId())) {
				uploadSample = samplesMap.get(sample.getId());
			} else {
				UploadFolderName sampleName = new GalaxyFolderName(
						sample.getSampleName());
				uploadSample = new GalaxySample(sampleName);
				samplesMap.put(sample.getId(), uploadSample);
			}

			uploadSample.addSampleFile(sequenceFile.getFile());
		}

		return Sets.newHashSet(samplesMap.values());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<UploadSample> convertSequenceFilesByIdToUploadSamples(
			Set<Long> sequenceFileIds) throws EntityNotFoundException {
		checkNotNull(sequenceFileIds, "sequenceFileIds is null");

		Set<SequenceFile> sequenceFiles = new HashSet<>();

		for (Long sequenceFileId : sequenceFileIds) {
			SequenceFile sequenceFile = sfRepository.findOne(sequenceFileId);

			if (sequenceFile == null) {
				throw new EntityNotFoundException(
						"SequenceFile identified by [" + sequenceFileId
								+ "] not found");
			} else {
				sequenceFiles.add(sequenceFile);
			}
		}

		return convertSequenceFilesToUploadSamples(sequenceFiles);
	}
}
