package ca.corefacility.bioinformatics.irida.service.impl.sample;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Service class for managing {@link Sample}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Service
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

	/**
	 * Constructor.
	 * 
	 * @param sampleRepository
	 *            the sample repository.
	 * @param validator
	 *            validator.
	 */
	@Autowired
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
	@Transactional
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
	public Sample getSampleForProject(Project project, Long identifier) throws EntityNotFoundException {
		Optional<Sample> sample = psjRepository.getSamplesForProject(project).stream().map(j -> j.getObject())
				.filter(s -> s.getId().equals(identifier)).findFirst();
		if (sample.isPresent()) {
			return sample.get();
		} else {
			throw new EntityNotFoundException("Join between the project and this identifier doesn't exist");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Sample getSampleBySequencerSampleId(Project project, String sampleId) {
		Sample s = sampleRepository.getSampleBySequencerSampleId(project, sampleId);
		if (s != null) {
			return s;
		} else {
			throw new EntityNotFoundException("No sample with external id [" + sampleId + "] in project ["
					+ project.getId() + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void removeSequenceFileFromSample(Sample sample, SequenceFile sequenceFile) {
		ssfRepository.removeFileFromSample(sample, sequenceFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sample read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<ProjectSampleJoin> getSamplesForProjectWithName(Project project, String name, int page, int size,
			Direction order, String... sortProperties) {
		if (sortProperties.length == 0) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}
		return psjRepository.findAll(ProjectSampleJoinSpecification.searchSampleWithNameInProject(name, project),
				new PageRequest(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<ProjectSampleJoin> searchProjectSamples(Specification<ProjectSampleJoin> specification, int page,
			int size, Direction order, String... sortProperties) {
		// if the sort properties are null, empty, or are an empty string, use
		// CREATED_DATE
		if (sortProperties == null || sortProperties.length == 0
				|| (sortProperties.length == 1 && sortProperties[0].equals(""))) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}

		return psjRepository.findAll(specification, new PageRequest(page, size, order, sortProperties));
	}
}
