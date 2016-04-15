package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunController;
import ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun.RESTSequencingRunSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.net.HttpHeaders;

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

		MiseqRun run = new MiseqRun(LayoutType.SINGLE_END, "workflow");
		Map<String, String> representation = new HashMap<String, String>();
		representation.put(RESTSequencingRunSequenceFilesController.SEQUENCEFILE_ID_KEY, "" + seqId);

		when(objectService.read(seqId)).thenReturn(singleEndSequenceFile);
		when(miseqRunService.read(sequencingrunId)).thenReturn(run);

		ModelMap modelMap = controller.addSequenceFileToMiseqRun(sequencingrunId, representation, response);

		verify(objectService).read(seqId);
		verify(miseqRunService).read(sequencingrunId);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull("Object should not be null", o);
		assertTrue("Object should be an instance of MiseqRunResource", o instanceof MiseqRun);
		MiseqRun res = (MiseqRun) o;
		String seqFileLocation = linkTo(RESTSequencingRunController.class).slash(sequencingrunId)
				.slash("sequenceFiles").slash(seqId).withSelfRel().getHref();
		assertEquals("Sequence file location should be correct", seqFileLocation, res.getLink(Link.REL_SELF).getHref());
		assertEquals("Sequence file location should be correct", seqFileLocation,
				response.getHeader(HttpHeaders.LOCATION));
		assertEquals("HTTP status must be CREATED", HttpStatus.CREATED.value(), response.getStatus());
	}

}
