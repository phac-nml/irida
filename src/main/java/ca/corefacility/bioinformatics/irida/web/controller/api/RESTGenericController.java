package ca.corefacility.bioinformatics.irida.web.controller.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;

import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

/**
 * A controller that can serve any model from the database.
 *
 * @param <Type> the type that this controller is working with.
 */
@Controller
@RequestMapping("/api/generic")
public abstract class RESTGenericController<Type extends IridaResourceSupport & IridaThing & Comparable<Type>> {

	/**
	 * name of the objects used to render the view classes.
	 */
	public static final String RESOURCE_NAME = "responseResource";
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

	protected RESTGenericController() {
	}

	/**
	 * Construct an instance of {@link RESTGenericController}.
	 * {@link RESTGenericController} is an abstract type, and should only be
	 * used as a super-class.
	 *
	 * @param crudService the service used to manage resources in the database.
	 * @param type        the type of that this controller manages.
	 */
	protected RESTGenericController(CRUDService<Long, Type> crudService, Class<Type> type) {
		this.crudService = crudService;
	}

	/**
	 * Construct a collection of {@link Link}s for a specific resource. Each
	 * resource may have custom links that refer to other controllers, but not
	 * all will. This method is called by the <code>getResource</code> method.
	 *
	 * @param resource the resource to generate the links for.
	 * @return a collection of links.
	 */
	protected Collection<Link> constructCustomResourceLinks(Type resource) {
		return Collections.emptySet();
	}

	/**
	 * Get all resources in the application.
	 *
	 * @return a model containing all resources of the specified type in the
	 * application.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseResource<ResourceCollection<Type>> listAllResources() {
		Iterable<Type> entities = crudService.findAll();
		ResourceCollection<Type> resources = new ResourceCollection<>();
		for (Type entity : entities) {
			entity.add(constructCustomResourceLinks(entity));
			entity.add(linkTo(getClass()).slash(entity.getId())
					.withSelfRel());
			resources.add(entity);
		}

		resources.add(linkTo(getClass()).withSelfRel());

		// get custom links for the collection
		Collection<Link> constructCollectionResourceLinks = constructCollectionResourceLinks(resources);
		resources.add(constructCollectionResourceLinks);

		ResponseResource<ResourceCollection<Type>> responseObject = new ResponseResource<>(resources);
		return responseObject;
	}

	/**
	 * Retrieve and serialize an individual instance of a resource by
	 * identifier.
	 *
	 * @param identifier the identifier of the resource to retrieve from the database.
	 * @return the model and view for the individual resource.
	 */
	@RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
	public ResponseResource<Type> getResource(@PathVariable Long identifier) {
		logger.trace("Getting resource with id [" + identifier + "]");
		// construct a new instance of an identifier as specified by the client

		// try to retrieve a resource from the database using the identifier
		// supplied by the client.
		Type t = crudService.read(identifier);

		// add any custom links for the specific resource type that we're
		// serving
		// right now (implemented in the class that extends GenericController).
		t.add(constructCustomResourceLinks(t));
		// add a self-rel to this resource
		t.add(linkTo(getClass()).slash(identifier)
				.withSelfRel());

		// add the resource to the model
		ResponseResource<Type> responseObject = new ResponseResource<>(t);

		// send the response back to the client.
		return responseObject;
	}

	/**
	 * Create a new instance of {@code Type} in the database, then respond to
	 * the client with the location of the resource.
	 *
	 * @param resource the {@code Type} that we should de-serialize to get an
	 *                 instance of {@code Type} to persist.
	 * @param response a reference to the servlet response.
	 * @return a response containing the location of the newly persisted
	 * resource.
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseResource<Type> create(@RequestBody Type resource, HttpServletResponse response) {

		// ask the subclass to map the de-serialized request to a concrete
		// instance of the type managed by this controller.

		// persist the resource to the database.
		resource = crudService.create(resource);

		// the persisted resource is assigned an identifier by the
		// service/database
		// layer. We'll use this identifier to tell the client where to find the
		// persisted resource.
		Long id = resource.getId();
		logger.trace("Created resource with ID [" + resource.getId() + "]");

		// In order to obtain a correct created date, the persisted resource is
		// accessed from the service/database layer.
		Type readType = crudService.read(id);

		// the location of the new resource is relative to this class (i.e.,
		// linkTo(getClass())) with the identifier appended.
		String location = linkTo(getClass()).slash(id)
				.withSelfRel()
				.getHref();

		// add any custom links for the specific resource type that we're
		// serving
		// right now (implemented in the class that extends GenericController).
		readType.add(constructCustomResourceLinks(resource));

		//add a self reference
		readType.add(linkTo(getClass()).slash(id)
				.withSelfRel());

		// add the resource to the model
		ResponseResource<Type> responseObject = new ResponseResource<>(readType);

		// add a location header.
		response.addHeader(HttpHeaders.LOCATION, location);

		// set the response status.
		response.setStatus(HttpStatus.CREATED.value());

		// send the response back to the client.
		return responseObject;
	}

	/**
	 * Delete the instance of the resource identified by a specific identifier.
	 *
	 * @param identifier the identifier that should be deleted from the database.
	 * @return a response indicating that the resource was deleted.
	 */
	@RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
	public ResponseResource<RootResource> delete(@PathVariable Long identifier) {

		// ask the service to delete the resource specified by the identifier
		crudService.delete(identifier);

		RootResource rootResource = new RootResource();
		rootResource.add(linkTo(getClass()).withRel(REL_COLLECTION));

		ResponseResource<RootResource> responseObject = new ResponseResource<>(rootResource);

		// respond to the client with a successful message
		return responseObject;
	}

	/**
	 * Update some of the fields of an individual resource in the database. The
	 * client should only send the key-value pairs for the properties that are
	 * to be updated in the database.
	 *
	 * @param identifier     the identifier of the resource to be updated.
	 * @param representation the properties to be updated and their new values.
	 * @return a response indicating that the resource was updated.
	 */
	@RequestMapping(value = "/{identifier}", method = RequestMethod.PATCH, consumes = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseResource<RootResource> update(@PathVariable Long identifier,
			@RequestBody Map<String, Object> representation) {
		// update the resource specified by the client. clients *may* be able
		// to update the identifier of some resources, and so we should get a
		// handle on the updated resource so that we can respond with a
		// possibly updated location.
		Type resource = crudService.updateFields(identifier, representation);
		Long id = resource.getId();
		logger.trace("Updated resource with ID [" + resource.getId() + "]");

		// construct the possibly updated location of the resource using the id
		// of the resource as returned by the service after updating.

		// create a response including the new location.
		RootResource rootResource = new RootResource();
		rootResource.add(linkTo(getClass()).slash(id)
				.withSelfRel());
		rootResource.add(linkTo(getClass()).withRel(REL_COLLECTION));
		ResponseResource<RootResource> responseObject = new ResponseResource<>(rootResource);
		// respond to the client
		return responseObject;
	}

	/**
	 * Get custom links to the ResourceCollection being called by
	 * {@link RESTGenericController#listAllResources()}. This method can be
	 * overridden by extending classes to add links to the returned list.
	 *
	 * @param list {@link ResourceCollection} to add links to
	 * @return Collection of links to add to the {@link ResourceCollection}
	 */
	protected Collection<Link> constructCollectionResourceLinks(ResourceCollection<Type> list) {
		return Sets.newHashSet();
	}

}
