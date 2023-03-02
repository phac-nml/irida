package ca.corefacility.bioinformatics.irida.jackson2.mixin;

import java.sql.Timestamp;

import ca.corefacility.bioinformatics.irida.oauth2.IridaOAuth2AuthorizationService;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.TimestampDeserializer;

/**
 * This mixin class is used to serialize/deserialize {@link Timestamp}.
 * <p>
 * This is used to by the {@link IridaOAuth2AuthorizationService}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = TimestampDeserializer.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TimestampMixin {

}
