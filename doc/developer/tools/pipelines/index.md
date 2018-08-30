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

Pipelines in IRIDA take as input data managed by IRIDA and run through a collection of tools to produce some meaningful result.  Pipelines are implemented as a [Galaxy Workflow][] and executed using an instance of [Galaxy][] that has been setup for IRIDA.  Pipelines are versioned and are stored and distributed either along with the IRIDA software, or as a separate plugin compiled into a Java JAR file.  Tools used by a pipeline are versioned and are stored and distributed using [Galaxy Toolsheds][].  In particular, the [Galaxy Main Toolshed][] and the [IRIDA Toolshed][] are used to store and distribute tools for a pipeline.

![irida-pipelines][]

IRIDA provides support for developing and integrating additional pipelines from Galaxy.  This process can be divided into two stages: **Galaxy Workflow Development** and **IRIDA Integration**.  The necessary steps, in brief, are:

1. Galaxy Workflow Development
    1. Develop a Galaxy Workflow
    2. Upload dependency tools to a Galaxy Toolshed
    3. Export Workflow
2. IRIDA Integration
    1. Write IRIDA workflow files (or run [irida-wf-ga2xml][])
    2. Write IRIDA workflow plugin
    3. Build plugin JAR and move to `/etc/irida/plugins` directory
    4. Start IRIDA

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

If the workflow being developed includes custom tools that do not already exist in Galaxy these tools should be uploaded to a [Galaxy ToolShed][Galaxy Toolsheds] to allow for distribution of this workflow.  This should be done before building and exporting the final workflow __*.ga__ file, since the ids of each tool in the Galaxy workflow include the name of the toolshed.  For example, the id for Prokka, which is used for annotation of genomes, is `toolshed.g2.bx.psu.edu/repos/crs4/prokka/prokka/1.4.0`, which includes the name of the toolshed where Prokka can be found <https://toolshed.g2.bx.psu.edu/>.

More information on developing a tool for Galaxy can be found in the [Galaxy Tool Development][] documentation.

### 3. Export Workflow

Once the workflow is written in Galaxy, it can be exported to a file by going to the **Workflow** menu at the top, finding your particular workflow and selecting **Download or Export**.  This will save the workflow as a __*.ga__ file, which is a JSON-formatted file defining the tools, tool versions, and structure of the workflow.

![export-workflow][]

IRIDA Integration
-----------------

### 1. Write IRIDA workflow files

IRIDA makes use of two files `irida_workflow.xml` and `irida_workflow_structure.ga` to define the workflow and associated metadata about the workflow.

The easiest way to generate these files is to make use of the [irida-wf-ga2xml][] program. Assuming you already have a `galaxy_workflow.ga` file exported from the steps above, you can generate the necessary additional files for IRIDA with:

```bash
java -jar irida-wf-ga2xml-1.0.0-SNAPSHOT-standalone.jar \
  -i galaxy_workflow.ga \
  -n WORKFLOW_NAME \
  -t ANALYSIS_TYPE \
  -W WORKFLOW_VERSION \
  -o output
```

This will build the necesary files under `output/`.  As an example:

```bash
java -jar irida-wf-ga2xml-1.0.0-SNAPSHOT-standalone.jar -i src/main/resources/ca/corefacility/bioinformatics/irida/model/workflow/analysis/type/workflows/SISTRTyping/0.3/irida_workflow_structure.ga -n SISTRTyping -t SISTR_TYPING -W 0.1.0 -o output
```

This will produce the following directory structure:

```
output
└── SISTRTyping
    └── 0.1.0
        ├── irida_workflow_structure.ga
        ├── irida_workflow.xml
        └── messages_en.properties
```

*NOTE: You may need to edit the output from [irida-wf-ga2xml][] to ensure that only necessary tool parameters are kept in the **irida_workflow.xml** file and that the proper tool revision is used for each tool if this information is not embedded in your Galaxy Workflow `ga` file.*

### 2. Write IRIDA workflow plugin

IRIDA includes a mechanism for packaging up all the above workflow files into a single JAR file which can be distributed and installed independently of the main IRIDA software. To package up the IRIDA workflow into a JAR file you can start with a template plugin located at [github plugin].



#### C. Move Workflow Definition

In order for IRIDA to automatically load up the workflow definition files, the entire directory structure for `MyPipeline` should be moved to `src/main/resources/ca/corefacility/bioinformatics/irida/model/enums/workflows/MyPipeline`.  So, this should look like:

```
src/main/resources/ca/corefacility/bioinformatics/irida/model/enums/workflows/MyPipeline
└── 0.1
    ├── irida_workflow_structure.ga
    └── irida_workflow.xml
```

### 3. Additional IRIDA Updates

A few other smaller steps need to be taken before the workflow is properly integrated into IRIDA.  These include the following.

#### A. Adding a default workflow entry

The file `src/main/resources/ca/corefacility/bioinformatics/irida/config/workflows.properties` defines the default workflows associated with a particular analysis pipeline type.  This is in the format of **irida.workflow.default.[analysis_type]=[analysis_id]**.  Please fill in the **[analysis_type]** and **[analysis_id]** entries for your specific workflow and add this line to the `workflows.properties` file.  For example:

```
irida.workflow.default.mypipeline=49507566-e10c-41b2-ab6f-0fb5383be997
```

In this case, the **[analysis_type]** is *mypipeline*, which comes from the `<analysisType>` XML tag from the workflow description file.  The **[analysis_id]** is *49507566-e10c-41b2-ab6f-0fb5383be997*, which comes from the `<id>` XML tag from the workflow description file.

#### B. Adding messages for the UI

Some messages need to be defined in order to display the pipeline in the UI.  These are stored in the file `src/main/resources/i18n/messages_en.properties` and include messages for the title and description displayed in the UI as well as messages for each workflow tool parameter (each `<parameter name="<parameter_name>" ... />` in `irida_workflow.xml`). 

The format for these messages must be:

```properties
workflow.workflow-analysis-type.title=...
workflow.workflow-analysis-type.description=...

pipeline.title.workflow-directory-name=...
pipeline.h1.workflow-directory-name=...

pipeline.parameters.modal-title.workflow-directory-name=...
# for each parameter defined in irida_workflow.xml
pipeline.parameters.workflow-directory-name.parameter_name=...
```

where 

- `workflow-analysis-type` must be the `AnalysisType` enum you defined in `AnalysisType.java`
- `workflow-directory-name` must be your directory name under `src/main/resources/ca/corefacility/bioinformatics/irida/model/enums/workflows/` (e.g. `MyPipeline` for the above example)
- `parameter_name` is `<parameter_name>` in `<parameter name="<parameter_name>"` for each `<parameter/>` in `irida_workflow.xml`

For the above example with **MyPipeline**, the messages would be similar to:

```properties
workflow.mypipeline.title=My Pipeline
workflow.mypipeline.description=Run my custom pipeline.

pipeline.title.MyPipeline=Pipelines - My Pipeline
pipeline.h1.MyPipeline=My Pipeline

pipeline.parameters.modal-title.mypipeline=My Pipeline Parameters
pipeline.parameters.mypipeline.my_parameter=My Parameter Description.
pipeline.parameters.mypipeline.other_parameter=Other Parameter Description.
```


#### C. [Optional] Customize the color of your pipeline

Given your `AnalysisType`, add an entry to `src/main/webapp/resources/sass/pages/pipelines-selection.scss` where your `AnalysisType` is the CSS class. For example, with `mypipeline`, you would add: 

```css
.mypipeline {
  background-color: #c8c8c8 !important;
  color: #222 !important;
}
```


### 4. Run IRIDA

Once you've made all the above modifications, you can attempt to load up the pipeline in IRIDA with the command:

```
mvn clean jetty:run
```

This should launch an instance of IRIDA on <http://localhost:8080>. If you log in with **admin** and **password1** you should be able to navigate to the pipelines page, which should now display:

![my-pipeline-irida][]

If you select some samples and attempt to run this pipeline you should see:

![my-pipeline-launch][]

If you attempt to modify the parameters of this pipeline you should see:

![my-pipeline-parameters][]

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
[export-workflow]: images/export-workflow.png
[AnalysisType]: ../../apidocs/ca/corefacility/bioinformatics/irida/model/enums/AnalysisType.html
[Analysis]: ../../apidocs/ca/corefacility/bioinformatics/irida/model/workflow/analysis/Analysis.html
[AnalysisPhylogenomicsPipeline]: ../../apidocs/ca/corefacility/bioinformatics/irida/model/workflow/analysis/AnalysisPhylogenomicsPipeline.html
[IRIDA Workflow Description]: workflow-description/
[Galaxy Tool Development]: galaxy/
[my-pipeline-irida]: images/my-pipeline-irida.png
[my-pipeline-launch]: images/my-pipeline-launch.png
[my-pipeline-parameters]: images/my-pipeline-parameters.png
[irida-wf-ga2xml]: https://github.com/phac-nml/irida-wf-ga2xml
