
package ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

import com.google.common.net.HttpHeaders;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Controller
public class SequencingRunSequenceFilesController {

    private SequencingRunService miseqRunService;
    private SequenceFileService sequencefileService;
    /**
     * key used in map when adding sequencefile to miseqrun.
     */
    public static final String SEQUENCEFILE_ID_KEY = "sequenceFileId";
    
    @Autowired
    public SequencingRunSequenceFilesController(SequencingRunService service, SequenceFileService sequencefileService) {
        this.miseqRunService = service;
        this.sequencefileService = sequencefileService;
    }
    
    /**
     * Add a relationship between a {@link MiseqRun} and a {@link SequenceFile}.
     *
     * @param representation the JSON key-value pair that contains the identifier for the sequenceFile
     * @return a response indicating that the collection was modified.
     */
    @RequestMapping(value = "/api/sequencingrun/{sequencingrunId}/sequenceFiles", method = RequestMethod.POST)
    public ResponseEntity<String> addSequenceFileToMiseqRun(@PathVariable Long sequencingrunId,
                                                   @RequestBody Map<String, String> representation) {
        
        String stringId = representation.get(SEQUENCEFILE_ID_KEY);
        long seqId = Long.parseLong(stringId);
        // first, get the SequenceFile
        SequenceFile file = sequencefileService.read(seqId);
        // then, get the miseq run
        SequencingRun run = miseqRunService.read(sequencingrunId);
        // then add the user to the project with the specified role.
        miseqRunService.addSequenceFileToSequencingRun(run, file);

        String location = linkTo(SequencingRunController.class).slash(sequencingrunId).slash("sequenceFiles").slash(seqId).withSelfRel().getHref();

        MultiValueMap<String, String> responseHeaders = new LinkedMultiValueMap<>();
        responseHeaders.add(HttpHeaders.LOCATION, location);

        return new ResponseEntity<>("success", responseHeaders, HttpStatus.CREATED);
    }
}
