package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pipelines/v2")
public class PipelineLaunchController {

	@RequestMapping("")
	public String getPipelineLaunchPage() {
		// Pipeline does not exist --> let's handle this client side.

		// Empty cart --> cannot do anything, redirect to pipeline listing?

		return "pipelines/launch";
	}
}
