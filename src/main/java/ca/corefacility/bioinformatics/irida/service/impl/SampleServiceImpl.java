package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Validator;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;

/**
 * Service class for managing {@link Sample}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImpl extends CRUDServiceImpl<Long, Sample> implements SampleService {

	/**
	 * Reference to {@link SampleRepository} for managing {@link Sample}.
	 */
	private SampleRepository sampleRepository;
	/**
	 * Reference to {@link ProjectSampleJoinRepository} for managing
	 * {@link ProjectSampleJoin}.
	 */
	private ProjectSampleJoinRepository psjRepository;

	/**
	 * Reference to {@link SampleSequenceFileJoinRepository} for managing
	 * {@link SampleSequenceFileJoin}.
	 */
	private SampleSequenceFileJoinRepository ssfRepository;

	protected SampleServiceImpl() {
		super(null, null, Sample.class);
	}

	/**
	 * Constructor.
	 * 
	 * @param sampleRepository
	 *            the sample repository.
	 * @param validator
	 *            validator.
	 */
	public SampleServiceImpl(SampleRepository sampleRepository, ProjectSampleJoinRepository psjRepository,
			SampleSequenceFileJoinRepository ssfRepository, Validator validator) {
		super(sampleRepository, validator, Sample.class);
		this.sampleRepository = sampleRepository;
		this.psjRepository = psjRepository;
		this.ssfRepository = ssfRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Sample create(Sample s) {
		return super.create(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public SampleSequenceFileJoin addSequenceFileToSample(Sample sample, SequenceFile sampleFile) {
		// call the relationship repository to create the relationship between
		// the two entities.
		SampleSequenceFileJoin join = new SampleSequenceFileJoin(sample, sampleFile);
		return ssfRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Sample getSampleForProject(Project project, Long identifier) throws EntityNotFoundException {

		Sample s = null;

		// confirm that the link between project and this identifier exists
		List<Join<Project, Sample>> samplesForProject = psjRepository.getSamplesForProject(project);
		for (Join<Project, Sample> join : samplesForProject) {
			if (join.getObject().getId().equals(identifier)) {
				// load the sample from the database
				s = read(identifier);
			}
		}

		if (s == null) {
			throw new EntityNotFoundException("Join between the project and this identifier doesn't exist");
		}

		// return sample to the caller
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Sample getSampleByExternalSampleId(Project p, String sampleId) {
		Sample s = sampleRepository.getSampleByExternalSampleId(p, sampleId);
		if (s != null) {
			return s;
		} else {
			throw new EntityNotFoundException("No sample with external id [" + sampleId + "] in project [" + p.getId()
					+ "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public void removeSequenceFileFromSample(Sample sample, SequenceFile sequenceFile) {
		ssfRepository.removeFileFromSample(sample, sequenceFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<Join<Project, Sample>> getSamplesForProject(Project project) {
		return psjRepository.getSamplesForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public Sample mergeSamples(Project project, Sample mergeInto, Sample... toMerge) {
		// confirm that all samples are part of the same project:
		confirmProjectSampleJoin(project, mergeInto);

		for (Sample s : toMerge) {
			confirmProjectSampleJoin(project, s);
			List<Join<Sample, SequenceFile>> sequenceFiles = ssfRepository.getFilesForSample(s);
			for (Join<Sample, SequenceFile> sequenceFile : sequenceFiles) {
				removeSequenceFileFromSample(s, sequenceFile.getObject());
				addSequenceFileToSample(mergeInto, sequenceFile.getObject());
			}
			// have to remove the sample to be deleted from its project:
			psjRepository.removeSampleFromProject(project, s);
			sampleRepository.delete(s.getId());
		}
		return mergeInto;
	}

	private void confirmProjectSampleJoin(Project project, Sample sample) {
		Set<Project> projects = new HashSet<>();
		List<Join<Project, Sample>> sampleProjects = psjRepository.getProjectForSample(sample);
		for (Join<Project, Sample> p : sampleProjects) {
			projects.add(p.getSubject());
		}
		if (!projects.contains(project)) {
			throw new IllegalArgumentException("Cannot merge sample [" + sample.getId()
					+ "] with other samples; the sample does not belong to project [" + project.getId() + "]");
		}
	}
}
