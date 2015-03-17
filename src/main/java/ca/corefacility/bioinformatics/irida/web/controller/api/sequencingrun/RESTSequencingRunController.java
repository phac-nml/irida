package ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;

/**
 *
 */
@Controller
@RequestMapping(value = "/api/sequencingrun")
public class RESTSequencingRunController extends RESTGenericController<SequencingRun> {
	private static final Logger logger = LoggerFactory.getLogger(RESTSequencingRunController.class);

	/**
	 * Default constructor. Should not be used.
	 */
	protected RESTSequencingRunController() {
	}

	/**
	 * Constructor for {@link RESTProjectsController}, requires a reference to a
	 * {@link ProjectService}.
	 *
	 * @param service
	 *            the {@link MiseqRunService} to be used by this controller.
	 */
	@Autowired
	public RESTSequencingRunController(SequencingRunService service) {
		super(service, SequencingRun.class);

	}

	@RequestMapping(value = "/miseqrun", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ModelMap createMiseqRun(@RequestBody MiseqRun representation, HttpServletResponse response) {
		logger.trace("creating miseq run");
		return create(representation, response);
	}

}
