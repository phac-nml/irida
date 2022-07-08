package ca.corefacility.bioinformatics.irida.jackson2.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "links", "systemRole" })
public abstract class UserMixin {

}
