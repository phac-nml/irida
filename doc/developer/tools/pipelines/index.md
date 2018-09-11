---
layout: default
---

# IRIDA Pipeline Development

This document describes the necessary steps for integrating new pipelines into IRIDA.

* This comment becomes the table of contents.
{:toc}

# 1. Introduction

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

# 2. Galaxy Workflow Development

Galaxy provides the ability to organize different bioinformatics tools together into a single workflow for producing specific results.  These workflows can make use of already existing bioinformatics tools in Galaxy, or can include customized tools which can be distributed using a [Galaxy Toolshed][Galaxy Toolsheds].

## 2.1. Develop a Galaxy Workflow

Galaxy provides a built-in editor for constructing and modifying workflows.

![galaxy-workflow-editor][]

This editor allows for the definition of input files and file types, tools and parameter settings for the tools, as well as which files will be used as output from the workflow.  More information on constructing Galaxy workflows can be found in the [Galaxy Workflow Editor][] documentation.

In order for a workflow to properly be integrated into IRIDA, the input and output to this workflow must be in a specific format.

### 2.1.1. Input Format

IRIDA currently only supports two types of input files: a collection of **paired-end sequence reads** in *FASTQ* format, and an optional **reference genome** in FASTA format.

For the **paired-end sequence reads** this must be a dataset collection of type **list:paired**.

![sequence-reads-input-editor][]

For the optional **reference genome**, if you wish to use a reference genome, the type must be an **input dataset**, not a dataset collection.

![reference-input-editor][]

Please also make note of the names given to each input dataset, in this case *sequence_reads_paired* and *reference*, as the names will be used to link up data sent from IRIDA to the Galaxy workflow.

### 2.1.2. Output Format

Output datasets within IRIDA can be of any file type and there can be many outputs for each workflow.  Each output should have a consistent name which will be used by IRIDA to find and download the appropriate file from Galaxy.  This can be accomplished by adding a **Rename Dataset** action to each output file.  In this case, for the tool **PhyML** the name is **phylogeneticTree.tre**.

![output-editor][]

In addition, each output dataset should be marked as a **workflow output** by selecting the asterix `*` icon, in this case both the **output_tree** and the **csv** files from the PhyML and SNP Matrix tools have been selected as output.

## 2.2. Upload dependency tools to a Galaxy Toolshed

If the workflow being developed includes custom tools that do not already exist in Galaxy these tools should be uploaded to a [Galaxy ToolShed][Galaxy Toolsheds] to allow for distribution of this workflow.  This should be done before building and exporting the final workflow __*.ga__ file, since the ids of each tool in the Galaxy workflow include the name of the toolshed.  For example, the id for Prokka, which is used for annotation of genomes, is `toolshed.g2.bx.psu.edu/repos/crs4/prokka/prokka/1.4.0`, which includes the name of the toolshed where Prokka can be found <https://toolshed.g2.bx.psu.edu/>.

More information on developing a tool for Galaxy can be found in the [Galaxy Tool Development][] documentation.

## 2.3. Export Workflow

Once the workflow is written in Galaxy, it can be exported to a file by going to the **Workflow** menu at the top, finding your particular workflow and selecting **Download or Export**.  This will save the workflow as a __*.ga__ file, which is a JSON-formatted file defining the tools, tool versions, and structure of the workflow.

![export-workflow][]

# 3. IRIDA Integration

## 3.1. Write IRIDA workflow files

IRIDA makes use of three files `irida_workflow.xml`, `irida_workflow_structure.ga`, and `messages_en.properties` to define the workflow and associated metadata about the workflow.

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

## 3.2. Write IRIDA workflow plugin

IRIDA includes a mechanism for packaging up all the above workflow files into a single JAR file which can be distributed and installed independently of the main IRIDA software. To package up the IRIDA workflow into a JAR file you can start with a template plugin located in [irida-example-plugin][]. An overview of the changes you will need to make is as follows.

### 3.2.1. Copy workflow files above from `output/` to [src/main/resources/workflows][workflows-dir]

You will need to copy over the files generated above to `src/main/resources/workflows`. The directory structure you should end up with will be:

```
workflows/
└── 0.1.0
    ├── irida_workflow_structure.ga
    ├── irida_workflow.xml
    └── messages_en.properties
```

* The directory `0.1.0` corresponds to all files for a particular version of a pipeline (in this case `0.1.0`). Previous versions of the pipeline should each be kept in their own numbered directory (e.g., `0.1.0`, `0.2.0`) so that IRIDA can load up information about these pipelines.
* The file `irida_workflow_structure.ga` is a [Galaxy][] workflow file which is uploaded to a Galaxy instance by IRIDA before execution.
* The file `irida_workflow.xml` contains information about this particular pipeline used by IRIDA.
* The file `messages_en.properties` contains messages which will be displayed in the IRIDA UI.

You may (or may not) need to modify these files from the automatically generated versions. A description of each of these files is as follows.

#### 3.2.1.1. `irida_workflow_structure.ga`

The `irida_workflow_structure.ga` file contains the workflow structure (generated from [Galaxy][]). An example of the format is as follows:

```json
{
    "a_galaxy_workflow": "true", 
    "annotation": "", 
    "format-version": "0.1", 
    "name": "Read info", 
    "steps": {
        "0": {
            "annotation": "", 
            "content_id": null, 
            "id": 0, 
            "input_connections": {}, 
            "inputs": [
                {
                    "description": "", 
                    "name": "sequence_reads"
                }
            ], 
            "label": null, 
            "name": "Input dataset collection", 
            "outputs": [], 
            "position": {
                "left": 200, 
                "top": 200
            }, 
            "tool_errors": null, 
            "tool_id": null, 
            "tool_state": "{\"collection_type\": \"list:paired\", \"name\": \"sequence_reads\"}", 
            "tool_version": null, 
            "type": "data_collection_input", 
            "uuid": "e267248b-6ddc-4b5e-a476-e805ce1bc4d5", 
            "workflow_outputs": [
                {
                    "label": null, 
                    "output_name": "output", 
                    "uuid": "ae59d874-94ab-4b14-b456-15e2fee2860d"
                }
            ]
        },
...
```

Normally you will not be required to modify this file.

#### 3.2.1.2. `irida_workflow.xml`

This file contains information about the particular pipeline installed in IRIDA. An example would be:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<iridaWorkflow>
  <id>79d90ca8-00ae-441b-b5c7-193c9e85a968</id>
  <name>ReadInfo</name>
  <version>0.1.0</version>
  <analysisType>READ_INFO</analysisType>
  <inputs>
    <sequenceReadsPaired>sequence_reads</sequenceReadsPaired>
    <requiresSingleSample>true</requiresSingleSample>
  </inputs>
  <parameters>
    <parameter name="Grep1-4-pattern" defaultValue="^@">
      <toolParameter toolId="Grep1" parameterName="pattern"/>
    </parameter>
  </parameters>
  <outputs>
    <output name="hash.txt" fileName="hash.txt"/>
    <output name="read-count.txt" fileName="read-count.txt"/>
  </outputs>
  <toolRepositories/>
</iridaWorkflow>
```

Normally this file will be properly auto-generated for you. A few key elements are:

1. `<id>` defines a unique id for the workflow.  This must be a UUID.  A quick way to generate a random UUID on linux is the command `uuid -v 4`.
2. `<analysisType>` defines what type of analysis this workflow belongs to.  This string should match the string defined for the `AnalysisType` in the Java plugin class defined below.
2. `<sequenceReadsPaired>` defines the name of the input dataset in Galaxy for the paired-end sequence reads chosen previously.  In this case it is *sequence_reads*.
3. `<toolParameter>` defines how to map parameters a user selects in IRIDA to those in Galaxy (defined in the **irida_workflow_structure.ga** file).
4. `<output>` defines, for an output file, a name in IRIDA and maps it to the name of the file in Galaxy that was chosen previously.  In this case it is *hash.txt* and *read-count.txt*.
5. `<toolRepositories>` defines the different Galaxy ToolSheds from which the dependency tools come from, as well as a revision number for the tool.

Additional details and a description of the syntax of this file can be found in the [IRIDA Workflow Description][] documentation.

#### 3.2.1.3. `messages_en.properties`

This file contains information on the text to display in the IRIDA UI for each pipeline (specifically the *en* or English text, other languages can be stored in other `messages_xx.properties` files). An example of this file is:

```properties
pipeline.parameters.modal-title.readinfo=ReadInfo Pipeline Parameters
workflow.READ_INFO.title=ReadInfo Pipeline
pipeline.h1.ReadInfo=ReadInfo Pipeline
pipeline.title.ReadInfo=Pipelines - ReadInfo
workflow.READ_INFO.description=

pipeline.parameters.readinfo.Grep1-4-invert=Grep1-4-invert
pipeline.parameters.readinfo.Grep1-4-pattern=Grep1-4-pattern

pipeline.parameters.readinfo.wc_gnu-5-include_header=wc_gnu-5-include_header
```

The entries like `workflow.READ_INFO.title=ReadInfo Pipeline` contain the text used to display the pipeline entry on the "Pipelines" page in the UI:

![irida-pipelines][]

The entries like `pipeline.parameters.readinfo.Grep1-4-pattern=Grep1-4-pattern` contain information used to display the text when adjusting pipeline parameters:

![pipeline-parameters][]

The `Grep1-4-pattern` part corresponds to the **name** attribute under a `<parameter>` entry in the **irida_workflow.xml** file:

```xml
<parameter name="Grep1-4-pattern" defaultValue="^@">
  <toolParameter toolId="Grep1" parameterName="pattern"/>
</parameter>
```

### 3.2.2. Write a `Plugin.java` class defining some key properties of the pipeline



## 3.3. Run IRIDA

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
[IRIDA Workflow Description]: workflow-description/
[Galaxy Tool Development]: galaxy/
[my-pipeline-irida]: images/my-pipeline-irida.png
[my-pipeline-launch]: images/my-pipeline-launch.png
[my-pipeline-parameters]: images/my-pipeline-parameters.png
[irida-wf-ga2xml]: https://github.com/phac-nml/irida-wf-ga2xml
[irida-example-plugin]: https://github.com/phac-nml/irida-example-plugin
[workflows-dir]: https://github.com/phac-nml/irida-example-plugin/tree/development/src/main/resources/workflows
[pipeline-parameters]: images/pipeline-parameters.png