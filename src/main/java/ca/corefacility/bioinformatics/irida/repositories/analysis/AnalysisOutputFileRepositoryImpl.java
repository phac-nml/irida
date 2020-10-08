package ca.corefacility.bioinformatics.irida.repositories.analysis;

import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;

/**
 * Custom implementation of {@link FilesystemSupplementedRepositoryImpl} for
 * {@link AnalysisOutputFile}.
 * 
 *
 */
@Repository
public class AnalysisOutputFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<AnalysisOutputFile> {

	@Autowired
	public AnalysisOutputFileRepositoryImpl(EntityManager entityManager,
			@Qualifier("outputFileBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public AnalysisOutputFile save(AnalysisOutputFile entity) {
		return super.saveInternal(entity);
	}

}
