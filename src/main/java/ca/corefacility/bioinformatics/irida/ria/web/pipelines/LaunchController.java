package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/launch")
public class LaunchController {
    private UICartService cartService;

    // Setters

    @Autowired
    public void setCartService(UICartService cartService) {
        this.cartService = cartService;
    }


    /**
     * Mapping for the pipeline launch page.
     * @return The path to the launch page html file.
     */
    @GetMapping("")
    public String getPipelineLaunchPage(Model model) {
        if(cartService.isCartEmpty()) {
            // User cannot launch a pipeline if the cart is empty.
            return "redirect:/cart/pipelines";
        }
        model.addAttribute("entry", "launch");
        return "entry";
    }
}
