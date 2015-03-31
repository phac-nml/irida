---
layout: default
---

Galaxy Tool Development
-----------------------

The first step to getting a tool integrated into IRIDA is to integrate it into Galaxy.  Galaxy tools can include any Linux/UNIX program that is controlled through a command-line interface.  Galaxy is made aware of the tools through the use of a tool configuration file, which is an XML file defining the inputs, parameters, and outputs to the tool.  For a simple tool run like `tool input_file output_file` the XML wrapper would look like:

```xml
<tool id="tool" name="Tool" version="0.0.1">
  <description>Description of tool</description>

  <command>tool $input $output</command>

  <inputs>
    <param format="tabular" name="input" type="data" label="Input file"/>
  </inputs>

  <outputs>
    <data format="tabular" name="output" />
  </outputs>

  <help>
  Instructions on the usage of this tool.
  </help>
</tool>
```

The `<command>tool $input $output</command>` describes how the tool will be run, while the `<inputs></inputs>` and `<outputs></outputs>` can be used to define inputs and outputs to the tool respectively.  A description of the syntax of the tool configuation file can be found in the [Galaxy Tool XML File][] documentation.

Although the tool confiuration file can be written manually, a project [Planemo][] has been started by the Galaxy developers to create a suite of tools to help out with integrating new tools into Galaxy.



[Galaxy]: http://galaxyproject.org/
[Galaxy Tool XML File]: https://wiki.galaxyproject.org/Admin/Tools/ToolConfigSyntax
[Planemo]: https://planemo.readthedocs.org/en/latest/
