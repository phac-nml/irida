---
layout: default
---

This guide describes the Best Practices for UI Development on the IRIDA Platform

* This comment becomes the table of contents
{:toc}

Font Awesome Icons
==================

The IRIDA UI relies heavily on [Font Awesome](http://fortawesome.github.io/Font-Awesome/) for font icons. 

### Commonly Used Icons

| What     | Icon                   | Markup                               | Why?
|----------|:------------------------------------:|-----------------------------------------------------------------------|----------------------------------------------------------|
| file     | {% icon fa-file-o %}                 | `<i class="fa fa-file-o fa-fw" aria-hidden="true"></i>`               | Use to refer to a file of non-specific type.             |
| excel    | {% icon fa-file-excel-o %}           | `<i class="fa fa-file-excel-o fa-fw" aria-hidden="true"></i>`         | Use to refer to an excel file.                           |
| remove   | {% icon fa-remove %}                 | `<i class="fa fa-times fa-fw" aria-hidden="true"></i>`                | Use when performing a non-permanent removal from a list. |
| delete   | {% icon fa-trash-o %}                | `<i class="fa fa-trash fa-fw" aria-hidden="true"></i>`                | Use when deleting an item permanently from the UI.       |
| merge    |  {% icon fa-compress %}              | `<i class="fa fa-compress fa-fw" aria-hidden="true"></i>`             | Use for merging files.                                   |
| copy     | {% icon fa-copy %}                   | `<i class="fa fa-copy fa-fw" aria-hidden="true"></i>`                 | Use for copying an file.                                 |
| save     | {% icon fa-save %}                   | `<i class="fa fa-floppy-o" aria-hidden="true"></i>`                   | Use for saving an item                                   | 
| download | {% icon fa-download %}               | `<i class="fa fa-download fa-fw" aria-hidden="true"></i>`             | Use for downloading an item                              |
| warning  | {% icon fa-exclamation-triangle %}   | `<i class="fa fa-exclamation-triangle fa-fw" aria-hidden="true"></i>` | Use to prefix warning messages                           |
| id       |  {% icon fa-barcode %}               | `<i class="fa fa-barcode fa-fw" aria-hidden="true"></i>`              | Use for IRIDA identifier                                 | 
| organism | {% icon fa-leaf %}                   | `<i class="fa fa-leaf fa-fw" aria-hidden="true"></i>`                 | Use for IRIDA organisms                                  | 
| date     |  {% icon fa-calendar-o %}            | `<i class="fa fa-calendar fa-fw" aria-hidden="true"></i>`             | Use for any date or calendar                             | 
| loading  | {% icon fa-circle-o-notch fa-spin %} | `<i class="fa fa-circle-o-notch fa-spin fa-fw"></i>`                  | Use for displaying ajax loading                          |
| terminal |  {% icon fa-terminal %}              | `<i class="fa fa-terminal fa-fw" aria-hidden="true"></i>`             | Use for terminal commands                                |
| question |  {% icon fa-question-circle %}       | `<i class="fa fa-question-circle fa-fw“ aria-hidden="true"></i>`      | Display a help hover. |
