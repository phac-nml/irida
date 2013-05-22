package ca.corefacility.bioinformatics.irida.web.controller.api;

import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.service.RelationshipService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.relationship.RelationshipResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for managing {@link Relationship} entities.
 */
@Controller
@ExposesResourceFor(Relationship.class)
@RequestMapping("/relationships")
public class RelationshipsController extends GenericController<Identifier, Relationship, RelationshipResource> {
    /**
     * logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RelationshipsController.class);

    /**
     * Create a controller for managing {@link Relationship} resources on the web.
     *
     * @param relationshipService the service used to manage resources in the database.
     */
    @Autowired
    public RelationshipsController(RelationshipService relationshipService) {
        super(relationshipService, relationshipService, Relationship.class, Identifier.class,
                RelationshipResource.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Relationship mapResourceToType(RelationshipResource resource) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
