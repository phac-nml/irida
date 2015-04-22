package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Specification for searching {@link AnalysisSubmission} properties
 *
 */
public class AnalysisSubmissionSpecification {
	public static Specification<AnalysisSubmission> searchAnalysis(String name, AnalysisState state, Date minDate, Date maxDate) {
		return new Specification<AnalysisSubmission>() {
			@Override public Predicate toPredicate(Root<AnalysisSubmission> analysisSubmissionRoot,
					CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (!Strings.isNullOrEmpty(name)) {
					predicateList.add(criteriaBuilder.like(analysisSubmissionRoot.get("name"), "%" + name + "%"));
				}
				if (state != null) {
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("analysisState"), state));
				}
				if (minDate != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(
							analysisSubmissionRoot.get("createdDate"), minDate));
				}
				if (maxDate != null) {
					predicateList.add(criteriaBuilder
							.lessThanOrEqualTo(analysisSubmissionRoot.get("createdDate"), maxDate));
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
