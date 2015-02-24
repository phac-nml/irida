package ca.corefacility.bioinformatics.irida.service.impl.analysis.submission;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;

/**
 * Implementation of an AnalysisSubmissionService.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class AnalysisSubmissionServiceImpl extends CRUDServiceImpl<Long, AnalysisSubmission> implements
		AnalysisSubmissionService {
	
	private UserRepository userRepository;
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	private final ReferenceFileRepository referenceFileRepository;

	/**
	 * Builds a new AnalysisSubmissionServiceImpl with the given information.
	 * 
	 * @param analysisSubmissionRepository
	 *            A repository for accessing analysis submissions.
	 * @param userRepository
	 *            A repository for accessing user information.
	 * @param validator
	 *            A validator.
	 */
	@Autowired
	public AnalysisSubmissionServiceImpl(AnalysisSubmissionRepository analysisSubmissionRepository,
			UserRepository userRepository, final ReferenceFileRepository referenceFileRepository, Validator validator) {
		super(analysisSubmissionRepository, validator, AnalysisSubmission.class);
		this.userRepository = userRepository;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.referenceFileRepository = referenceFileRepository;
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
		
		return super.create(analysisSubmission);
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
			String name) {
		final Collection<AnalysisSubmission> createdSubmissions = new HashSet<AnalysisSubmission>();
		// Single end reads
		IridaWorkflowDescription description = workflow.getWorkflowDescription();
		if (description.acceptsSingleSequenceFiles()) {
			for (int i = 0; i < sequenceFiles.size(); i++) {
				SequenceFile file = sequenceFiles.get(i);
				// Build the analysis submission
				AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
				builder.name(name + "_" + (i + 1));
				builder.inputFilesSingle(ImmutableSet.of(file));

				// Add reference file
				if (ref != null && description.requiresReference()) {
					// Note: This cannot be empty if through the UI if the
					// pipeline required a reference file.
					ReferenceFile referenceFile = referenceFileRepository.findOne(ref);
					builder.referenceFile(referenceFile);
				}

				if (params != null && description.acceptsParameters()) {
					// Note: This cannot be empty if through the UI if the
					// pipeline required params.
					builder.inputParameters(params);
				}

				// Create the submission
				createdSubmissions.add(create(builder.build()));
			}
		}

		// Paired end reads
		if (description.acceptsPairedSequenceFiles()) {
			for (int i = 0; i < sequenceFilePairs.size(); i++) {
				SequenceFilePair pair = sequenceFilePairs.get(i);
				// Build the analysis submission
				AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
				builder.name(name + "_" + (i + 1));
				builder.inputFilesPaired(ImmutableSet.of(pair));

				// Add reference file
				if (ref != null && description.requiresReference()) {
					ReferenceFile referenceFile = referenceFileRepository.findOne(ref);
					builder.referenceFile(referenceFile);
				}

				if (description.acceptsParameters()) {
					builder.inputParameters(params);
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
			String name) {
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
			builder.inputParameters(params);
		}

		// Create the submission
		return create(builder.build());
	}
}
