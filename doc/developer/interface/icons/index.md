---
layout: default
---

This guide describes the Best Practices for UI Development on the IRIDA Platform

* This comment becomes the table of contents
{:toc}

Thymeleaf Font Awesome Dialect
==============================

The IRIDA UI relies heavily on [Font Awesome](http://fortawesome.github.io/Font-Awesome/) for vector icons. In order to ensure consistent use of icons within the UI a Thymeleaf dialect was created.

If the type of icon selected is not available an error will be thrown and the page will not render.

Dialect Usage
-------------

### Basic Usage

If an icon needs to be added to the page, use the following syntax, where type is the type of icon:

```html
<fa:icon type="organism" />
```

The Font Awesome Dialect will render the following after server sided processing by Thymeleaf:

```html
<span class="fa fa-leaf"></span>
```

### Adding fixed width

Not all icons in Font Awesome are a consistent width.  If the intent of usage is in a list (such as a sidebar) add the `fixed` to the tag:

```html
<fa:icon type="organism" fixed="fixed" />
```

The Font Awesome Dialect will render the following after server sided processing by Thymeleaf:

```html
<span class="fa fa-leaf fa-fw"></span>
```

### Icons size

Since Font Awesome icons are vector icons, they can be rendered at larger sizes without any impact on the way they are displayed.

There are six sizes of icons that can be add in IRIDA UI: 

|-----------|---------------------------------------------|--------------------------|
| no tag    | &lt;fa:icon type="organism" /&gt;           | {% icon fa-leaf %}       |
| size="lg" | &lt;fa:icon type="organism" size="lg" /&gt; | {% icon fa-leaf fa-lg %} |
| size="2x" | &lt;fa:icon type="organism" size="2x" /&gt; | {% icon fa-leaf fa-2x %} |
| size="3x" | &lt;fa:icon type="organism" size="3x" /&gt; | {% icon fa-leaf fa-3x %} |
| size="4x" | &lt;fa:icon type="organism" size="4x" /&gt; | {% icon fa-leaf fa-4x %} |
| size="5x" | &lt;fa:icon type="organism" size="5x" /&gt; | {% icon fa-leaf fa-5x %} |

### Available Icons

| Type          | When to Use                                               | Icon                               |
|---------------|-----------------------------------------------------------|:----------------------------------:|
| file          | Use to refer to a file                                    | {% icon fa-file-o %}               |
| remove        | Use when performing a non-permanent removal from a list   | {% icon fa-remove %}               |
| delete        | Use when deleting an item permanently from the UI         | {% icon fa-trash-o %}              |
| merge         | Use for merging files                                     | {% icon fa-compress %}             |
| copy          | Use for copying an item                                   | {% icon fa-copy %}                 |
| save          | Use for saving an item                                    | {% icon fa-save %}                 |
| download      | Use for downloading an item                               | {% icon fa-download %}             |
| warning       | Use to prefix warning messages                            | {% icon fa-exclamation-triangle %} |
| id            | Use for IRIDA identifier                                  | {% icon fa-barcode %}              |
| organism      | Use for IRIDA organisms                                   | {% icon fa-leaf %}                 |
| date          | Use for any date or calendar                              | {% icon fa-calendar-o %}           |
| loading       | Use for displaying ajax loading                           | {% icon fa-spinner fa-pulse %}     |
| terminal      | Use for terminal commands                                 | {% icon fa-terminal %}             |
| show          | Use for indicating opening a disclosure panel (accordion) | {% icon fa-chevron-right %}        |
| hide          | Use for indicating hiding a disclosure panel (accordion)  | {% icon fa-chevron-down %}         |
| pipelineType  | Use to indicate a type of pipeline                        | {% icon fa-cogs %}                 |
| pipelineState | Use to indicate the state of a pipeline                   | {% icon fa-heartbeat %}            |
