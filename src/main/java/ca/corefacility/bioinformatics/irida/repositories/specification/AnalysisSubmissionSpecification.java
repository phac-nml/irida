package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Specification for searching {@link AnalysisSubmission} properties
 *
 */
public class AnalysisSubmissionSpecification {

	/**
	 * Search for analyses with a given name, {@link AnalysisState}, or Workflow UUID
	 *
	 * @param search      Basic search string
	 * @param name        Analysis name
	 * @param state       {@link AnalysisState}
	 * @param workflowIds Set of UUIDs to search
	 * @param user        The {@link User} owning the analysis
	 * @param project     A project the analysis is shared with
	 * @param automated   Whether this analysis submission was submitted as part of an automated process.
	 * @return Specificaton for this search
	 */
	public static Specification<AnalysisSubmission> filterAnalyses(String search, String name, Set<AnalysisState> states,
			User user, Set<UUID> workflowIds, Project project, Boolean automated) {
		return new Specification<AnalysisSubmission>() {
			@Override
			public Predicate toPredicate(Root<AnalysisSubmission> analysisSubmissionRoot,
					CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();

				if (!Strings.isNullOrEmpty(search)) {
					predicateList.add(criteriaBuilder.like(analysisSubmissionRoot.get("name"), "%" + search + "%"));
				}
				if (!Strings.isNullOrEmpty(name)) {
					predicateList.add(criteriaBuilder.like(analysisSubmissionRoot.get("name"), "%" + name + "%"));
				}
				if (states != null && !states.isEmpty()) {
					predicateList.add(criteriaBuilder.isTrue(analysisSubmissionRoot.get("analysisState").in(states)));
				}
				if (workflowIds != null && !workflowIds.isEmpty()) {
					predicateList.add(criteriaBuilder.isTrue(analysisSubmissionRoot.get("workflowId").in(workflowIds)));
				}
				if (user != null) {
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("submitter"), user));
				}
				if(automated != null){
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("automated"), automated));
				}

				/*
				 * If we're searching for a specific project's analyses
				 */
				if (project != null) {
					Subquery<Long> projectSelect = criteriaQuery.subquery(Long.class);
					Root<ProjectAnalysisSubmissionJoin> projectRoot = projectSelect
							.from(ProjectAnalysisSubmissionJoin.class);
					projectSelect.select(projectRoot.get("analysisSubmission").get("id"))
							.where(criteriaBuilder.equal(projectRoot.get("project"), project));

					predicateList.add(criteriaBuilder.in(analysisSubmissionRoot.get("id")).value(projectSelect));
				}

				if (predicateList.size() > 0) {
					return criteriaBuilder.and(Iterables.toArray(predicateList, Predicate.class));
				} else {
					return null;
				}
			}
		};
	}
}
