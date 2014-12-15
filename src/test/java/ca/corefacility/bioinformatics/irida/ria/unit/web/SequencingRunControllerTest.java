package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.SequencingRunEntity;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.SequencingRunController;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

import com.google.common.collect.Lists;

public class SequencingRunControllerTest {
	private SequencingRunController controller;

	private SequencingRunService sequencingRunService;

	@Before
	public void setup() {
		sequencingRunService = mock(SequencingRunService.class);
		controller = new SequencingRunController(sequencingRunService);
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
	public void testGetDetailsPage() {
		Long runId = 1l;
		SequencingRun sequencingRunEntity = new SequencingRunEntity();
		ExtendedModelMap model = new ExtendedModelMap();
		when(sequencingRunService.read(runId)).thenReturn(sequencingRunEntity);

		String detailsPage = controller.getDetailsPage(runId, model);

		verify(sequencingRunService).read(runId);
		assertEquals(SequencingRunController.DETAILS_VIEW, detailsPage);
		assertEquals(sequencingRunEntity, model.get("run"));
	}
}
