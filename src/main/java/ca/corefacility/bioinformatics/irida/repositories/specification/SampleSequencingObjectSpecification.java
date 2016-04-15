package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Specification for searching through {@link SampleSequencingObjectJoin}s
 */
public class SampleSequencingObjectSpecification {

	/**
	 * Get {@link SampleSequencingObjectJoin}s that have a given {@link Sample}
	 * and whose {@link SequencingObject}s are of a given type
	 * 
	 * @param sample
	 *            {@link Sample} of the join
	 * @param type
	 *            Class type of the {@link SequencingObject}
	 * @return Specification to search for the above mentioned
	 *         {@link SampleSequencingObjectJoin}s
	 */
	public static Specification<SampleSequencingObjectJoin> getSequenceOfTypeForSample(Sample sample,
			Class<? extends SequencingObject> type) {
		return new Specification<SampleSequencingObjectJoin>() {

			@Override
			public Predicate toPredicate(Root<SampleSequencingObjectJoin> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				// Get the SequencingObjects of type
				Root<? extends SequencingObject> subTypeRoot = query.from(type);

				// get objects with the given sample and whose sequencingobjects
				// are in subTypeRoot
				return cb.and(cb.equal(root.get("sample"), sample),
						cb.equal(root.get("sequencingObject").get("id"), subTypeRoot.get("id")));
			}

		};

	}
}
