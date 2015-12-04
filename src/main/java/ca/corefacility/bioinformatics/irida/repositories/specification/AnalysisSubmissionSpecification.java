package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Specification for searching {@link AnalysisSubmission} properties
 *
 */
public class AnalysisSubmissionSpecification {
	public static Specification<AnalysisSubmission> searchAnalysis(String name, AnalysisState state, Date minDate,
			Date maxDate) {
		return new Specification<AnalysisSubmission>() {
			@Override
			public Predicate toPredicate(Root<AnalysisSubmission> analysisSubmissionRoot,
					CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (!Strings.isNullOrEmpty(name)) {
					predicateList.add(criteriaBuilder.like(analysisSubmissionRoot.get("name"), "%" + name + "%"));
				}
				if (state != null) {
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("analysisState"), state));
				}
				if (minDate != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(analysisSubmissionRoot.get("createdDate"),
							minDate));
				}
				if (maxDate != null) {
					predicateList.add(criteriaBuilder.lessThanOrEqualTo(analysisSubmissionRoot.get("createdDate"),
							maxDate));
				}
				if (predicateList.size() > 0) {
					return criteriaBuilder.and(Iterables.toArray(predicateList, Predicate.class));
				} else {
					return null;
				}
			}
		};
	}

	/**
	 * Search for analyses with a given name, {@link AnalysisState}, or Workflow
	 * UUID
	 * 
	 * @param search
	 *            Basic search string
	 * @param name
	 *            Analysis name
	 * @param state
	 *            {@link AnalysisState}
	 * @param workflowIds
	 *            Set of UUIDs to search
	 * @return Specificaton for this search
	 */
	public static Specification<AnalysisSubmission> filterAnalyses(String search, String name, AnalysisState state, User user,
			Set<UUID> workflowIds) {
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
				if (state != null) {
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("analysisState"), state));
				}
				if (workflowIds != null && !workflowIds.isEmpty()) {
					predicateList.add(criteriaBuilder.isTrue(analysisSubmissionRoot.get("workflowId").in(workflowIds)));
				}
				if(user != null){
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("submitter"), user));
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
