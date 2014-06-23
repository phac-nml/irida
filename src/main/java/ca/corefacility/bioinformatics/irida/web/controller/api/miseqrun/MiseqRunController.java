
package ca.corefacility.bioinformatics.irida.web.controller.api.miseqrun;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.miseqrun.MiseqRunResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.GenericController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/miseqrun")
public class MiseqRunController extends GenericController<SequencingRun, MiseqRunResource> {

    /**
     * Default constructor. Should not be used.
     */
    protected MiseqRunController() {
    }

    /**
     * Constructor for {@link ProjectsController}, requires a reference to a {@link ProjectService}.
     *
     * @param service the {@link MiseqRunService} to be used by this controller.
     */
    @Autowired
    public MiseqRunController(SequencingRunService service) {
        super(service, SequencingRun.class, MiseqRunResource.class);

    }
    
}
