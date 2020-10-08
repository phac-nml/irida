package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.google.common.collect.ImmutableList;

/**
 * Factory for creating a {@link Page} of {@link AnalysisSubmission} for testing the DataTables calls.
 */
public class AnalysesDataFactory {
	protected static Page<AnalysisSubmission> getPagedAnalysisSubmissions() {
		return new Page<AnalysisSubmission>() {
			@Override
			public int getTotalPages() {
				return 15;
			}

			@Override
			public long getTotalElements() {
				return 150;
			}

			@Override
			public <U> Page<U> map(Function<? super AnalysisSubmission, ? extends U> function) {
				return null;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public List<AnalysisSubmission> getContent() {
				return ImmutableList.of();
			}

			@Override
			public boolean hasContent() {
				return false;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<AnalysisSubmission> iterator() {
				return null;
			}
		};
	}
}
