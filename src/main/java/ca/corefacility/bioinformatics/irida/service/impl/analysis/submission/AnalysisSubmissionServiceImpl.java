package ca.corefacility.bioinformatics.irida.service.impl.analysis.submission;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.TransientPropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;

/**
 * Implementation of an AnalysisSubmissionService.
 * 
 *
 */
@Service
public class AnalysisSubmissionServiceImpl extends CRUDServiceImpl<Long, AnalysisSubmission> implements
		AnalysisSubmissionService {
	
	/**
	 * A {@link Map} defining the progress transitions points for each state in
	 * an {@link AnalysisSubmission}.
	 */
	// @formatter:off
	private static final Map<AnalysisState,Float> STATE_PERCENTAGE = ImmutableMap.<AnalysisState,Float>builder().
			put(AnalysisState.NEW,              0.0f).
			put(AnalysisState.PREPARING,        0.0f).
			put(AnalysisState.PREPARED,         1.0f).
			put(AnalysisState.SUBMITTING,       5.0f).
			put(AnalysisState.RUNNING,          10.0f).
			put(AnalysisState.FINISHED_RUNNING, 90.0f).
			put(AnalysisState.COMPLETING,       95.0f).
			put(AnalysisState.COMPLETED,        100.0f).
			build();
	// @formatter:on
	
	private static final float RUNNING_PERCENT = STATE_PERCENTAGE.get(AnalysisState.RUNNING);
	private static final float FINISHED_RUNNING_PERCENT = STATE_PERCENTAGE.get(AnalysisState.FINISHED_RUNNING);
	
	private UserRepository userRepository;
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private final ReferenceFileRepository referenceFileRepository;
	private final SequenceFileService sequenceFileService;
	private final SequenceFilePairService sequenceFilePairService;
	private final GalaxyHistoriesService galaxyHistoriesService;

	/**
	 * Builds a new AnalysisSubmissionServiceImpl with the given information.
	 * 
	 * @param analysisSubmissionRepository
	 *            A repository for accessing analysis submissions.
	 * @param userRepository
	 *            A repository for accessing user information.
	 * @param referenceFileRepository
	 *            the reference file repository
	 * @param sequenceFileService
	 *            the sequence file service.
	 * @param sequenceFilePairService
	 *            the sequence file pair service
	 * @param galaxyHistoriesService
	 *            The {@link galaxyHistoriesService}.
	 * @param validator
	 *            A validator.
	 */
	@Autowired
	public AnalysisSubmissionServiceImpl(AnalysisSubmissionRepository analysisSubmissionRepository,
			UserRepository userRepository, final ReferenceFileRepository referenceFileRepository,
			final SequenceFileService sequenceFileService, final SequenceFilePairService sequenceFilePairService,
			final GalaxyHistoriesService galaxyHistoriesService, Validator validator) {
		super(analysisSubmissionRepository, validator, AnalysisSubmission.class);
		this.userRepository = userRepository;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.referenceFileRepository = referenceFileRepository;
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.galaxyHistoriesService = galaxyHistoriesService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisState getStateForAnalysisSubmission(Long analysisSubmissionId) throws EntityNotFoundException {
		checkNotNull(analysisSubmissionId, "analysisSubmissionId is null");

		AnalysisSubmission submission = this.read(analysisSubmissionId);

		return submission.getAnalysisState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisSubmission read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<AnalysisSubmission> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<AnalysisSubmission> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean exists(Long id) {
		return super.exists(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Revisions<Integer, AnalysisSubmission> findRevisions(Long id) throws EntityRevisionDeletedException {
		return super.findRevisions(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<Revision<Integer, AnalysisSubmission>> findRevisions(Long id, Pageable pageable)
			throws EntityRevisionDeletedException {
		return super.findRevisions(id, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<AnalysisSubmission> list(int page, int size, Direction order, String... sortProperties)
			throws IllegalArgumentException {
		return super.list(page, size, order, sortProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<AnalysisSubmission> list(int page, int size, Direction order) {
		return super.list(page, size, order);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long count() {
		return super.count();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Long id) throws EntityNotFoundException {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisSubmission update(Long id, Map<String, Object> updatedFields) throws ConstraintViolationException,
			EntityExistsException, InvalidPropertyException {
		return super.update(id, updatedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnalysisSubmission create(AnalysisSubmission analysisSubmission) throws ConstraintViolationException,
			EntityExistsException {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.loadUserByUsername(userDetails.getUsername());
		analysisSubmission.setSubmitter(user);
		
		try {
			return super.create(analysisSubmission);
		} catch (final InvalidDataAccessApiUsageException e) {
			// if the exception is because we're using unsaved properties, try to wrap the exception with a sane-er message.
			if (e.getCause() != null) {
				final Throwable primaryCause = e.getCause();
				if (primaryCause.getCause() != null
						&& primaryCause.getCause() instanceof TransientPropertyValueException) {
					final TransientPropertyValueException propertyException = (TransientPropertyValueException) primaryCause
							.getCause();
					if (Objects.equals("namedParameters", propertyException.getPropertyName())) {
						throw new UnsupportedOperationException(
								"You must save the named properties *before* you use them in a submission.", e);
					}
				}
			}

			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<AnalysisSubmission> search(Specification<AnalysisSubmission> specification, int page, int size,
			Direction order, String... sortProperties) {
		return super.search(specification, page, size, order, sortProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AnalysisSubmission> getAnalysisSubmissionsForUser(User user) {
		checkNotNull(user, "user is null");

		return analysisSubmissionRepository.findBySubmitter(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AnalysisSubmission> getAnalysisSubmissionsForCurrentUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepository.loadUserByUsername(userDetails.getUsername());
		return getAnalysisSubmissionsForUser(user);
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Collection<AnalysisSubmission> createSingleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs, Map<String, String> params,
			IridaWorkflowNamedParameters namedParameters, String name) {
		final Collection<AnalysisSubmission> createdSubmissions = new HashSet<AnalysisSubmission>();
		// Single end reads
		IridaWorkflowDescription description = workflow.getWorkflowDescription();
		if (description.acceptsSingleSequenceFiles()) {
			final Map<Sample, SequenceFile> samplesMap = sequenceFileService.getUniqueSamplesForSequenceFiles(Sets
					.newHashSet(sequenceFiles));
			for (final Sample s : samplesMap.keySet()) {
				// Build the analysis submission
				AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
				builder.name(name + "_" + s.getSampleName());
				builder.inputFilesSingle(ImmutableSet.of(samplesMap.get(s)));

				// Add reference file
				if (ref != null && description.requiresReference()) {
					// Note: This cannot be empty if through the UI if the
					// pipeline required a reference file.
					ReferenceFile referenceFile = referenceFileRepository.findOne(ref);
					builder.referenceFile(referenceFile);
				}

				if (description.acceptsParameters()) {
					if (namedParameters != null) {
						builder.withNamedParameters(namedParameters);
					} else {
						if (!params.isEmpty()) {
							// Note: This cannot be empty if through the UI if
							// the pipeline required params.
							builder.inputParameters(params);
						}
					}
				}

				// Create the submission
				createdSubmissions.add(create(builder.build()));
			}
		}

		// Paired end reads
		if (description.acceptsPairedSequenceFiles()) {
			final Map<Sample, SequenceFilePair> samplesMap = sequenceFilePairService
					.getUniqueSamplesForSequenceFilePairs(Sets.newHashSet(sequenceFilePairs));
			for (final Sample s : samplesMap.keySet()) {
				// Build the analysis submission
				AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
				builder.name(name + "_" + s.getSampleName());
				builder.inputFilesPaired(ImmutableSet.of(samplesMap.get(s)));

				// Add reference file
				if (ref != null && description.requiresReference()) {
					ReferenceFile referenceFile = referenceFileRepository.findOne(ref);
					builder.referenceFile(referenceFile);
				}

				if (description.acceptsParameters()) {
					if (namedParameters != null) {
						builder.withNamedParameters(namedParameters);
					} else {
						if (!params.isEmpty()) {
							// Note: This cannot be empty if through the UI if
							// the pipeline required params.
							builder.inputParameters(params);
						}
					}
				}

				// Create the submission
				createdSubmissions.add(create(builder.build()));
			}
		}
		
		return createdSubmissions;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public AnalysisSubmission createMultipleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs, Map<String, String> params,
			IridaWorkflowNamedParameters namedParameters, String name) {
		AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
		builder.name(name);
		IridaWorkflowDescription description = workflow.getWorkflowDescription();

		// Add reference file
		if (ref != null && description.requiresReference()) {
			ReferenceFile referenceFile = referenceFileRepository.findOne(ref);
			builder.referenceFile(referenceFile);
		}

		// Add any single end sequencing files.
		if (!sequenceFiles.isEmpty() && description.acceptsSingleSequenceFiles()) {
			builder.inputFilesSingle(Sets.newHashSet(sequenceFiles));
		}

		// Add any paired end sequencing files.
		if (!sequenceFilePairs.isEmpty() && description.acceptsPairedSequenceFiles()) {
			builder.inputFilesPaired(Sets.newHashSet(sequenceFilePairs));
		}

		if (description.acceptsParameters()) {
			if (namedParameters != null) {
				builder.withNamedParameters(namedParameters);
			} else {
				if (!params.isEmpty()) {
					// Note: This cannot be empty if through the UI if
					// the pipeline required params.
					builder.inputParameters(params);
				}
			}
		}

		// Create the submission
		return create(builder.build());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getPercentCompleteForAnalysisSubmission(Long id) throws EntityNotFoundException,
			ExecutionManagerException, NoPercentageCompleteException {
		AnalysisSubmission analysisSubmission = read(id);
		AnalysisState analysisState = analysisSubmission.getAnalysisState();

		switch (analysisState) {
		case NEW:
		case PREPARING:
		case PREPARED:
		case SUBMITTING:
			return STATE_PERCENTAGE.get(analysisState);

			/**
			 * If the analysis is in a state of {@link AnalysisState.RUNNING}
			 * then we are able to ask Galaxy for the proportion of jobs that
			 * are complete. We can scale this value between RUNNING_PERCENT
			 * (10%) and FINISHED_RUNNING_PERCENT (90%) so that after all jobs
			 * are complete we are only at 90%. The remaining 10% involves
			 * transferring files back to Galaxy.
			 * 
			 * For example, if there are 10 out of 20 jobs finished on Galaxy,
			 * then the proportion of jobs complete is 10/20 = 0.5. So, the
			 * percent complete for the overall analysis is: percentComplete =
			 * 10 + (90 - 10) * 0.5 = 50%.
			 * 
			 * If there are 20 out of 20 jobs finished in Galaxy, then the
			 * percent complete is: percentComplete = 10 + (90 - 10) * 1.0 =
			 * 90%.
			 */
		case RUNNING:
			String workflowHistoryId = analysisSubmission.getRemoteAnalysisId();
			GalaxyWorkflowStatus workflowStatus = galaxyHistoriesService.getStatusForHistory(workflowHistoryId);
			return RUNNING_PERCENT + (FINISHED_RUNNING_PERCENT - RUNNING_PERCENT)
					* workflowStatus.getProportionComplete();
			
		case FINISHED_RUNNING:
		case COMPLETING:
		case COMPLETED:
			return STATE_PERCENTAGE.get(analysisState);
		default:
			throw new NoPercentageCompleteException("No valid percent complete for state " + analysisState);
		}
	}

	@Override
	public AnalysisSubmission cleanupSubmission(Long id) throws EntityNotFoundException, ExecutionManagerException {
		AnalysisSubmission analysisSubmission = read(id);
		AnalysisCleanedState analysisCleanedState = analysisSubmission.getAnalysisCleanedState();
		checkState(AnalysisCleanedState.CLEANING.equals(analysisCleanedState), "Cannot clean while in state " + analysisCleanedState);
		
		return analysisSubmission;
	}
}
