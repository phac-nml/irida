package ca.corefacility.bioinformatics.irida.jackson2.mixin;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * This mixin class is only used by the rest api for {@link Sample}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true, value = { "defaultSequencingObject", "defaultGenomeAssembly " })
public abstract class SampleMixin {

}
