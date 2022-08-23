package ca.corefacility.bioinformatics.irida.jackson2.mixin;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.oauth2.IridaOAuth2AuthorizationService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * This mixin class is used to serialize/deserialize {@link User}.
 * <p>
 * This is used to by the {@link IridaOAuth2AuthorizationService}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "links", "systemRole" })
public abstract class UserMixin {

}
