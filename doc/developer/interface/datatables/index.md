---
layout: default
---

This guide describes the datatable development on the IRIDA Platform

* This comment becomes the table of contents
{:toc}

HTML Table Layout
=================

(Snippet from `/src/main/webapp/pages/projects/project_samples.html`)
```html
<table id="samplesTable" class="table table-striped" cellspacing="0" width="100%"
       data:url="@{/projects/{id}/ajax/samples(id=${project.getId()})}">
    <thead>
    <tr>
        <th data-orderable="false"></th>
        <th data-data="sampleName" data-name="sample.sampleName" th:text="#{project.samples.table.name}"></th>
        <th data-data="organism" data-name="sample.organism" th:text="#{project.samples.table.organism}"></th>
        <th data-data="projectName" data-name="project.name" th:text="#{project.samples.table.project}"></th>
        <th class="dt-date" data-data="createdDate" th:text="#{project.samples.table.created}"></th>
        <th class="dt-date" data-data="modifiedDate" data-name="sample.modifiedDate"
            th:text="#{project.samples.table.modified}"></th>
    </tr>
    </thead>
</table>
```

* On the `table` element:
    - an `id` attribute is required.
    - `data:url="@{}"` for the ajax request url for the table content.
* Each `th` element:
    - `data-data=""` indicates which attribute in the json object should be displayed in this column.
    - `data-name=""` sent to the server where it is used to determine which property the column belongs to.
    - `data-orderable="true"` if the column is orderable.
    - If the column is to be populated with a date, add the `dt-date` class.  This sets the column width to match the the width appropriate for a date.
    
JavaScript
==========

To create a basic datatable: 

```javascript 1.8
import "./../../../vendor/datatables/datatables";
import {
  tableConfig
} from "../../../utilities/datatables-utilities";

$([table selector]).DataTable(Obeject.assign({}, tableConfig), {
  /* custom properties */
});
```

For a complete example of custom column rendering, row selection, and date rendering see `/src/main/webapp/resources/js/pages/projects/samples/project-samples.js`.
    
    