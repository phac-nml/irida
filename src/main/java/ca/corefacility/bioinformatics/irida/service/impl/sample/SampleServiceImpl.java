package ca.corefacility.bioinformatics.irida.service.impl.sample;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.criteria.*;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.enums.StatisticTimePeriod;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceConcatenation;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceConcatenationRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.SearchCriteria;
import ca.corefacility.bioinformatics.irida.repositories.specification.SearchOperation;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics.GenericStatModel;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Service class for managing {@link Sample}.
 */
@Service
public class SampleServiceImpl extends CRUDServiceImpl<Long, Sample> implements SampleService {

	private static final Logger logger = LoggerFactory.getLogger(SampleServiceImpl.class);

	/**
	 * Reference to {@link SampleRepository} for managing {@link Sample}.
	 */
	private SampleRepository sampleRepository;
	/**
	 * Reference to {@link ProjectSampleJoinRepository} for managing {@link ProjectSampleJoin}.
	 */
	private ProjectSampleJoinRepository psjRepository;

	private SampleSequencingObjectJoinRepository ssoRepository;

	private QCEntryRepository qcEntryRepository;

	private SequencingObjectRepository sequencingObjectRepository;
	private SequenceConcatenationRepository concatenationRepository;

	/**
	 * Reference to {@link AnalysisRepository}.
	 */
	private final AnalysisRepository analysisRepository;

	private final SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;

	private final UserRepository userRepository;

	private final MetadataEntryRepository metadataEntryRepository;
	private final SequenceFileRepository sequenceFileRepository;
	private final AnalysisSubmissionRepository submissionRepository;

	/**
	 * Constructor.
	 *
	 * @param sampleRepository                   the sample repository.
	 * @param psjRepository                      the project sample join repository.
	 * @param analysisRepository                 the analysis repository.
	 * @param ssoRepository                      The {@link SampleSequencingObjectJoin} repository
	 * @param sequencingObjectRepository         the {@link SequencingObject} repository
	 * @param concatenationRepository            the {@link SequenceConcatenationRepository} repository
	 * @param qcEntryRepository                  a repository for storing and reading {@link QCEntry}
	 * @param sampleGenomeAssemblyJoinRepository A {@link SampleGenomeAssemblyJoinRepository}
	 * @param userRepository                     A {@link UserRepository}
	 * @param metadataEntryRepository            A {@link MetadataEntryRepository}
	 * @param sequenceFileRepository             A {@link SequenceFileRepository}
	 * @param submissionRepository               A {@link AnalysisSubmissionRepository}
	 * @param validator                          validator.
	 */
	@Autowired
	public SampleServiceImpl(SampleRepository sampleRepository, ProjectSampleJoinRepository psjRepository,
			final AnalysisRepository analysisRepository, SampleSequencingObjectJoinRepository ssoRepository,
			QCEntryRepository qcEntryRepository, SequencingObjectRepository sequencingObjectRepository,
			SequenceConcatenationRepository concatenationRepository,
			SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository, UserRepository userRepository,
			MetadataEntryRepository metadataEntryRepository, SequenceFileRepository sequenceFileRepository,
			AnalysisSubmissionRepository submissionRepository, Validator validator) {
		super(sampleRepository, validator, Sample.class);
		this.sampleRepository = sampleRepository;
		this.psjRepository = psjRepository;
		this.analysisRepository = analysisRepository;
		this.ssoRepository = ssoRepository;
		this.qcEntryRepository = qcEntryRepository;
		this.sequencingObjectRepository = sequencingObjectRepository;
		this.concatenationRepository = concatenationRepository;
		this.userRepository = userRepository;
		this.sampleGenomeAssemblyJoinRepository = sampleGenomeAssemblyJoinRepository;
		this.metadataEntryRepository = metadataEntryRepository;
		this.sequenceFileRepository = sequenceFileRepository;
		this.submissionRepository = submissionRepository;
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
	public Sample updateFields(Long id, Map<String, Object> updatedFields)
			throws ConstraintViolationException, ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException,
			InvalidPropertyException {
		return super.updateFields(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#object, 'canUpdateSample')")
	@Override
	public Sample update(Sample object) {
		object.setModifiedDate(new Date());
		return super.update(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#sample, 'canReadSample')")
	@PostFilter("hasPermission(filterObject, 'canReadMetadataEntry')")
	@Override
	public Set<MetadataEntry> getMetadataForSample(Sample sample) {
		return metadataEntryRepository.getMetadataForSample(sample);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@Override
	public List<Long> getLockedSamplesInProject(Project project) {
		return psjRepository.getLockedSamplesForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#project, 'canReadProject')")
	@PostAuthorize("hasPermission(returnObject,'readProjectMetadataResponse')")
	public ProjectMetadataResponse getMetadataForProjectSamples(Project project, List<Long> sampleIds,
			List<MetadataTemplateField> fields) {
		checkArgument(!fields.isEmpty(), "fields must not be empty");
		Map<Long, Set<MetadataEntry>> metadataForProjectSamples = metadataEntryRepository.getMetadataForProjectSamples(
				project, sampleIds, fields);

		return new ProjectMetadataResponse(project, metadataForProjectSamples);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#s, 'canUpdateSample')")
	@Transactional
	public Sample updateSampleMetadata(Sample s, Set<MetadataEntry> metadataToSet) {
		Set<MetadataEntry> currentMetadata = getMetadataForSample(s);

		metadataEntryRepository.deleteAll(currentMetadata);

		for (MetadataEntry e : metadataToSet) {
			e.setSample(s);
		}

		metadataEntryRepository.saveAll(metadataToSet);

		s = read(s.getId());
		s.setModifiedDate(new Date());
		// re-saving sample to update modified date
		s = sampleRepository.save(s);

		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#s, 'canUpdateSample')")
	@Transactional
	public Sample mergeSampleMetadata(Sample s, Set<MetadataEntry> metadataToAdd) {
		Set<MetadataEntry> currentMetadata = getMetadataForSample(s);

		// loop through entry set and see if it already exists
		for (MetadataEntry newMetadataEntry : metadataToAdd) {
			MetadataTemplateField field = newMetadataEntry.getField();
			newMetadataEntry.setSample(s);

			Optional<MetadataEntry> metadataEntryForField = currentMetadata.stream()
					.filter(e -> e.getField().equals(field))
					.findFirst();

			if (metadataEntryForField.isPresent()) {
				MetadataEntry originalMetadataEntry = metadataEntryForField.get();

				// if the metadata entries are of the same type, I can directly merge
				if (originalMetadataEntry.getClass().equals(newMetadataEntry.getClass())) {
					originalMetadataEntry.merge(newMetadataEntry);
				} else {
					// if they are different types, I need to replace the
					// metadata entry instead of merging
					currentMetadata.remove(originalMetadataEntry);
					metadataEntryRepository.delete(originalMetadataEntry);

					currentMetadata.add(newMetadataEntry);
				}
			} else {
				currentMetadata.add(newMetadataEntry);
			}
		}

		metadataEntryRepository.saveAll(currentMetadata);

		s = read(s.getId());
		s.setModifiedDate(new Date());
		// re-saving sample to update modified date
		s = sampleRepository.save(s);

		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(
			"hasAnyRole('ROLE_ADMIN','ROLE_SEQUENCER') or (hasPermission(#project, 'canReadProject') and hasPermission(#sampleId, 'canReadSample'))")
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
	public Sample getSampleBySampleName(Project project, String sampleName) {
		Sample s = sampleRepository.getSampleBySampleName(project, sampleName);
		if (s != null) {
			return s;
		} else {
			throw new EntityNotFoundException(
					"No sample with name [" + sampleName + "] in project [" + project.getId() + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SEQUENCER') or hasPermission(#projectIds, 'canReadProject')")
	public Map<String, List<Long>> getSampleIdsBySampleNameForProjects(List<Long> projectIds,
			List<String> sampleNames) {
		return sampleRepository.getSampleIdsBySampleNameInProjects(projectIds, sampleNames)
				.stream()
				.collect(Collectors.groupingBy(sampleNameIDTuple -> (String) sampleNameIDTuple.get(0),
						Collectors.mapping(sampleNameIDTuple -> (Long) sampleNameIDTuple.get(1), Collectors.toList())));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasPermission(#sample, 'canUpdateSample')")
	public void removeSequencingObjectFromSample(Sample sample, SequencingObject object) {
		ssoRepository.delete(ssoRepository.readObjectForSample(sample, object.getId()));
		object = sequencingObjectRepository.findSequencingObjectById(object.getId());
		if (sample.getDefaultSequencingObject() != null && sample.getDefaultSequencingObject()
				.getId()
				.equals(object.getId())) {
			sampleRepository.removeDefaultSequencingObject(sample);
		}
		Set<AnalysisSubmission> submissions = submissionRepository.findAnalysisSubmissionsForSequencingObject(object);
		if (submissions.isEmpty() && object.getSequencingRun() == null) {
			Set<SequenceConcatenation> concatenations = concatenationRepository.findConcatenatedSequencingObjectSources(
					object);
			for (SequenceConcatenation concat : concatenations) {
				concat.removeSource(object);
				concatenationRepository.save(concat);
			}
			SequenceConcatenation concatenated = concatenationRepository.findConcatenatedSequencingObject(object);
			if (concatenated != null) {
				concatenationRepository.delete(concatenated);
			}
			for (SequenceFile file : object.getFiles()) {
				sequenceFileRepository.delete(file);
			}
			sequencingObjectRepository.delete(object);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<Join<Project, Sample>> getSamplesForProject(Project project) {
		return psjRepository.getSamplesForProject(project);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	@Override
	public List<Sample> getSamplesForProjectShallow(Project project) {
		List<Sample> samplesForProjectShallow = sampleRepository.getSamplesForProjectShallow(project);
		return samplesForProjectShallow;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<Sample> getSamplesInProject(Project project, List<Long> sampleIds) {
		return psjRepository.getSamplesInProject(project, sampleIds);
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
	@PreAuthorize(
			"hasPermission(#project, 'isProjectOwner') and hasPermission(#mergeInto, 'canUpdateSample') and hasPermission(#toMerge, 'canUpdateSample')")
	public Sample mergeSamples(Project project, Sample mergeInto, Collection<Sample> toMerge) {
		// confirm that all samples are part of the same project:
		confirmProjectSampleJoin(project, mergeInto);

		logger.debug(
				"Merging samples " + toMerge.stream().map(Sample::getId).collect(Collectors.toList()) + " into sample ["
						+ mergeInto.getId() + "]");

		for (Sample s : toMerge) {
			confirmProjectSampleJoin(project, s);
			List<SampleSequencingObjectJoin> sequencesForSample = ssoRepository.getSequencesForSample(s);
			for (SampleSequencingObjectJoin join : sequencesForSample) {
				SequencingObject sequencingObject = join.getObject();
				ssoRepository.delete(join);
				addSequencingObjectToSample(mergeInto, sequencingObject);
			}

			Collection<SampleGenomeAssemblyJoin> genomeAssemblyJoins = sampleGenomeAssemblyJoinRepository.findBySample(
					s);
			for (SampleGenomeAssemblyJoin join : genomeAssemblyJoins) {
				GenomeAssembly genomeAssembly = join.getObject();

				logger.trace(
						"Removing genome assembly [" + genomeAssembly.getId() + "] from sample [" + s.getId() + "]");
				sampleGenomeAssemblyJoinRepository.delete(join);

				logger.trace("Adding genome assembly [" + genomeAssembly.getId() + "] to sample [" + mergeInto.getId()
						+ "]");
				SampleGenomeAssemblyJoin newJoin = new SampleGenomeAssemblyJoin(mergeInto, genomeAssembly);
				sampleGenomeAssemblyJoinRepository.save(newJoin);
			}

			// have to remove the sample to be deleted from its project:
			ProjectSampleJoin readSampleForProject = psjRepository.readSampleForProject(project, s);
			psjRepository.delete(readSampleForProject);
			sampleRepository.deleteById(s.getId());
		}
		mergeInto.setModifiedDate(new Date());
		sampleRepository.save(mergeInto);
		return mergeInto;
	}

	/**
	 * Confirm that a {@link ProjectSampleJoin} exists between the given {@link Project} and {@link Sample}.
	 *
	 * @param project the {@link Project} to check
	 * @param sample  the {@link Sample} to check
	 * @throws IllegalArgumentException if join does not exist
	 */
	private void confirmProjectSampleJoin(Project project, Sample sample) throws IllegalArgumentException {
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

		ProjectSampleJoinSpecification psjSampleWithNameInProject = new ProjectSampleJoinSpecification();

		psjSampleWithNameInProject.add(new SearchCriteria("project", project, SearchOperation.EQUAL));
		psjSampleWithNameInProject.add(new SearchCriteria("sample.sampleName", name, SearchOperation.MATCH));

		return psjRepository.findAll(psjSampleWithNameInProject, PageRequest.of(page, size, order, sortProperties));
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
				final AnalysisFastQC sequenceFileFastQC = analysisRepository.findFastqcAnalysisForSequenceFile(
						sequenceFile);
				if (sequenceFileFastQC == null || sequenceFileFastQC.getTotalBases() == null) {
					throw new SequenceFileAnalysisException(
							"Missing FastQC analysis for SequenceFile [" + sequenceFile.getId() + "]");
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
	 * Add a {@link SequencingObject} to a {@link Sample} after testing if it exists in a {@link Sample} already
	 *
	 * @param sample    {@link Sample} to add to
	 * @param seqObject {@link SequencingObject} to add
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
	public Page<ProjectSampleJoin> getFilteredProjectSamples(List<Project> projects,
			ProjectSampleJoinSpecification filterSpec, int currentPage, int pageSize, Sort sort) {
		filterSpec.add(new SearchCriteria("project", projects, SearchOperation.IN));

		return psjRepository.findAll(filterSpec, PageRequest.of(currentPage, pageSize, sort));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasAnyRole('ROLE_ADMIN') or hasPermission(#projects, 'canReadProject')")
	public Page<ProjectSampleJoin> getFilteredSamplesForProjects(List<Project> projects, List<String> sampleNames,
			String sampleName, String searchTerm, String organism, Date minDate, Date maxDate, int currentPage,
			int pageSize, Sort sort) {

		ProjectSampleJoinSpecification psjFilteredSamplesForProjects = new ProjectSampleJoinSpecification();

		psjFilteredSamplesForProjects.add(new SearchCriteria("project", projects, SearchOperation.IN));

		// Check to see if the sampleNames are in the samples
		if (sampleNames.size() > 0) {
			psjFilteredSamplesForProjects.add(new SearchCriteria("sample.sampleName", sampleNames, SearchOperation.IN));
		}

		// Check to see if there is a specific sample name
		if (!Strings.isNullOrEmpty(sampleName)) {
			psjFilteredSamplesForProjects.add(
					new SearchCriteria("sample.sampleName", sampleName, SearchOperation.MATCH));
		}

		// Check for the table search.
		// This can be expanded in future to search any attribute on the sample (e.g. description)
		// Underscores within the search term are escaped as the underscores were being treated the same
		// as hyphens.
		if (!Strings.isNullOrEmpty(searchTerm)) {
			psjFilteredSamplesForProjects.add(
					new SearchCriteria("sample.sampleName", searchTerm.replace("_", "\\_"), SearchOperation.MATCH));
		}

		// Check for organism
		if (!Strings.isNullOrEmpty(organism)) {
			psjFilteredSamplesForProjects.add(new SearchCriteria("sample.organism", organism, SearchOperation.MATCH));
		}

		// Check if there is a minimum search date
		if (minDate != null) {
			psjFilteredSamplesForProjects.add(
					new SearchCriteria("sample.modifiedDate", minDate, SearchOperation.GREATER_THAN_EQUAL));
		}

		// Check if there is a maximum search date
		if (maxDate != null) {
			psjFilteredSamplesForProjects.add(
					new SearchCriteria("sample.modifiedDate", maxDate, SearchOperation.LESS_THAN_EQUAL));
		}

		return psjRepository.findAll(psjFilteredSamplesForProjects, PageRequest.of(currentPage, pageSize, sort));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#submission, 'canReadAnalysisSubmission')")
	@PostFilter("hasPermission(filterObject, 'canReadSample')")
	public Collection<Sample> getSamplesForAnalysisSubmission(AnalysisSubmission submission) {
		Set<SequencingObject> objectsForAnalysisSubmission = sequencingObjectRepository.findSequencingObjectsForAnalysisSubmission(
				submission);
		Set<Sample> samples = null;
		try {
			samples = objectsForAnalysisSubmission.stream()
					.map(s -> ssoRepository.getSampleForSequencingObject(s).getSubject())
					.collect(Collectors.toSet());
		} catch (NullPointerException e) {
			logger.warn("No samples were found for submission " + submission.getId());
		}
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
	@Transactional(readOnly = true)
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#samples, 'canReadSample')")
	public Map<Long, List<QCEntry>> getQCEntriesForSamples(List<Sample> samples) {
		return qcEntryRepository.getQCEntriesForSamples(samples)
				.stream()
				.collect(Collectors.groupingBy(sampleQCEntryTuple -> (Long) sampleQCEntryTuple.get(0),
						Collectors.mapping(sampleQCEntryTuple -> (QCEntry) sampleQCEntryTuple.get(1),
								Collectors.toList())));
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
	 * {@inheritDoc}
	 */
	@PreAuthorize("permitAll()")
	@Override
	public Page<ProjectSampleJoin> searchSamplesForUser(String query, final Integer page, final Integer count,
			final Sort sort) {
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		final User loggedIn = userRepository.loadUserByUsername(username);

		final PageRequest pr = PageRequest.of(page, count, sort);

		return psjRepository.findAll(sampleForUserSpecification(loggedIn, query), pr);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Override
	public Page<ProjectSampleJoin> searchAllSamples(String query, final Integer page, final Integer count,
			final Sort sort) {
		final PageRequest pr = PageRequest.of(page, count, sort);

		return psjRepository.findAll(sampleForUserSpecification(null, query), pr);
	}

	/**
	 * Verify that the given sort properties array is not null or empty. If it is, give a default sort property.
	 *
	 * @param sortProperties The given sort properites
	 * @return The corrected sort properties
	 */
	private String[] verifySortProperties(String[] sortProperties) {
		// if the sort properties are null, empty, or are an empty string, use
		// CREATED_DATE
		if (sortProperties == null || sortProperties.length == 0 || (sortProperties.length == 1
				&& sortProperties[0].equals(""))) {
			sortProperties = new String[] { CREATED_DATE_SORT_PROPERTY };
		}

		return sortProperties;
	}

	/**
	 * Specification for searching {@link Sample}s
	 *
	 * @param user        the {@link User} to get samples for. If this property is null, will serch for all users.
	 * @param queryString the query string to search for
	 * @return a {@link Specification} for {@link ProjectSampleJoin}
	 */
	private static Specification<ProjectSampleJoin> sampleForUserSpecification(final User user,
			final String queryString) {
		return new Specification<ProjectSampleJoin>() {

			@Override
			public Predicate toPredicate(Root<ProjectSampleJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate search;
				if (user != null) {
					Predicate sampleAccess = cb.or(individualProjectMembership(root, query, cb),
							groupProjectMembership(root, query, cb));
					search = cb.and(sampleAccess, sampleProperties(root, query, cb));
				} else {
					search = sampleProperties(root, query, cb);
				}

				return search;
			}

			/**
			 * Search with the given query for sample properties
			 *
			 * @param root  root for ProjectSampleJoin
			 * @param query criteria query
			 * @param cb    criteria query builder
			 * @return a predicate
			 */
			private Predicate sampleProperties(final Root<ProjectSampleJoin> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {

				return cb.or(cb.like(root.get("sample").get("sampleName"), "%" + queryString + "%"),
						cb.equal(root.get("sample").get("id").as(String.class), queryString));
			}

			/**
			 * This {@link Predicate} filters out {@link Project}s for the specific user where they are assigned
			 * individually as a member.
			 *
			 * @param root  the root of the query
			 * @param query the query
			 * @param cb    the builder
			 * @return a {@link Predicate} that filters {@link Project}s where users are individually assigned.
			 */
			private Predicate individualProjectMembership(final Root<ProjectSampleJoin> root,
					final CriteriaQuery<?> query, final CriteriaBuilder cb) {
				final Subquery<Long> userMemberSelect = query.subquery(Long.class);
				final Root<ProjectUserJoin> userMemberJoin = userMemberSelect.from(ProjectUserJoin.class);
				userMemberSelect.select(userMemberJoin.get("project").get("id"))
						.where(cb.equal(userMemberJoin.get("user"), user));
				return cb.in(root.get("project")).value(userMemberSelect);
			}

			/**
			 * This {@link Predicate} filters out {@link Project}s for the specific user where they are assigned
			 * transitively through a {@link UserGroup}.
			 *
			 * @param root  the root of the query
			 * @param query the query
			 * @param cb    the builder
			 * @return a {@link Predicate} that filters {@link Project}s where users are assigned transitively through
			 *         {@link UserGroup} .
			 */
			private Predicate groupProjectMembership(final Root<ProjectSampleJoin> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				final Subquery<Long> groupMemberSelect = query.subquery(Long.class);
				final Root<UserGroupProjectJoin> groupMemberJoin = groupMemberSelect.from(UserGroupProjectJoin.class);
				groupMemberSelect.select(groupMemberJoin.get("project").get("id"))
						.where(cb.equal(groupMemberJoin.join("userGroup").join("users").get("user"), user));
				return cb.in(root.get("project")).value(groupMemberSelect);
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Long getSamplesCreated(Date createdDate) {
		Long samplesCount = sampleRepository.countSamplesCreatedInTimePeriod(createdDate);
		return samplesCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<GenericStatModel> getSamplesCreatedGrouped(Date createdDate, StatisticTimePeriod statisticTimePeriod) {
		return sampleRepository.countSamplesCreatedGrouped(createdDate, statisticTimePeriod.getGroupByFormat());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Map<Long, Long> getCoverageForSamplesInProject(Project project, List<Long> sampleIds) {
		return psjRepository.calculateCoverageForSamplesInProject(project, sampleIds)
				.stream()
				.collect(HashMap::new, (sampleCoverageMap, sampleCoverageTuple) -> sampleCoverageMap.put(
						(Long) sampleCoverageTuple.get(0), (Long) sampleCoverageTuple.get(1)), Map::putAll);
	}
}
