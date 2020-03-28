package ca.corefacility.bioinformatics.irida.ria.unit.web;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.SequencingRunController;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import java.io.IOException;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SequencingRunControllerTest {
	private SequencingRunController controller;

	private SequencingRunService sequencingRunService;
	private SequencingObjectService objectService;


	@Before
	public void setup() {
		sequencingRunService = mock(SequencingRunService.class);
		objectService = mock(SequencingObjectService.class);
		controller = new SequencingRunController(sequencingRunService, objectService);
	}

	@Test
	public void testGetListPage() {
		assertEquals(SequencingRunController.LIST_VIEW, controller.getListPage());
	}

	@Test
	public void testGetDetailsPage() throws IOException {
		Long runId = 1L;
		SequencingRun sequencingRunEntity = new SequencingRun(SequencingRun.LayoutType.PAIRED_END, "miseq");
		ExtendedModelMap model = new ExtendedModelMap();
		when(sequencingRunService.read(runId)).thenReturn(sequencingRunEntity);

		String detailsPage = controller.getDetailsPage(runId, model);

		verify(sequencingRunService).read(runId);
		assertEquals(SequencingRunController.DETAILS_VIEW, detailsPage);
		assertEquals(sequencingRunEntity, model.get("run"));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetFilesPage() throws IOException {
		Long runId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		SequencingRun sequencingRunEntity = new SequencingRun(SequencingRun.LayoutType.PAIRED_END, "miseq");

		ImmutableSet<SequencingObject> files = ImmutableSet.of(new SingleEndSequenceFile(new LocalSequenceFile()));

		when(sequencingRunService.read(runId)).thenReturn(sequencingRunEntity);
		when(objectService.getSequencingObjectsForSequencingRun(sequencingRunEntity)).thenReturn(files);

		String filesPage = controller.getFilesPage(runId, model);

		assertEquals(SequencingRunController.FILES_VIEW, filesPage);
		assertFalse(((Collection) model.get("sequencingObjects")).isEmpty());
		assertEquals(sequencingRunEntity, model.get("run"));
		assertTrue(model.containsKey("fileCount"));

		verify(sequencingRunService).read(runId);
		verify(objectService).getSequencingObjectsForSequencingRun(sequencingRunEntity);

	}
}
