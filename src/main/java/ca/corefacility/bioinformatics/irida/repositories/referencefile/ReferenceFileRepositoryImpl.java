package ca.corefacility.bioinformatics.irida.repositories.referencefile;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;

/**
 * Custom implementation of {@link FilesystemSupplementedRepositoryImpl} for
 * {@link ReferenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Repository
public class ReferenceFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<ReferenceFile> {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceFileRepositoryImpl.class);

	@Autowired
	public ReferenceFileRepositoryImpl(EntityManager entityManager,
			@Qualifier("referenceFileBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferenceFile save(ReferenceFile entity) {
		Long referenceFileLength = getReferenceFileLength(entity);
		entity.setFileLength(referenceFileLength);
		return super.saveInternal(entity);
	}

	/**
	 * Calculate the length of a reference file in bases
	 * 
	 * @param entity
	 *            The reference file object to calculate
	 * @return The number of bases in the file
	 */
	private Long getReferenceFileLength(ReferenceFile entity) {
		Long totalLength = 0l;

		Path file = entity.getFile();
		logger.trace("Calculating reference length for file: " + file);
		try {
			LinkedHashMap<String, DNASequence> readFastaDNASequence = FastaReaderHelper.readFastaDNASequence(file
					.toFile());
			for (Entry<String, DNASequence> entry : readFastaDNASequence.entrySet()) {
				logger.trace("Calculating for sequence " + entry.getValue().getAccession());
				int length = entry.getValue().getLength();
				totalLength += length;
			}
		} catch (Exception e) {
			logger.error("Cannot calculate reference file length", e);
			totalLength = null;
		}
		return totalLength;
	}
}
