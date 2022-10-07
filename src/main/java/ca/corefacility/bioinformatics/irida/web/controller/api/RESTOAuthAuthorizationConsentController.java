package ca.corefacility.bioinformatics.irida.web.controller.api;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Joiner;

/**
 * Controller class for serving custom OAuth2 authorization consent pages
 */
@Controller
public class RESTOAuthAuthorizationConsentController {

    /**
     * Basic authorization consent controller for OAuth2
     * 
     * @param principal The princiapl user making the auth request
     * @param model     Model objects to be passed to the view
     * @param clientId  The clientId
     * @param scope     The requested scopes
     * @param state     The state object from the auth request
     * @return
     */
    @GetMapping("/api/oauth/consent")
    public ModelAndView consent(Principal principal, Map<String, Object> model,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.STATE) String state) {

        String[] requestedScopes = StringUtils.delimitedListToStringArray(scope, " ");

        model.put("clientId", clientId);
        model.put("state", state);
        model.put("requestedScopes", Joiner.on(" & ").join(requestedScopes));
        model.put("scopes", requestedScopes);
        model.put("principalName", principal.getName());

        return new ModelAndView("oauth/authorization_consent", model);
    }
}
