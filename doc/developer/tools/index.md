---
layout: default
---

IRIDA Tool Development
======================

Tools in IRIDA are any piece of software which uses as input data managed by IRIDA to produce a meaningful result.  Tools can be divided into two categories:

Internal Tools
--------------

![irida-pipelines][]

These are tools that are packaged up along with IRIDA and run internal to the IRIDA system.  They are organized into pipelines which take as input data managed by IRIDA and run a collection of tools to produce some result.  Pipelines are implemented as a [Galaxy Workflow][] and executed using an instance of [Galaxy][] which is installed alongside the IRIDA web interface.

For more information on developing these tools, please refer to the [IRIDA Pipeline Development][] guide.

External Tools
--------------

These are tools that are not packaged up along with IRIDA but that can interact with IRIDA to get access to IRIDA-managed data.  This includes desktop applications or web servers for sophisticated analyses which cannot easily be integrated into Galaxy. Integration of such tools can be performed via the [IRIDA REST API][].

[Galaxy]: http://galaxyproject.org/
[Galaxy Workflow]: https://wiki.galaxyproject.org/Learn/AdvancedWorkflow
[IRIDA Pipeline Development]: pipelines/
[irida-pipelines]: pipelines/images/irida-pipelines.png
[IRIDA REST API]: ../rest/
