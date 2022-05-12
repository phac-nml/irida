package ca.corefacility.bioinformatics.irida.ria.unit.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.SequencingRunController;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequencingRunControllerTest {

	private static final String SEQUENCING_RUNS_PAGE = "sequencing-runs/index";
	private SequencingRunController controller;

	@BeforeEach
	public void setup() {
		controller = new SequencingRunController();
	}

	@Test
	public void testSequencingRunsPage() {
		String sequencingRunsPage = controller.getPage();
		assertEquals(SEQUENCING_RUNS_PAGE, sequencingRunsPage);
	}

}
