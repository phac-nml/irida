package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.SequencingRunEntity;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.SequencingRunController;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class SequencingRunControllerTest {
	private SequencingRunController controller;

	private SequencingRunService sequencingRunService;
	private SequencingObjectService objectService;
	private SequenceFileWebUtilities sequenceFileUtilities;
	private MessageSource messageSource;

	@Before
	public void setup() {
		sequencingRunService = mock(SequencingRunService.class);
		sequenceFileUtilities = mock(SequenceFileWebUtilities.class);
		objectService = mock(SequencingObjectService.class);
		messageSource = mock(MessageSource.class);
		controller = new SequencingRunController(sequencingRunService, objectService, sequenceFileUtilities,
				messageSource);
	}

	@Test
	public void testGetListPage() {
		assertEquals(SequencingRunController.LIST_VIEW, controller.getListPage());
	}

	@Test
	public void testGetSequencingRuns() {
		List<SequencingRun> runs = Lists.newArrayList(new SequencingRunEntity());
		when(sequencingRunService.findAll()).thenReturn(runs);
		List<Map<String, Object>> sequencingRuns = controller.getSequencingRuns(Locale.ENGLISH);
		verify(sequencingRunService).findAll();
		assertEquals(runs.size(), sequencingRuns.size());
	}

	@Test
	public void testGetDetailsPage() throws IOException {
		Long runId = 1L;
		SequencingRun sequencingRunEntity = new SequencingRunEntity();
		ExtendedModelMap model = new ExtendedModelMap();
		when(sequencingRunService.read(runId)).thenReturn(sequencingRunEntity);

		String detailsPage = controller.getDetailsPage(runId, model);

		verify(sequencingRunService).read(runId);
		assertEquals(SequencingRunController.DETAILS_VIEW, detailsPage);
		assertEquals(sequencingRunEntity, model.get("run"));
		assertTrue(model.containsKey("files"));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetFilesPage() throws IOException {
		Long runId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		SequencingRun sequencingRunEntity = new SequencingRunEntity();
		Map<String, Object> fileMap = ImmutableMap.of("id", 5L);

		ImmutableSet<SequencingObject> files = ImmutableSet.of(new SingleEndSequenceFile(new SequenceFile()));

		when(sequencingRunService.read(runId)).thenReturn(sequencingRunEntity);
		when(objectService.getSequencingObjectsForSequencingRun(sequencingRunEntity)).thenReturn(files);
		when(sequenceFileUtilities.getFileDataMap(any(SequenceFile.class))).thenReturn(fileMap);

		String filesPage = controller.getFilesPage(runId, model);

		assertEquals(SequencingRunController.FILES_VIEW, filesPage);
		assertEquals(fileMap, ((List) model.get("files")).iterator().next());
		assertEquals(sequencingRunEntity, model.get("run"));

		verify(sequencingRunService).read(runId);
		verify(objectService).getSequencingObjectsForSequencingRun(sequencingRunEntity);
		verify(sequenceFileUtilities).getFileDataMap(any(SequenceFile.class));

	}
}
