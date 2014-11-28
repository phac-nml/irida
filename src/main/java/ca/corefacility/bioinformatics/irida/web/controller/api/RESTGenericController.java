package ca.corefacility.bioinformatics.irida.web.controller.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.GenericsException;

import com.google.common.net.HttpHeaders;

/**
 * A controller that can serve any model from the database.
 * 
 * @param <Type>
 *            the type that this controller is working with.
 * @param <ResourceType>
 *            the type that this controller uses to serialize and de-serialize
 *            the <code>Type</code> to the client.
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/api/generic")
public abstract class RESTGenericController<Type extends IridaThing & Comparable<Type>, ResourceType extends Resource<Type>> {

	/**
	 * name of objects sent back to the client for all generic resources.
	 */
	public static final String RESOURCE_NAME = "resource";
	/**
	 * name of related resources sent back to the client.
	 */
	public static final String RELATED_RESOURCES_NAME = "relatedResources";
	/**
	 * Rel used for terminating a relationship between resources.
	 */
	public static final String REL_RELATIONSHIP = "relationship";
	/**
	 * Link back to the collection after deletion of a resource.
	 */
	public static final String REL_COLLECTION = "collection";
	/**
	 * logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RESTGenericController.class);
	/**
	 * service used for working with classes in the database.
	 */
	protected CRUDService<Long, Type> crudService;
	/**
	 * The type used to serialize/de-serialize the <code>Type</code> to the
	 * client.
	 */
	private Class<ResourceType> resourceType;

	protected RESTGenericController() {
	}

	/**
	 * Construct an instance of {@link RESTGenericController}.
	 * {@link RESTGenericController} is an abstract type, and should only be used as
	 * a super-class.
	 * 
	 * @param crudService
	 *            the service used to manage resources in the database.
	 * @param identifierType
	 *            the type of identifier used by the type that this controller
	 *            manages.
	 * @param resourceType
	 *            the type used to serialize/de-serialize the type to the
	 *            client.
	 */
	protected RESTGenericController(CRUDService<Long, Type> crudService, Class<Type> type, Class<ResourceType> resourceType) {
		this.crudService = crudService;
		this.resourceType = resourceType;
	}

	/**
	 * Construct a collection of {@link Link}s for a specific resource. Each
	 * resource may have custom links that refer to other controllers, but not
	 * all will. This method is called by the <code>getResource</code> method.
	 * 
	 * @param resource
	 *            the resource to generate the links for.
	 * @return a collection of links.
	 */
	protected Collection<Link> constructCustomResourceLinks(Type resource) {
		return Collections.emptySet();
	}

	/**
	 * Get all resources in the application.
	 * 
	 * @return a model containing all resources of the specified type in the
	 *         application.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelMap listAllResources() {
		Iterable<Type> entities = crudService.findAll();
		ResourceCollection<ResourceType> resources = new ResourceCollection<>();
		try {
			for (Type entity : entities) {
				ResourceType resource = getResourceInstance(entity);
				resource.setResource(entity);
				resource.add(constructCustomResourceLinks(entity));
				resource.add(linkTo(getClass()).slash(entity.getId()).withSelfRel());
				resources.add(resource);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new GenericsException("Could not initialize resourceType: [" + resourceType + "]",e);
		}

		resources.add(linkTo(getClass()).withSelfRel());

		ModelMap model = new ModelMap();
		model.addAttribute(RESTGenericController.RESOURCE_NAME, resources);
		return model;
	}

	/**
	 * Retrieve and serialize an individual instance of a resource by
	 * identifier.
	 * 
	 * @param identifier
	 *            the identifier of the resource to retrieve from the database.
	 * @return the model and view for the individual resource.
	 */
	@RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
	public ModelMap getResource(@PathVariable Long identifier) {
		ModelMap model = new ModelMap();

		logger.debug("Getting resource with id [" + identifier + "]");
		// construct a new instance of an identifier as specified by the client

		// try to retrieve a resource from the database using the identifier
		// supplied by the client.
		Type t = crudService.read(identifier);

		// prepare the resource for serialization to the client.
		ResourceType resource = null;
		try {
			resource = getResourceInstance(t);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new GenericsException("Failed to construct an instance of ResourceType for [" + getClass() + "]");
		}
		resource.setResource(t);

		// add any custom links for the specific resource type that we're
		// serving
		// right now (implemented in the class that extends GenericController).
		resource.add(constructCustomResourceLinks(t));
		// add a self-rel to this resource
		resource.add(linkTo(getClass()).slash(identifier).withSelfRel());

		// add the resource to the model
		model.addAttribute(RESOURCE_NAME, resource);

		// send the response back to the client.
		return model;
	}

	/**
	 * Create a new instance of {@link Type} in the database, then respond to
	 * the client with the location of the resource.
	 * 
	 * @param representation
	 *            the {@link ResourceType} that we should de-serialize to get an
	 *            instance of {@link Type} to persist.
	 * @return a response containing the location of the newly persisted
	 *         resource.
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> create(@RequestBody ResourceType representation) {
		// ask the subclass to map the de-serialized request to a concrete
		// instance of the type managed by this controller.
		Type resource = representation.getResource();

		// persist the resource to the database.
		resource = crudService.create(resource);

		// the persisted resource is assigned an identifier by the
		// service/database
		// layer. We'll use this identifier to tell the client where to find the
		// persisted resource.
		Long id = resource.getId();
		logger.debug("Created resource with ID [" + resource.getId() + "]");

		// the location of the new resource is relative to this class (i.e.,
		// linkTo(getClass())) with the identifier appended.
		String location = linkTo(getClass()).slash(id).withSelfRel().getHref();

		// construct a set of headers that we can add to the response,
		// including the location header.
		MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
		responseHeaders.add(HttpHeaders.LOCATION, location);

		// send the response back to the client.
		return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
	}

	/**
	 * Delete the instance of the resource identified by a specific identifier.
	 * 
	 * @param identifier
	 *            the identifier that should be deleted from the database.
	 * @return a response indicating that the resource was deleted.
	 */
	@RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
	public ModelMap delete(@PathVariable Long identifier) {
		ModelMap modelMap = new ModelMap();

		// ask the service to delete the resource specified by the identifier
		crudService.delete(identifier);

		RootResource rootResource = new RootResource();
		rootResource.add(linkTo(getClass()).withRel(REL_COLLECTION));

		modelMap.addAttribute(RESOURCE_NAME, rootResource);

		// respond to the client with a successful message
		return modelMap;
	}

	/**
	 * Update some of the fields of an individual resource in the database. The
	 * client should only send the key-value pairs for the properties that are
	 * to be updated in the database.
	 * 
	 * @param identifier
	 *            the identifier of the resource to be updated.
	 * @param representation
	 *            the properties to be updated and their new values.
	 * @return a response indicating that the resource was updated.
	 */
	@RequestMapping(value = "/{identifier}", method = RequestMethod.PATCH, consumes = {
			MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ModelMap update(@PathVariable Long identifier, @RequestBody Map<String, Object> representation) {
		// update the resource specified by the client. clients *may* be able
		// to update the identifier of some resources, and so we should get a
		// handle on the updated resource so that we can respond with a
		// possibly updated location.
		Type resource = crudService.update(identifier, representation);
		Long id = resource.getId();
		logger.debug("Updated resource with ID [" + resource.getId() + "]");

		// construct the possibly updated location of the resource using the id
		// of the resource as returned by the service after updating.

		// create a response including the new location.
		ModelMap modelMap = new ModelMap();
		RootResource rootResource = new RootResource();
		rootResource.add(linkTo(getClass()).slash(id).withSelfRel());
		rootResource.add(linkTo(getClass()).withRel(REL_COLLECTION));
		modelMap.addAttribute(RESOURCE_NAME, rootResource);
		// respond to the client
		return modelMap;
	}
	
	/**
	 * Get an instance of the resource class for a Type
	 * @param entity the entity to get a resource instance for
	 * @return An instance of ResourceType
	 * @throws InstantiationException If the resource instance cannot be created
	 * @throws IllegalAccessException If the resource instance cannot be created
	 */
	protected ResourceType getResourceInstance(Type entity) throws InstantiationException, IllegalAccessException{
		logger.trace("Instantiating instance of " + resourceType);
		ResourceType resource = resourceType.newInstance();
		return resource;
	}
}
