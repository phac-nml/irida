---
layout: default
---

This guide describes the datatable development on the IRIDA Platform

* This comment becomes the table of contents
{:toc}

Datatables in the IRIDA Platform
================================

The IRIDA Platform relies heavily on large tables.  To facilitate development we have integrated the [Dandelion](http://dandelion.github.io/) [Datatables Project](http://dandelion.github.io/datatables/).


Required Dandelion Bundles
--------------------------

Dandelion will automatically inject the required css and javascript into the web page.

```html
<body ddl:bundle-includes="irida-datatables">
```

Dandelion will inject the following css into the **end** of the header tag:
* `/resources/bower_components/DataTables/media/js/jquery.dataTables.css`

Dandelion will inject the following javascript at the **end** of the body tag:
* `/resources/bower_components/DataTables/media/js/jquery.dataTables.js`
* `/resources/js/utils/datatable-utils.js`

Table Layout
------------

```html
<div dt:conf="projectsTable">
    <div dt:confType="callback" dt:type="draw" dt:function="iridaDatatables#datatable.tableDrawn"></div>
</div>
<table id="projectsTable" dt:table="true" dt:url="@{${ajaxURL}}" dt:filterPlaceholder="head_before">
    <thead>
    <tr role="row">
        <th id="project-id" dt:property="id" th:text="#{projects.table.id}"></th>
        <th id="project-name" dt:filterable="true" dt:property="name" th:text="#{projects.table.name}"
            dt:renderFunction="iridaDatatables#datatable.createItemButton"></th>
        <th id="project-organism" dt:filterable="true" dt:filterType="select" dt:property="organism" th:text="#{projects.table.organism}"></th>
        <th dt:property="role" dt:filterable="true" dt:filterType="select" dt:filterValues="roles" th:text="#{projects.table.role}"
            dt:renderFunction="iridaDatatables#datatable.i18n"></th>
        <th dt:property="samples" dt:searchable="false" th:text="#{projects.table.samples}"></th>
        <th dt:property="members" dt:searchable="false" th:text="#{projects.table.members}"></th>
        <th dt:property="created" dt:sortType="natural" th:text="#{projects.table.created}"
            dt:renderFunction="iridaDatatables#datatable.formatDate"></th>
        <th dt:property="modified" dt:sortType="natural" dt:sortInitDirection="desc" th:text="#{projects.table.modified}"
            dt:renderFunction="iridaDatatables#datatable.formatDate"></th>
    </tr>
    </thead>
</table>
```

### Configuration

Before the table is created there is a `div` with a `dt:conf="TABLE_ID"` attribute which references the datatable `id` attribute. This provides a configuration section for the datatable.

This snippet should be used for all datatables as it initializes the table to be full height and fixes the filters and search field to be bootstrap themed.

### `table` Attributes

| `dt:table="true"`                         	| **Required**.  Used by dandelion datatable to know which tables to render as a datatable.                                                                                                    	|
| `dt:url="@{url}"`                         	| **Required**. `url` is the url to the ajax end-point for the table data.                                                                                                                     	|
| `dt:filterPlaceholder="head_before"`      	| Add if wanting to add filters to the top of columns.                                                                                                                                         	|

### `th` Attributes

| `dt:filterable="true"`                    	| Add a filter to this column.                                                                                                                                                                 	|
| `dt:filterType="select"`                  	| Make the filter a select field. Unless the `dt:filterValues` attribute is used, it will generate the values from unique entries in the column.                                               	|
| `dt:filterValues="values"`                	| Variable containing a list to use as the filter options.  Thymeleaf can be used to internationalize this list win a script block at the end of the page.  See `projects.html` as an example. 	|
| `dt:property="propertyName"`              	| **Required**.  Which attribute to render in the current column.                                                                                                                              	|
| `dt:renderFunction="bundle#functionName"` 	| Function to call when the cell is rendered. `bundle` is the name of the dandelion bundle that contains the function.  `functionName` is the function to call.                                	|
| `dt:searchable="false"`                   	| Prevent the column from being searched in the global search.                                                                                                                                 	|
| `dt:sortType="natural"`                   	| Sort the column on the raw column data.                                                                                                                                                      	|
| `dt:sortInitDirection="desc"`             	| The column will be initially sorted based on this direction.                                                                                                                                 	|

Custom defined `dt:renderFunction`'s
------------------------------------

These function are written specifically for the IRIDA Platform and can be found in `/resources/js/utils/datatable-utils.js`.

### `iridaDatatables#datatable.formatDate` Date Format

This `dt:renderFunction` will format a unix timestamp into a date in the format `Do MMM YYYY`

**Example**

`1432744789000` --> `27th May 2015`

### `iridaDatatables#datatable.i18n` Internationalization

This `dt:renderFunction` will attempt to translate the columns value, if no internationalized value is available, it will return the untranslated value.

**Requires**

```js
var PAGE = {
    lang: {
        '__KEY__':  /*[[#{__VALUE__}]]*/ 'Collaborator',
    }
};
```

Where the __KEY__ is the key is the column value, and the __VALUE__ is the thymeleaf internationalized translation.

### `iridaDatatables#datatable.createItemButton` Render a link to the table item 

This `dt:renderFunction` is used to create a link to an IRIDA *thing* that has its own page.  It is expecting the object to contain a `link` attribute which the server should generate for the relative path to the *thing*'s page
