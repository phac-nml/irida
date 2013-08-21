package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation for managing {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImpl extends CRUDServiceImpl<Long, SequenceFile> implements SequenceFileService {

	/**
	 * A reference to the file system repository.
	 */
	private CRUDRepository<Long, SequenceFile> fileRepository;
	/**
	 * A reference to the data store repository.
	 */
	private SequenceFileRepository sequenceFileRepository;
	/**
	 * A reference to the chain of file processors.
	 */
	private FileProcessingChain fileProcessingChain;

	/**
	 * Constructor.
	 * 
	 * @param sequenceFileRepository
	 *            the sequence file repository.
	 * @param validator
	 *            validator.
	 */
	public SequenceFileServiceImpl(SequenceFileRepository sequenceFileRepository,
			CRUDRepository<Long, SequenceFile> fileRepository, Validator validator,
			FileProcessingChain fileProcessingChain) {
		super(sequenceFileRepository, validator, SequenceFile.class);
		this.sequenceFileRepository = sequenceFileRepository;
		this.fileRepository = fileRepository;
		this.fileProcessingChain = fileProcessingChain;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_USER')")
	public SequenceFile create(SequenceFile sequenceFile) {
		// Send the file to the database repository to be stored (in super)
		sequenceFile = super.create(sequenceFile);
		// Then store the file in an appropriate directory
		sequenceFile = fileRepository.create(sequenceFile);
		// And finally, update the database with the stored file location

		Map<String, Object> changed = new HashMap<>();
		changed.put("file", sequenceFile.getFile());
		sequenceFile = super.update(sequenceFile.getId(), changed);

		fileProcessingChain.launchChain(sequenceFile);

		return sequenceFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadSequenceFile')")
	public SequenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException {
		SequenceFile updated = super.update(id, updatedFields);

		if (updatedFields.containsKey("file")) {
			updated = fileRepository.update(id, updatedFields);
			updated = super.update(id, ImmutableMap.of("file", (Object) updated.getFile()));
			fileProcessingChain.launchChain(updated);
		}

		return updated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT') or hasPermission(#sample, 'canReadSample')")
	public SampleSequenceFileJoin createSequenceFileInSample(SequenceFile sequenceFile, Sample sample) {
		SequenceFile created = create(sequenceFile);
		SampleSequenceFileJoin addFileToSample = sequenceFileRepository.addFileToSample(sample, created);

		return addFileToSample;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public List<Join<Sample, SequenceFile>> getSequenceFilesForSample(Sample sample) {
		List<SampleSequenceFileJoin> joins = sequenceFileRepository.getFilesForSample(sample);
		return new ArrayList<Join<Sample, SequenceFile>>(joins);
	}
}
