package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Specification for searching {@link AnalysisSubmission} properties
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class AnalysisSubmissionSpecification {
	public static Specification<AnalysisSubmission> searchAnalysis(AnalysisState state, Date minDate, Date maxDate) {
		return new Specification<AnalysisSubmission>() {
			@Override public Predicate toPredicate(Root<AnalysisSubmission> analysisSubmissionRoot,
					CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (state != null) {
					predicateList.add(criteriaBuilder.equal(analysisSubmissionRoot.get("analysisState"), state));
				}
				if (minDate != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(
							analysisSubmissionRoot.<Date>get("createdDate"), minDate));
				}
				if (maxDate != null) {
					predicateList.add(criteriaBuilder
							.lessThanOrEqualTo(analysisSubmissionRoot.<Date>get("createdDate"), maxDate));
				}
				if (predicateList.size() > 0) {
					return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
				} else {
					return null;
				}
			}
		};
	}
}
