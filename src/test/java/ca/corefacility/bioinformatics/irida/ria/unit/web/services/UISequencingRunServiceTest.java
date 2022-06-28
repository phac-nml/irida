package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
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

public class UISequencingRunServiceTest {
	private static final String SUCCESS_MESSAGE = "Successfully deleted sequencing run 1";
	private UISequencingRunService service;
	private SequencingRunService runService;
	private SequencingObjectService objectService;
	private MessageSource messageSource;
	private User user1, user2;
	private SequencingRun run1, run2;
	private SequenceFile file1, file2, file3;
	private SequencingObject object1, object2;
	private Page<SequencingRun> page;

	@BeforeEach
	void setUp() {
		runService = mock(SequencingRunService.class);
		objectService = mock(SequencingObjectService.class);
		messageSource = mock(MessageSource.class);
		service = new UISequencingRunService(runService, objectService, messageSource);

		user1 = new User(1L, "mirabel", "mirabel@casita.ca", "Password1!", "Mirabel", "Madrigal", "1234");
		user2 = new User(2L, "isabela", "isabela@casita.ca", "Password1!", "Isabela", "Madrigal", "5678");

		run1 = new SequencingRun(SequencingRun.LayoutType.SINGLE_END, "miseq");
		run1.setId(1L);
		run1.setDescription("Sequencing Run 1");
		run1.setUser(user1);

		run2 = new SequencingRun(SequencingRun.LayoutType.PAIRED_END, "miseq");
		run2.setId(2L);
		run2.setDescription("Sequencing Run 2");
		run2.setUser(user2);

		file1 = new SequenceFile(Path.of("/tmp/file1.fastq"));
		file1.setId(1L);

		file2 = new SequenceFile(Path.of("/tmp/file2.fastq"));
		file2.setId(2L);

		file3 = new SequenceFile(Path.of("/tmp/file3.fastq"));
		file3.setId(3L);

		object1 = new SingleEndSequenceFile(file1);
		object1.setId(1L);

		object2 = new SequenceFilePair(file2, file3);
		object2.setId(2L);

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

		when(runService.read(anyLong())).thenReturn(run1);
		when(runService.list(anyInt(), anyInt(), any(Sort.class))).thenReturn(page);
		when(objectService.getSequencingObjectsForSequencingRun(any())).thenReturn(
				Stream.of(object1, object2).collect(Collectors.toCollection(HashSet::new)));
		when(messageSource.getMessage(anyString(), any(), any())).thenReturn(SUCCESS_MESSAGE);
	}

	@Test
	void getSequencingRunTest() {
		SequencingRun response = service.getSequencingRun(run1.getId());
		assertEquals(run1, response, "Received the correct sequencing run response");
	}

	@Test
	void getSequencingRunDetailsTest() {
		SequencingRunDetails response = service.getSequencingRunDetails(run1.getId());
		SequencingRunDetails expectedResponse = new SequencingRunDetails(run1);
		assertEquals(expectedResponse, response, "Received the correct sequencing run details response");
	}

	@Test
	void getSequencingRunFilesTest() {
		List<SequenceFileDetails> response = service.getSequencingRunFiles(run1.getId());
		List<SequenceFileDetails> expectedResponse = new ArrayList<>();
		expectedResponse.add(new SequenceFileDetails(file1, object1.getId()));
		expectedResponse.add(new SequenceFileDetails(file2, object2.getId()));
		expectedResponse.add(new SequenceFileDetails(file3, object2.getId()));
		assertEquals(expectedResponse, response, "Received the correct sequencing run files response");
	}

	@Test
	void listSequencingRunsTest() {
		SequencingRunsListRequest request = new SequencingRunsListRequest();
		request.setSortColumn("id");
		request.setSortDirection("ascend");
		request.setCurrent(1);
		request.setPageSize(10);
		TableResponse<SequencingRunModel> response = service.listSequencingRuns(request, Locale.ENGLISH);
		assertEquals(2, response.getDataSource().size(), "Received the correct sequencing run table response");

	}

	@Test
	void deleteSequencingRunTest() {
		String response = service.deleteSequencingRun(run1.getId(), Locale.ENGLISH);
		assertEquals(SUCCESS_MESSAGE, response, "Received the correct delete sequencing run response");
	}
}
