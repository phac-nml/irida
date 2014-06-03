package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controllers for handling AngularJS request for the Files
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/files/view/")
public class FilesViewController {
	private static final Logger logger = LoggerFactory.getLogger(FilesViewController.class);

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String getFilesView() {
		logger.debug("Getting Files Main View");
		return "views/files";
	}
}
