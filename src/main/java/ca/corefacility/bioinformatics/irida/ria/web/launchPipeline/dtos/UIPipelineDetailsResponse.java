package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavedPipelineParameters;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references.UIReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.InputWithOptions;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;

/**
 * Model class to send details about a workflow pipeline to the UI Used on the launch pipeline page.
 */
public class UIPipelineDetailsResponse extends AjaxResponse {
    private String name;
    private String description;
    private String type;
    private String updateSamples;
    private boolean requiresReference;
    private List<SavedPipelineParameters> savedPipelineParameters;
    private List<InputWithOptions> parameterWithOptions;
    private List<UIReferenceFile> referenceFiles;
    private boolean acceptsSingleSequenceFiles;
    private boolean acceptsPairedSequenceFiles;
    private boolean acceptsGenomeAssemblies;
    private List<InputWithOptions> dynamicSources;
    private List<SelectOption> projects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<InputWithOptions> getParameterWithOptions() {
        return parameterWithOptions;
    }

    public void setParameterWithOptions(List<InputWithOptions> parameterWithOptions) {
        this.parameterWithOptions = parameterWithOptions;
    }

    public List<SavedPipelineParameters> getSavedPipelineParameters() {
        return savedPipelineParameters;
    }

    public void setSavedPipelineParameters(List<SavedPipelineParameters> savedPipelineParameters) {
        this.savedPipelineParameters = savedPipelineParameters;
    }

    public boolean isRequiresReference() {
        return requiresReference;
    }

    public void setRequiresReference(boolean requiresReference) {
        this.requiresReference = requiresReference;
    }

    public List<UIReferenceFile> getReferenceFiles() {
        return referenceFiles;
    }

    public void setReferenceFiles(List<UIReferenceFile> referenceFiles) {
        this.referenceFiles = referenceFiles;
    }

    public String getUpdateSamples() {
        return updateSamples;
    }

    public void setUpdateSamples(String updateSamples) {
        this.updateSamples = updateSamples;
    }

    public boolean isAcceptsSingleSequenceFiles() {
        return acceptsSingleSequenceFiles;
    }

    public void setAcceptsSingleSequenceFiles(boolean acceptsSingleSequenceFiles) {
        this.acceptsSingleSequenceFiles = acceptsSingleSequenceFiles;
    }

    public boolean isAcceptsPairedSequenceFiles() {
        return acceptsPairedSequenceFiles;
    }

    public void setAcceptsPairedSequenceFiles(boolean acceptsPairedSequenceFiles) {
        this.acceptsPairedSequenceFiles = acceptsPairedSequenceFiles;
    }

    public boolean isAcceptsGenomeAssemblies() {
        return acceptsGenomeAssemblies;
    }

    public void setAcceptsGenomeAssemblies(boolean acceptsGenomeAssemblies) {
        this.acceptsGenomeAssemblies = acceptsGenomeAssemblies;
    }

    public List<InputWithOptions> getDynamicSources() {
        return dynamicSources;
    }

    public void setDynamicSources(List<InputWithOptions> dynamicSources) {
        this.dynamicSources = dynamicSources;
    }

    public List<SelectOption> getProjects() {
        return projects;
    }

    public void setProjects(List<SelectOption> projects) {
        this.projects = projects;
    }
}