---
layout: default
---

IRIDA Pipeline Development
==========================

Pipelines in IRIDA are a collection of tools organized into a workflow and executed using Galaxy on whole genome sequence data managed by IRIDA.

![irida-pipelines][]

Currently, there are two pipelines integrated into IRIDA:

1. A pipeline for constructing whole genome phylogenies.
2. A pipeline for performing *de novo* assembly and annotation on genomes.

IRIDA provides support for integrating additional pipelines.  The necessary steps, in brief, are:

1. Develop a Galaxy workflow and instructions on how to install all necessary tools in Galaxy.
3. Develop a data model to store results of a workflow in the IRIDA database.
2. Export the Galaxy workflow to a file and write an IRIDA workflow description file for this workflow.
4. Update messages and other files in order to integrate the pipeline into the IRIDA UI.

Developing a Galaxy Workflow
----------------------------

Galaxy provides the ability to organized different bioinformatics tools together into a single workflow for producing specific results.  These workflows can be saved and uploaded into separate Galaxy instances, providing a mechanism for sharing workflows.  In addition, Galaxy provides an editor for developing workflows which looks as follows.

![galaxy-workflow-editor][]

Building an IRIDA Analysis data model
-------------------------------------

Writing an IRIDA workflow description file
------------------------------------------

Additional IRIDA updates
------------------------

A few other smaller steps need to be taken before the workflow is properly integrated into IRIDA.  These include the following.

### Adding a default workflow entry

The file `src/main/resources/ca/corefacility/bioinformatics/irida/config/workflows.properties` defines the default workflows associated with a particular analysis pipeline.  This is in the format of `irida.workflow.default.[workflow type]=[workflow_id]`.  Please fill in the **[workflow_type]** and **[workflow_id]** entries for your specific workflow and add this line to the `workflows.properties` file.  For example:

```
irida.workflow.default.phylogenomics=ccca532d-b0be-4f2c-bd6d-9886aa722571
```

### Adding messages for the UI

Some messages need to be defined in order to display the pipeline in the UI.  These are stored in the file `src/main/resources/i18n/messages_en.properties` and include messages for the title and description displayed in the UI as well as error messages for each parameter.  This should look similar to:

```
workflow.assembly-annotation.title=Assembly and Annotation Pipeline
workflow.assembly-annotation.description=Generate an assembled and annotated genome from the reads within a sample.

pipeline.parameters.assemblyannotation.assembly-kmers=Comma-separated k-mer values to use for assembly with SPAdes
pipeline.parameters.assemblyannotation.assembly-contig-min-length=Minimum contig length to keep from an assembly
pipeline.parameters.assemblyannotation.assembly-contig-min-coverage=Minimum contig coverage to keep from an assembly
pipeline.parameters.assemblyannotation.annotation-similarity-e-value-cutoff=The e-value cutoff for annotation with Prokka
```

[Galaxy]: http://galaxyproject.org/
[galaxy-workflow-editor]: <F5>
[irida-pipelines]: images/irida-pipelines.png
[Galaxy Advanced Workflow]: https://wiki.galaxyproject.org/Learn/AdvancedWorkflow
