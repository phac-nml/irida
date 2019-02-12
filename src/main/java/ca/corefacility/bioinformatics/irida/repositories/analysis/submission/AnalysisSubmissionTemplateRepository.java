package ca.corefacility.bioinformatics.irida.repositories.analysis.submission;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnalysisSubmissionTemplateRepository extends IridaJpaRepository<AnalysisSubmissionTemplate, Long> {
	@Query("FROM AnalysisSubmissionTemplate a WHERE a.submittedProject=?1")
	public List<AnalysisSubmissionTemplate> getAnalysisSubmissionTemplatesForProject(Project project);
}
