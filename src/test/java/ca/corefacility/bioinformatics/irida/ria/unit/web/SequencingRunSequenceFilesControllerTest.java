package ca.corefacility.bioinformatics.irida.ria.unit.web;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunController;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.net.HttpHeaders;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Tests for {@link RESTSequencingRunSequenceFilesController}.
 */
public class SequencingRunSequenceFilesControllerTest {
	private RESTSequencingRunSequenceFilesController controller;
	private SequencingObjectService objectService;
	private SequencingRunService miseqRunService;

	@Before
	public void setUp() {
		miseqRunService = mock(SequencingRunService.class);
		objectService = mock(SequencingObjectService.class);
		controller = new RESTSequencingRunSequenceFilesController(miseqRunService, objectService);
	}

	@Test
	public void addSequenceFileToMiseqRunTest() throws IOException {
		Long seqId = 1L;
		Long sequencingrunId = 2L;
		MockHttpServletResponse response = new MockHttpServletResponse();

		SingleEndSequenceFile singleEndSequenceFile = TestDataFactory.constructSingleEndSequenceFile();

		SequencingRun run = new SequencingRun(LayoutType.SINGLE_END, "miseq");
		Map<String, String> representation = new HashMap<String, String>();
		representation.put(RESTSequencingRunSequenceFilesController.SEQUENCEFILE_ID_KEY, "" + seqId);

		when(objectService.read(seqId)).thenReturn(singleEndSequenceFile);
		when(miseqRunService.read(sequencingrunId)).thenReturn(run);

		ResponseResource<SequencingRun> responseResource = controller.addSequenceFilesToSequencingRun(sequencingrunId,
				representation, response);

		verify(objectService).read(seqId);
		verify(miseqRunService).read(sequencingrunId);

		SequencingRun res = responseResource.getResource();
		assertNotNull("Sequencing run should not be null", res);
		String seqFileLocation = linkTo(RESTSequencingRunController.class).slash(sequencingrunId)
				.slash("sequenceFiles")
				.slash(seqId)
				.withSelfRel()
				.getHref();
		assertEquals("Sequence file location should be correct", seqFileLocation, res.getLink(IanaLinkRelations.SELF.value())
				.map(i -> i.getHref()).orElse(null));
		assertEquals("Sequence file location should be correct", seqFileLocation,
				response.getHeader(HttpHeaders.LOCATION));
		assertEquals("HTTP status must be CREATED", HttpStatus.CREATED.value(), response.getStatus());
	}

}
