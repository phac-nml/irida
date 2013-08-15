
package ca.corefacility.bioinformatics.irida.web.controller.api.miseqrun;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;
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
public class MiseqRunController extends GenericController<MiseqRun, MiseqRunResource> {

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
    public MiseqRunController(MiseqRunService service) {
        super(service, MiseqRun.class, MiseqRunResource.class);

    }

}
