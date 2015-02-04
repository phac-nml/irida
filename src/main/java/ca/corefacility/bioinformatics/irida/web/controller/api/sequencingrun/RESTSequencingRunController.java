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

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.lookup.ModelLookup;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencingrun.MiseqRunResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencingrun.SequencingRunResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/api/sequencingrun")
public class RESTSequencingRunController extends RESTGenericController<SequencingRun, SequencingRunResource> {
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
		super(service, SequencingRun.class, SequencingRunResource.class);

	}

	@RequestMapping(value = "/miseqrun", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ModelMap createMiseqRun(@RequestBody MiseqRunResource representation,HttpServletResponse response) {
		return create(representation,response);
	}

	/**
	 * {@inheritDoc}
	 * Looking through the SequencingRun classes for the correct resource class
	 */
	@Override
	protected SequencingRunResource getResourceInstance(SequencingRun entity) throws InstantiationException,
			IllegalAccessException {
		logger.trace("Looking up resource type for " + entity.getClass());
		Class<? extends IdentifiableResource<? extends IridaThing>> resourceClass = ModelLookup.getResourceClass(entity
				.getClass());

		if (resourceClass == null) {
			throw new GenericsException("Could not initialize resource for type: [" + entity.getClass() + "]");
		}

		SequencingRunResource resource = null;

		logger.trace("Using type " + resourceClass + " for type " + entity.getClass());

		try {
			resource = (SequencingRunResource) resourceClass.newInstance();
		} catch (ClassCastException ex) {
			throw new GenericsException("Could not initialize resource for type: [" + entity.getClass() + "]", ex);
		}

		return resource;
	}

}
