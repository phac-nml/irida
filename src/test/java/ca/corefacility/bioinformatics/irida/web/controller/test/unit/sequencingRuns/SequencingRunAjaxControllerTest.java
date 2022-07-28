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
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SequencingRunAjaxControllerTest {
	private UISequencingRunService uiService;
	private SequencingRunService runService;
	private SequencingObjectService objectService;
	private ProjectService projectService;
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;
	private MessageSource messageSource;
	private SequencingRunAjaxController controller;
	private User user1, user2;
	private SequencingRun run1, run2;
	private Page<SequencingRun> page;

	@BeforeEach
	void setUp() {
		runService = mock(SequencingRunService.class);
		objectService = mock(SequencingObjectService.class);
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		sequenceFileService = mock(SequenceFileService.class);
		messageSource = mock(MessageSource.class);
		uiService = new UISequencingRunService(runService, objectService, projectService, sampleService,
				sequenceFileService, messageSource);
		controller = new SequencingRunAjaxController(uiService);

		user1 = new User(1L, "mirabel", "mirabel@casita.ca", "Password1!", "Mirabel", "Madrigal", "1234");
		user2 = new User(2L, "isabela", "isabela@casita.ca", "Password1!", "Isabela", "Madrigal", "5678");

		run1 = new SequencingRun(LayoutType.SINGLE_END, "miseq");
		run1.setId(1L);
		run1.setDescription("Sequencing Run 1");
		run1.setUser(user1);

		run2 = new SequencingRun(LayoutType.PAIRED_END, "miseq");
		run2.setId(2L);
		run2.setDescription("Sequencing Run 2");
		run2.setUser(user2);

		page = new Page<SequencingRun>() {
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
				return List.of(run1, run2);
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

		when(uiService.getSequencingRun(anyLong())).thenReturn(run1);
		when(runService.read(anyLong())).thenReturn(run1);
		when(runService.list(anyInt(), anyInt(), any(Sort.class))).thenReturn(page);
	}

	@Test
	void getSequencingRunTest() {
		ResponseEntity<SequencingRun> response = controller.getSequencingRun(run1.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Received an 200 OK response");
	}

	@Test
	void getSequencingRunDetailsTest() {
		ResponseEntity<SequencingRunDetails> response = controller.getSequencingRunDetails(run1.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Received an 200 OK response");
	}

	@Test
	void getSequencingRunFilesTest() {
		ResponseEntity<List<SequenceFileDetails>> response = controller.getSequencingRunFiles(run1.getId());
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Received an 200 OK response");
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
		ResponseEntity<String> response = controller.deleteSequencingRun(run1.getId(), Locale.ENGLISH);
		assertEquals(HttpStatus.OK, response.getStatusCode(), "Received an 200 OK response");
	}
}
