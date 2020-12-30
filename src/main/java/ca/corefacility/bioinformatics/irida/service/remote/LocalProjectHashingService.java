package ca.corefacility.bioinformatics.irida.service.remote;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LocalProjectHashingService {
    ProjectService projectService;
    SampleService sampleService;
    SequencingObjectService sequencingObjectService;
    GenomeAssemblyService assemblyService;

    @Autowired
    public LocalProjectHashingService(ProjectService projectService, SampleService sampleService, SequencingObjectService sequencingObjectService, GenomeAssemblyService assemblyService) {
        this.projectService = projectService;
        this.sampleService = sampleService;
        this.sequencingObjectService = sequencingObjectService;
        this.assemblyService = assemblyService;
    }

    @PreAuthorize("hasPermission(#project, 'canReadProject')")
    public Integer getProjectHash(Project project) {

        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(project);

        List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(project);

        for (Join<Project, Sample> j : samplesForProject) {
            Sample sample = j.getObject();

            getSampleHash(sample, builder);
        }

        return builder.toHashCode();

    }

    private void getSampleHash(Sample sample, HashCodeBuilder builder) {
        //add the sample itself
        builder.append(sample);

        //add all the metadata entries
        Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);
        for (MetadataEntry e : metadataForSample) {
            builder.append(e);
        }

        //add all the sequence files
        Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = sequencingObjectService.getSequencingObjectsForSample(sample);
        for (SampleSequencingObjectJoin join : sequencingObjectsForSample) {
            builder.append(join.getObject());
        }

        //add all assemblies
        Collection<SampleGenomeAssemblyJoin> assembliesForSample = assemblyService.getAssembliesForSample(sample);
        for (SampleGenomeAssemblyJoin join : assembliesForSample) {
            builder.append(join.getObject());
        }
    }

}
