package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for managing relationships between {@link Sample} and {@link SequenceFile}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class SampleSequenceFilesController {

    /**
     * Get the {@link SequenceFile} entities associated with a specific {@link Sample}.
     *
     * @param projectId the identifier for the {@link Project}.
     * @param sampleId  the identifier for the {@link Sample}.
     * @return the {@link SequenceFile} entities associated with the {@link Sample}.
     */
    @RequestMapping(value = "/projects/{projectId}/samples/{sampleId}/sequenceFiles", method = RequestMethod.GET)
    public ModelMap getSampleSequenceFiles(@PathVariable String projectId, @PathVariable String sampleId) {
        throw new UnsupportedOperationException("not implemented.");
    }
}
