package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for storing and retrieving {@link AnalysisSubmissionTemplate}s
 */
public interface AnalysisSubmissionTemplateRepository extends IridaJpaRepository<AnalysisSubmissionTemplate, Long> {
	/**
	 * Get all the {@link AnalysisSubmissionTemplate}s for a given {@link Project}
	 *
	 * @param project the project to get templates for
	 * @return a list of {@link AnalysisSubmissionTemplate}
	 */
	@Query("FROM AnalysisSubmissionTemplate a WHERE a.submittedProject=?1")
	public List<AnalysisSubmissionTemplate> getAnalysisSubmissionTemplatesForProject(Project project);

	/**
	 * Get only the enabled {@link AnalysisSubmissionTemplate}s for a given {@link Project}
	 *
	 * @param project the project to get templates for
	 * @return a list of {@link AnalysisSubmissionTemplate}
	 */
	@Query("FROM AnalysisSubmissionTemplate a WHERE a.submittedProject=?1 AND a.enabled=1")
	public List<AnalysisSubmissionTemplate> getEnabledAnalysisSubmissionTemplatesForProject(Project project);
}
