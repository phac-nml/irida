package ca.corefacility.bioinformatics.irida.web.controller.api.miseqrun;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.lookup.ModelLookup;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.miseqrun.MiseqRunResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.miseqrun.SequencingRunResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.ProjectsController;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/sequencingrun")
public class MiseqRunController extends GenericController<SequencingRun, SequencingRunResource> {
	private static final Logger logger = LoggerFactory.getLogger(MiseqRunController.class);

	private static Map<Class<? extends SequencingRun>, Class<? extends SequencingRunResource>> classes = new HashMap<>();
	static {
		classes.put(MiseqRun.class, MiseqRunResource.class);
	}

	/**
	 * Default constructor. Should not be used.
	 */
	protected MiseqRunController() {
	}

	/**
	 * Constructor for {@link ProjectsController}, requires a reference to a
	 * {@link ProjectService}.
	 *
	 * @param service
	 *            the {@link MiseqRunService} to be used by this controller.
	 */
	@Autowired
	public MiseqRunController(SequencingRunService service) {
		super(service, SequencingRun.class, SequencingRunResource.class);

	}

	@RequestMapping(value = "/miseqrun", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> createMiseqRun(@RequestBody MiseqRunResource representation) {
		return create(representation);
	}

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
