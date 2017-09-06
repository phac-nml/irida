package ca.corefacility.bioinformatics.irida.service.impl.sample;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleSpecification;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Service class for managing {@link Sample}.
 * 
 */
@Service
public class SampleServiceImpl extends CRUDServiceImpl<Long, Sample> implements SampleService {

	private static final Logger logger = LoggerFactory.getLogger(SampleServiceImpl.class);

	/**
	 * Reference to {@link SampleRepository} for managing {@link Sample}.
	 */
	private SampleRepository sampleRepository;
	/**
	 * Reference to {@link ProjectSampleJoinRepository} for managing
	 * {@link ProjectSampleJoin}.
	 */
	private ProjectSampleJoinRepository psjRepository;
	
	private SampleSequencingObjectJoinRepository ssoRepository;
	
	private QCEntryRepository qcEntryRepository;
	
	private SequencingObjectRepository sequencingObjectRepository;

	/**
	 * Reference to {@link AnalysisRepository}.
	 */
	private final AnalysisRepository analysisRepository;
	
	private final SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;


	/**
	 * Constructor.
	 * 
	 * @param sampleRepository
	 *            the sample repository.
	 * @param psjRepository
	 *            the project sample join repository.
	 * @param analysisRepository
	 *            the analysis repository.
	 * @param ssoRepository
	 *            The {@link SampleSequencingObjectJoin} repository
	 * @param sequencingObjectRepository
	 *            the {@link SequencingObject} repository
	 * @param sampleGenomeAssemblyJoinRepository
	 *            The {@link SampleGenomeAssemblyJoin} repository.
	 * @param qcEntryRepository
	 *            a repository for storing and reading {@link QCEntry}
	 * @param validator
	 *            validator.
	 */
	@Autowired
	public SampleServiceImpl(SampleRepository sampleRepository, ProjectSampleJoinRepository psjRepository,
			final AnalysisRepository analysisRepository, SampleSequencingObjectJoinRepository ssoRepository,
			QCEntryRepository qcEntryRepository, SequencingObjectRepository sequencingObjectRepository,
			SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository, Validator validator) {
		super(sampleRepository, validator, Sample.class);
		this.sampleRepository = sampleRepository;
		this.psjRepository = psjRepository;
		this.analysisRepository = analysisRepository;
		this.ssoRepository = ssoRepository;
		this.qcEntryRepository = qcEntryRepository;
		this.sequencingObjectRepository = sequencingObjectRepository;
		this.sampleGenomeAssemblyJoinRepository = sampleGenomeAssemblyJoinRepository;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#idents, 'canReadSample')")
	public Iterable<Sample> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#id, 'canReadSample')")
	public Boolean exists(Long id) {
		return super.exists(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SEQUENCER')")
	public Sample create(final @Valid Sample s) {
		return super.create(s);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#id, 'canUpdateSample')")
	public Sample updateFields(Long id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException, InvalidPropertyException {
		return super.updateFields(id, updatedFields);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#object, 'canUpdateSample')")
	@Override
	public Sample update(Sample object) {
		return super.update(object);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SEQUENCER') or (hasPermission(#project, 'canReadProject') and hasPermission(#sampleId, 'canReadSample'))")
	public ProjectSampleJoin getSampleForProject(Project project, Long sampleId) {
		Sample sample = read(sampleId);
		ProjectSampleJoin join = psjRepository.readSampleForProject(project, sample);
		if (join == null) {
			throw new EntityNotFoundException("Join between the project and this identifier doesn't exist");

		}
		return join;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	public List<String> getSampleOrganismsForProject(Project project) {
		return psjRepository.getSampleOrganismsForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#seqObject, 'canReadSequencingObject')")
	public SampleSequencingObjectJoin getSampleForSequencingObject(SequencingObject seqObject) {
		return ssoRepository.getSampleForSequencingObject(seqObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#project, 'canReadProject')")
	public Sample getSampleBySampleName(Project project, String sampleId) {
		Sample s = sampleRepository.getSampleBySampleName(project, sampleId);
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
	@PreAuthorize("hasPermission(#sample, 'canUpdateSample')")
	public void removeSequencingObjectFromSample(Sample sample, SequencingObject object) {
		SampleSequencingObjectJoin readObjectForSample = ssoRepository.readObjectForSample(sample, object.getId());
		ssoRepository.delete(readObjectForSample);
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
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Long getNumberOfSamplesForProject(Project project) {
		return psjRepository.countSamplesForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#project, 'isProjectOwner') and hasPermission(#mergeInto, 'canUpdateSample') and hasPermission(#toMerge, 'canUpdateSample')")
	public Sample mergeSamples(Project project, Sample mergeInto, Collection<Sample> toMerge) {
		// confirm that all samples are part of the same project:
		confirmProjectSampleJoin(project, mergeInto);

		for (Sample s : toMerge) {
			confirmProjectSampleJoin(project, s);
			List<SampleSequencingObjectJoin> sequencesForSample = ssoRepository.getSequencesForSample(s);
			for (SampleSequencingObjectJoin join : sequencesForSample) {
				SequencingObject sequencingObject = join.getObject();
				ssoRepository.delete(join);
				addSequencingObjectToSample(mergeInto, sequencingObject);
			}

			// have to remove the sample to be deleted from its project:
			ProjectSampleJoin readSampleForProject = psjRepository.readSampleForProject(project, s);
			psjRepository.delete(readSampleForProject);
			sampleRepository.delete(s.getId());
		}
		return mergeInto;
	}

	/**
	 * Confirm that a {@link ProjectSampleJoin} exists between the given
	 * {@link Project} and {@link Sample}.
	 * 
	 * @param project
	 *            the {@link Project} to check
	 * @param sample
	 *            the {@link Sample} to check
	 * @throws IllegalArgumentException
	 *             if join does not exist
	 */
	private void confirmProjectSampleJoin(Project project, Sample sample) throws IllegalArgumentException{
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#id, 'canReadSample')")
	public Sample read(Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Page<ProjectSampleJoin> getSamplesForProjectWithName(Project project, String name, int page, int size,
			Direction order, String... sortProperties) {
		sortProperties = verifySortProperties(sortProperties);

		return psjRepository.findAll(ProjectSampleJoinSpecification.searchSampleWithNameInProject(name, project),
				new PageRequest(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public Long getTotalBasesForSample(Sample sample) throws SequenceFileAnalysisException {
		checkNotNull(sample, "sample is null");

		long totalBases = 0;

		List<SampleSequencingObjectJoin> sequencesForSample = ssoRepository.getSequencesForSample(sample);
		for (SampleSequencingObjectJoin join : sequencesForSample) {
			for (SequenceFile sequenceFile : join.getObject().getFiles()) {
				final AnalysisFastQC sequenceFileFastQC = analysisRepository
						.findFastqcAnalysisForSequenceFile(sequenceFile);
				if (sequenceFileFastQC == null || sequenceFileFastQC.getTotalBases() == null) {
					throw new SequenceFileAnalysisException("Missing FastQC analysis for SequenceFile ["
							+ sequenceFile.getId() + "]");
				}
				totalBases += sequenceFileFastQC.getTotalBases();
			}
		}

		return totalBases;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public Double estimateCoverageForSample(Sample sample, long referenceFileLength)
			throws SequenceFileAnalysisException {
		checkNotNull(sample, "sample is null");
		checkArgument(referenceFileLength > 0, "referenceFileLength (" + referenceFileLength + ") must be positive");

		return getTotalBasesForSample(sample) / (double) referenceFileLength;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public Double estimateCoverageForSample(Sample sample, ReferenceFile referenceFile)
			throws SequenceFileAnalysisException {
		checkNotNull(sample, "sample is null");
		checkNotNull(referenceFile, "referenceFile is null");

		return estimateCoverageForSample(sample, referenceFile.getFileLength());
	}

	/**
	 * Add a {@link SequencingObject} to a {@link Sample} after testing if it
	 * exists in a {@link Sample} already
	 * 
	 * @param sample
	 *            {@link Sample} to add to
	 * @param seqObject
	 *            {@link SequencingObject} to add
	 * @return a {@link SampleSequencingObjectJoin}
	 */
	@Transactional
	private SampleSequencingObjectJoin addSequencingObjectToSample(Sample sample, SequencingObject seqObject) {
		// call the relationship repository to create the relationship between
		// the two entities.
		if (ssoRepository.getSampleForSequencingObject(seqObject) != null) {
			throw new EntityExistsException("This sequencefile is already associated with a sample");
		}
		logger.trace("adding " + seqObject.getId() + " to sample " + sample.getId());
		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(sample, seqObject);
		return ssoRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#projects, 'canReadProject')")
	public Page<ProjectSampleJoin> getFilteredSamplesForProjects(List<Project> projects, List<String> sampleNames, String sampleName, String searchTerm,
			String organism, Date minDate, Date maxDate, int currentPage, int pageSize, Sort sort) {
		return psjRepository
				.findAll(ProjectSampleSpecification.getSamples(projects, sampleNames, sampleName, searchTerm, organism, minDate, maxDate),
						new PageRequest(currentPage, pageSize, sort));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	@PostFilter("hasPermission(filterObject, 'canReadSample')")
	public Collection<Sample> getSamplesForAnalysisSubimssion(AnalysisSubmission submission) {
		Set<SequencingObject> objectsForAnalysisSubmission = sequencingObjectRepository
				.findSequencingObjectsForAnalysisSubmission(submission);

		Set<Sample> samples = objectsForAnalysisSubmission.stream()
				.map(s -> ssoRepository.getSampleForSequencingObject(s).getSubject()).collect(Collectors.toSet());
		return samples;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<QCEntry> getQCEntriesForSample(Sample sample) {
		return qcEntryRepository.getQCEntriesForSample(sample);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#objects, 'canUpdateSample')")
	@Override
	public List<Sample> updateMultiple(Collection<Sample> objects) {
		return super.updateMultiple(objects);
	}
	
	/**
	 * Verify that the given sort properties array is not null or empty. If it
	 * is, give a default sort property.
	 * 
	 * @param sortProperties
	 *            The given sort properites
	 * @return The corrected sort properties
	 */
	private String[] verifySortProperties(String[] sortProperties) {
		// if the sort properties are null, empty, or are an empty string, use
		// CREATED_DATE
		if (sortProperties == null || sortProperties.length == 0
				|| (sortProperties.length == 1 && sortProperties[0].equals(""))) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}

		return sortProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@PreAuthorize("hasPermission(#sample, 'canUpdateSample')")
	@Override
	public void removeGenomeAssemblyFromSample(Sample sample, Long genomeAssemblyId) {
		SampleGenomeAssemblyJoin join = sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(sample.getId(),
				genomeAssemblyId);
		if (join != null) {
			logger.debug("Removing genome assembly [" + genomeAssemblyId + "] from sample [" + sample.getId() + "]");
			sampleGenomeAssemblyJoinRepository.delete(join.getObject().getId());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#sample, 'canReadSample')")
	@Override
	public Collection<SampleGenomeAssemblyJoin> getAssembliesForSample(Sample sample) {
		return sampleGenomeAssemblyJoinRepository.findBySample(sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#sample, 'canReadSample')")
	@Override
	public GenomeAssembly getGenomeAssemblyForSample(Sample sample, Long genomeAssemblyId) {
		SampleGenomeAssemblyJoin join = sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(sample.getId(),
				genomeAssemblyId);
		return join.getObject();
	}
}
