package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax/launch")
public class LaunchAjaxController {

    @GetMapping("/pipelineId")
    public void getPipelineDetails() {

    }
}
