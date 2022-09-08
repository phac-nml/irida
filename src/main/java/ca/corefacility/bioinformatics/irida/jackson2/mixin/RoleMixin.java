package ca.corefacility.bioinformatics.irida.jackson2.mixin;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.oauth2.IridaOAuth2AuthorizationService;

import com.fasterxml.jackson.annotation.*;

/**
 * This mixin class is used to serialize/deserialize {@link Role}.
 * <p>
 * This is used to by the {@link IridaOAuth2AuthorizationService}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RoleMixin {
    @JsonCreator
    public RoleMixin(@JsonProperty("name") String name) {
    }
}
