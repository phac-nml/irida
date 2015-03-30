---
layout: default
---

IRIDA Pipeline Development
==========================

This document describes the necessary steps for integrating new pipelines into IRIDA.

* This comment becomes the table of contents.
{:toc}

Introduction
------------

Pipelines in IRIDA take as input data managed by IRIDA and run through a collection of tools to produce some meaningful result.  Pipelines are implemented as a [Galaxy Workflow][] and executed using an instance of [Galaxy][] that has been setup for IRIDA.  Pipelines are versioned and are stored and distributed along with the IRIDA software.  Tools used by a pipeline are versioned and are stored and distributed using [Galaxy Toolsheds][].  In particular, the [Galaxy Main Toolshed][] and the [IRIDA Toolshed][] are used to store and distribute tools for a pipeline.

![irida-pipelines][]

Currently, there are two pipelines integrated into IRIDA:

1. A pipeline for constructing whole genome phylogenies.
2. A pipeline for performing *de novo* assembly and annotation on genomes.

IRIDA provides support for developing and integrating additional pipelines from Galaxy.  This process can be divided into two stages: **Galaxy Workflow Development** and **IRIDA Integration**.  The necessary steps, in brief, are:

1. Galaxy Workflow Development
    1. Develop a Galaxy Workflow.
    2. Upload dependency tools to a [Galaxy Toolshed][Galaxy Toolsheds] and write instructions on how to install the tools.
2. IRIDA Integration
    1. Develop a data model to store results of a workflow in the IRIDA database.
    2. Export the Galaxy workflow to a file and write an IRIDA workflow description file for this workflow.
    3. Update messages and other files in order to integrate the pipeline into the IRIDA UI.

Galaxy Workflow Development
---------------------------

Galaxy provides the ability to organize different bioinformatics tools together into a single workflow for producing specific results.  These workflows can make use of already existing bioinformatics tools in Galaxy, or can include customized tools which can be distributed using a [Galaxy Toolshed][Galaxy Toolsheds].

### 1. Develop a Galaxy Workflow

Galaxy provides a built-in editor for constructing and modifying workflows.

![galaxy-workflow-editor][]

This editor allows for the definition of input files and file types, tools and parameter settings for the tools, as well as which files will be used as output from the workflow.  More information on constructing Galaxy workflows can be found in the [Galaxy Workflow Editor][] documentation.

In order for a workflow to properly be integrated into IRIDA, the input and output to this workflow must be in a specific format.

#### A. Input Format

IRIDA currently only supports two types of input files: a collection of **paired-end sequence reads** in *FASTQ* format, and an optional **reference genome** in FASTA format.

For the **paired-end sequence reads** this must be a dataset collection of type **list:paired**.

![sequence-reads-input-editor][]

For the optional **reference genome**, if you wish to use a reference genome, the type must be an **input dataset**, not a dataset collection.

![reference-input-editor][]

Please also make note of the names given to each input dataset, in this case *sequence_reads_paired* and *reference*, as the names will be used to link up data sent from IRIDA to the Galaxy workflow.

#### B. Output Format

Output datasets within IRIDA can be of any file type and there can be many outputs for each workflow.  Each output should have a consistent name which will be used by IRIDA to find and download the appropriate file from Galaxy.  This can be accomplished by adding a **Rename Dataset** action to each output file.  In this case, for the tool **PhyML** the name is **phylogeneticTree.tre**.

![output-editor][]

In addition, each output dataset should be marked as a **workflow output** by selecting the asterix `*` icon, in this case both the **output_tree** and the **csv** files from the PhyML and SNP Matrix tools have been selected as output.

### 2. Upload dependency tools to a Galaxy Toolshed

If the workflow being developed includes custom tools that do not already exist in Galaxy these tools should be uploaded to a [Galaxy ToolShed][Galaxy Toolsheds] to allow for distribution of this workflow.

IRIDA Integration
-----------------

### 1. Pipeline Data Model

### 2. Write an IRIDA workflow description file

### 3. Additional IRIDA updates

A few other smaller steps need to be taken before the workflow is properly integrated into IRIDA.  These include the following.

#### A. Adding a default workflow entry

The file `src/main/resources/ca/corefacility/bioinformatics/irida/config/workflows.properties` defines the default workflows associated with a particular analysis pipeline.  This is in the format of `irida.workflow.default.[workflow type]=[workflow_id]`.  Please fill in the **[workflow_type]** and **[workflow_id]** entries for your specific workflow and add this line to the `workflows.properties` file.  For example:

```
irida.workflow.default.phylogenomics=ccca532d-b0be-4f2c-bd6d-9886aa722571
```

#### B. Adding messages for the UI

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
[Galaxy Toolsheds]: https://wiki.galaxyproject.org/ToolShed
[Galaxy Main Toolshed]: https://toolshed.g2.bx.psu.edu/
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[galaxy-workflow-editor]: images/galaxy-workflow-editor.png
[irida-pipelines]: images/irida-pipelines.png
[Galaxy Workflow]: https://wiki.galaxyproject.org/Learn/AdvancedWorkflow
[Galaxy Workflow Editor]: https://wiki.galaxyproject.org/Learn/AdvancedWorkflow/BasicEditing
[reference-input-editor]: images/reference-input-editor.png
[sequence-reads-input-editor]: images/sequence-reads-input-editor.png
[output-editor]: images/output-editor.png
