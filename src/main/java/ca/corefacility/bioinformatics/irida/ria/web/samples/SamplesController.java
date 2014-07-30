package ca.corefacility.bioinformatics.irida.ria.web.samples;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for all sample related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/samples")
public class SamplesController {
	// Page Names
	private static final String SAMPlES_DIR = "samples/";
	private static final String SAMPLE_PAGE = SAMPlES_DIR + "sample";
}
