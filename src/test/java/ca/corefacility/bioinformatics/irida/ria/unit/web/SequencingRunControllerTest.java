package ca.corefacility.bioinformatics.irida.ria.unit.web;

import org.junit.jupiter.api.BeforeEach;

import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.SequencingRunController;

public class SequencingRunControllerTest {
	private SequencingRunController controller;

	@BeforeEach
	public void setup() {
		controller = new SequencingRunController();
	}

}
