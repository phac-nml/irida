package ca.corefacility.bioinformatics.irida.web.controller.test.unit.sequencingRuns;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.SequencingRunAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequenceFileDetails;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunDetails;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunModel;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunsListRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequencingRunService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequencingRunAjaxControllerTest {

	private UISequencingRunService uiService;
	private SequencingRunService runService;
	private SequencingObjectService objectService;

	private MessageSource messageSource;
	private SequencingRunAjaxController controller;
	private final User USER1 = new User(1L, "mmadrigal", "mirabel@casita.ca", "Password1!", "Mirabel", "Madrigal",
			"1234");
	private final User USER2 = new User(2L, "imadrigal", "isabela@casita.ca", "Password1!", "Isabela", "Madrigal",
			"5678");
	private final SequencingRun RUN1 = new SequencingRun(1L, "Sequencing Run 1", LayoutType.SINGLE_END, "miseq", USER1);
	private final SequencingRun RUN2 = new SequencingRun(2L, "Sequecning Run 2", LayoutType.SINGLE_END, "miseq", USER2);

	@BeforeEach
	void setUp() {
		runService = mock(SequencingRunService.class);
		objectService = mock(SequencingObjectService.class);
		messageSource = mock(MessageSource.class);
		uiService = new UISequencingRunService(runService, objectService, messageSource);
		controller = new SequencingRunAjaxController(uiService);

		Page<SequencingRun> page = new Page<SequencingRun>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return 0;
			}

			@Override
			public <U> Page<U> map(Function<? super SequencingRun, ? extends U> converter) {
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
			public List<SequencingRun> getContent() {
				return List.of(RUN1, RUN2);
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
			public Iterator<SequencingRun> iterator() {
				return null;
			}
		};

		when(uiService.getSequencingRun(anyLong())).thenReturn(RUN1);
		when(runService.read(anyLong())).thenReturn(RUN1);
		when(runService.list(anyInt(), anyInt(), any(Sort.class))).thenReturn(page);
	}

	@Test
	void getSequencingRunTest() {
		ResponseEntity<SequencingRun> response = controller.getSequencingRun(RUN1.getId());
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void getSequencingRunDetailsTest() {
		ResponseEntity<SequencingRunDetails> response = controller.getSequencingRunDetails(RUN1.getId());
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void getSequencingRunFilesTest() {
		ResponseEntity<List<SequenceFileDetails>> response = controller.getSequencingRunFiles(RUN1.getId());
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void listSequencingRunsTest() {
		SequencingRunsListRequest request = new SequencingRunsListRequest();
		request.setSortColumn("id");
		request.setSortDirection("ascend");
		request.setCurrent(1);
		request.setPageSize(10);
		TableResponse<SequencingRunModel> response = controller.listSequencingRuns(request, Locale.ENGLISH);
		assertEquals(2, response.getDataSource().size(), "Should have 2 sequencing runs");
	}

	@Test
	void deleteMetadataTemplateTest() {
		ResponseEntity<String> response = controller.deleteSequencingRun(RUN1.getId(), Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}
}
