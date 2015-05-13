---
layout: default
---

IRIDA Workflow Description
==========================

This file describes the IRIDA Workflow Description format.  This is an **XML** format that is used to link up a Galaxy workflow into IRIDA and provide IRIDA with the necessary information on the input datasets for Galaxy, parameters, and output files to save.

* This comment becomes the table of contents.
{:toc}


Workflow Description XML Tag Details
====================================

`<iridaWorkflow>`
----------------

The outer-most tag. All other tags are contained within an `<iridaWorkflow>`.

### Attributes

None.

### Example

```xml
<iridaWorkflow>
</iridaWorkflow>
```

`<id>`
-----

An ID for the workflow which is used to uniquely identify this exact workflow.  This should be a [UUID][].  A quick way to generate a random (version 4) UUID is with the `uuid` command, as in `uuid -v 4`.

### Attributes

None.

### Example

```xml
<id>79872edb-4f3b-4a51-aff7-33ddbc05ec5e</id>
```

`<name>`
-------

The name of the workflow.

### Attibutes

None.

### Example

```xml
<name>MyPipeline</name>
```

`<version>`
-----------

A version number for the workflow.  This should be incremented on every modification to the workflow.

### Attributes

None.

### Example

```xml
<version>1.0</version>
```

`<analysisType>`
----------------

The particular type or class of analysis this workflow belongs to.  This must be one of the types defined in the `AnalysisType` enum in IRIDA.  Please see the [AnalysisType JavaDoc][] for the valid types.

### Attributes

None.

### Example

```xml
<analysisType>phylogenomics</analysisType>
```

`<inputs>`
----------

A description of the different types of inputs to this workflow.

### Attributes

None.

### Example

```xml
<inputs>
	<sequenceReadsPaired>sequence_reads_paired</sequenceReadsPaired>
</inputs>
```

`<sequenceReadsPaired>`
-----------------------

An optional tag contained in the `<inputs>` tag set.  This defines the name, if any, of a list of paired-end files used as input to the Galaxy workflow.

### Attributes

None.

### Example

<sequenceReadsPaired>sequence_reads_paired</sequenceReadsPaired>

`<sequenceReadsSingle>`
-------------

An optional tag contained in the `<inputs>` tag set.  This defines the name, if any, of a list of single-end files used as input to the Galaxy workflow.

### Attributes

None.

### Example

```xml
<sequenceReadsSingle>sequence_reads_single</sequenceReadsSingle>
```

`<reference>`
-------------

An optional tag contained in the `<inputs>` tag set.  This defines the name, if any, of the reference input for the Galaxy workflow.

### Attributes

None.

### Example

```xml
<reference>reference</reference>
```

`<requiresSingleSample>`
-------------

An optional tag contained in the `<inputs>` tag set.  This defines whether or not this workflow only operates on a single sample `true` or can handle multiple samples `false`.

### Attributes

None.

### Example

```xml
<requiresSingleSample>false</requiresSingleSample>
```

`<parameters>`
-------------

This defines the set of parameters for a workflow as well as how to map these parameters to Galaxy tool names and versions.

### Attributes

None.

### Example

```xml
<parameters>
</parameters>
```

`<parameter>`
-------------

Contained in the `<parameters>` tag.  This defines a single parameter for a workflow.

### Attributes

| attribute         | type   | details                                                                                                                 | required | example            |
|-------------------|--------|-------------------------------------------------------------------------------------------------------------------------|----------|--------------------|
| name              | string | The name of the parameter.  This will be used in the IRIDA database and configuration files to refer to this parameter. | yes      | name="myparameter" |
| defaultValue      | string | The default value of the parameter.                                                                                     | yes      | defaultValue="1"   |

### Example

```xml
<parameter name="myparameter" defaultValue="1">
</parameter>
```

`<toolParameter>`
-----------------

Contained in the `<parameter>` tag.  This defines a parameter in a Galaxy tool to map to from the parent `<parameter>` definition.

### Attributes

| attribute         | type   | details                                                    | required | example                                |
|-------------------|--------|------------------------------------------------------------|----------|----------------------------------------|
| toolId            | string | The id of the tool in Galaxy of the parameter to override. | yes      | toolId="my_galaxy_tool"                |
| parameterName     | string | The name of the parameter in the Galaxy tool to override.  | yes      | parameterName="parameter.section.name" |

### Example

```xml
<toolParameter toolId="my_galaxy_tool" parameterName="parameter.section.name" />
```

`<outputs>`
-----------

Used to define a list of output files from the Galaxy workflow which will be saved back into IRIDA.

### Attributes

None.

### Example

```xml
<outputs>
</outputs>
```

`<output>`
----------

Contained in the `<outputs>` tag.  Defines a particular output file from a Galaxy workflow.

### Attributes

| attribute | type   | details                                                                                                                                        | required | example                    |
|-----------|--------|------------------------------------------------------------------------------------------------------------------------------------------------|----------|----------------------------|
| name      | string | A label for the output file name.  This is used internally to map to a particular output file in the IRIDA database.                           | yes      | name="my-output-1"         |
| fileName  | string | The name of an output file from the Galaxy workflow.  This is used to find the particular output file when transferring results back to IRIDA. | yes      | fileName="output-file.txt" |

### Example

```xml
<output name="my-output-1" fileName="output-file.txt" />
```

`<toolRepositories>`
--------------------

Defines a list of the repositories storing the dependency Galaxy tools for this workflow.  This is used to keep track of the particular versions of dependencies and can also be used for integration testing with Galaxy.

### Attributes

None.

### Example

```xml
<toolRepositories>
</toolRepositories>
```

`<repository>`
--------------

Contained within a `<toolRepositories>` tag.  Defines a particular repository storing a dependency for the workflow.

### Attributes

None.

### Example

```xml
<repository>
</repository>
```

`<name>`
----------

Contained in the `<repository>` tag.  Defines the name of the tool repository.

### Attributes

None.

### Example

```xml
<name>my_tool</name>
```

`<owner>`
----------

Contained in the `<repository>` tag.  Defines the owner of the tool repository.

### Attributes

None.

### Example

```xml
<owner>irida</owner>
```

`<url>`
----------

Contained in the `<repository>` tag.  Defines the url of the tool repository.

### Attributes

None.

### Example

```xml
<url>https://irida.corefacility.ca/galaxy-shed</url>
```

`<revision>`
----------

Contained in the `<repository>` tag.  Defines the revision number of tool.

### Attributes

None.

### Example

```xml
<revision>de3e46eaf5ba</revision>
```

Workflow Description Example
============================

An example workflow description XML file is given below.

```xml
<?xml version="1.0" encoding="UTF-8"?>

<iridaWorkflow>
        <id>49507566-e10c-41b2-ab6f-0fb5383be997</id>
        <name>MyPipeline</name>
        <version>0.1</version>
        <analysisType>mypipeline</analysisType>
        <inputs>
                <sequenceReadsPaired>sequence_reads_paired</sequenceReadsPaired>
                <reference>reference</reference>
                <requiresSingleSample>false</requiresSingleSample>
        </inputs>
        <parameters>
                <parameter name="my_parameter" defaultValue="1">
                        <toolParameter
                                toolId="my_tool"
                                parameterName="my_parameter" />
                </parameter>
        </parameters>
        <outputs>
                <output name="tree" fileName="phylogeneticTree.tre" />
        </outputs>
        <toolRepositories>
                <repository>
                        <name>my_tool</name>
                        <owner>irida</owner>
                        <url>https://irida.corefacility.ca/galaxy-shed</url>
                        <revision>de3e46eaf5ba</revision>
                </repository>
        </toolRepositories>
</iridaWorkflow>
```

[UUID]: http://en.wikipedia.org/wiki/Universally_unique_identifier
[AnalysisType JavaDoc]: ../../../apidocs/ca/corefacility/bioinformatics/irida/model/enums/AnalysisType.html
