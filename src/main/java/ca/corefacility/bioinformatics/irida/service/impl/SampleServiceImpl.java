package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SampleService;

/**
 * Service class for managing {@link Sample}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SampleServiceImpl extends CRUDServiceImpl<Long, Sample> implements SampleService {

	/**
	 * Reference to {@link CRUDRepository} for managing {@link Sample}.
	 */
	private SampleRepository sampleRepository;
	/**
	 * Reference to {@link SequenceFileRepository} for managing
	 * {@link SequenceFile}.
	 */
	private SequenceFileRepository sequenceFileRepository;

	private ProjectRepository projectRespository;

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
	public SampleServiceImpl(SampleRepository sampleRepository, SequenceFileRepository sequenceFileRepository,
			ProjectRepository projectRepository, Validator validator) {
		super(sampleRepository, validator, Sample.class);
		this.projectRespository = projectRepository;
		this.sampleRepository = sampleRepository;
		this.sequenceFileRepository = sequenceFileRepository;
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
		return sequenceFileRepository.addFileToSample(sample, sampleFile);
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
		List<ProjectSampleJoin> samplesForProject = sampleRepository.getSamplesForProject(project);
		for (ProjectSampleJoin join : samplesForProject) {
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
		return sampleRepository.getSampleByExternalSampleId(p, sampleId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public void removeSequenceFileFromSample(Sample sample, SequenceFile sequenceFile) {
		sequenceFileRepository.removeFileFromSample(sample, sequenceFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<Join<Project, Sample>> getSamplesForProject(Project project) {
		return new ArrayList<Join<Project, Sample>>(sampleRepository.getSamplesForProject(project));
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
			List<SampleSequenceFileJoin> sequenceFiles = sequenceFileRepository.getFilesForSample(s);
			for (SampleSequenceFileJoin sequenceFile : sequenceFiles) {
				removeSequenceFileFromSample(s, sequenceFile.getObject());
				addSequenceFileToSample(mergeInto, sequenceFile.getObject());
			}
			sampleRepository.delete(s.getId());
		}
		return mergeInto;
	}

	private void confirmProjectSampleJoin(Project project, Sample sample) {
		Set<Project> projects = new HashSet<>();
		Collection<ProjectSampleJoin> sampleProjects = projectRespository.getProjectForSample(sample);
		for (ProjectSampleJoin p : sampleProjects) {
			projects.add(p.getSubject());
		}
		if (!projects.contains(project)) {
			throw new IllegalArgumentException("Cannot merge sample [" + sample.getId()
					+ "] with other samples; the sample does not belong to project [" + project.getId() + "]");
		}
	}
}
