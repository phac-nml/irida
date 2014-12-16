package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.SequencingRunEntity;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.SequencingRunController;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class SequencingRunControllerTest {
	private SequencingRunController controller;

	private SequencingRunService sequencingRunService;
	private SequenceFileService sequenceFileService;
	private SequenceFileWebUtilities sequenceFileUtilities;

	@Before
	public void setup() {
		sequencingRunService = mock(SequencingRunService.class);
		sequenceFileService = mock(SequenceFileService.class);
		sequenceFileUtilities = mock(SequenceFileWebUtilities.class);
		controller = new SequencingRunController(sequencingRunService, sequenceFileService, sequenceFileUtilities);
	}

	@Test
	public void testGetListPage() {
		assertEquals(SequencingRunController.LIST_VIEW, controller.getListPage());
	}

	@Test
	public void testGetSequencingRuns() {
		List<SequencingRun> runs = Lists.newArrayList(new SequencingRunEntity());
		when(sequencingRunService.findAll()).thenReturn(runs);
		Iterable<SequencingRun> sequencingRuns = controller.getSequencingRuns();
		verify(sequencingRunService).findAll();
		assertEquals(sequencingRuns, runs);
	}

	@Test
	public void testGetDetailsPage() throws IOException {
		Long runId = 1l;
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
		Long runId = 1l;
		ExtendedModelMap model = new ExtendedModelMap();
		SequencingRun sequencingRunEntity = new SequencingRunEntity();
		Map<String, Object> fileMap = ImmutableMap.of("id", 5l);
		Set<SequenceFile> files = ImmutableSet.of(new SequenceFile());

		when(sequencingRunService.read(runId)).thenReturn(sequencingRunEntity);
		when(sequenceFileService.getSequenceFilesForSequencingRun(sequencingRunEntity)).thenReturn(files);
		when(sequenceFileUtilities.getFileDataMap(files.iterator().next())).thenReturn(fileMap);

		String filesPage = controller.getFilesPage(runId, model);

		assertEquals(SequencingRunController.FILES_VIEW, filesPage);
		assertEquals(fileMap, ((List) model.get("files")).iterator().next());
		assertEquals(sequencingRunEntity, model.get("run"));

		verify(sequencingRunService).read(runId);
		verify(sequenceFileService).getSequenceFilesForSequencingRun(sequencingRunEntity);
		verify(sequenceFileUtilities).getFileDataMap(files.iterator().next());

	}
}
