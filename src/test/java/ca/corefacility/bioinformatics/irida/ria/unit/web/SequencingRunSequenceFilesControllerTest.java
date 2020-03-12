package ca.corefacility.bioinformatics.irida.ria.unit.web;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunController;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;
import com.google.common.net.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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

		ModelMap modelMap = controller.addSequenceFilesToSequencingRun(sequencingrunId, representation, response);

		verify(objectService).read(seqId);
		verify(miseqRunService).read(sequencingrunId);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull("Object should not be null", o);
		assertTrue("Object should be an instance of SequencingRun", o instanceof SequencingRun);
		SequencingRun res = (SequencingRun) o;
		String seqFileLocation = linkTo(RESTSequencingRunController.class).slash(sequencingrunId)
				.slash("sequenceFiles")
				.slash(seqId)
				.withSelfRel()
				.getHref();
		assertEquals("Sequence file location should be correct", seqFileLocation, res.getLink(Link.REL_SELF)
				.getHref());
		assertEquals("Sequence file location should be correct", seqFileLocation,
				response.getHeader(HttpHeaders.LOCATION));
		assertEquals("HTTP status must be CREATED", HttpStatus.CREATED.value(), response.getStatus());
	}

}
