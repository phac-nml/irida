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

<table>
<tr>
<td>
no tag
</td>
<td>
&lt;fa:icon type="organism" /&gt;
</td>
<td>    
{% icon fa-leaf %}
</td>
</tr>
<tr>
<td>
size="lg"
</td>
<td>
&lt;fa:icon type="organism" size="lg" /&gt;
</td>
<td>    
{% icon fa-leaf fa-lg %}
</td>
</tr>
<tr>
<td>
size="2x"
</td>
<td>
&lt;fa:icon type="organism" size="2x" /&gt;
</td>
<td>    
{% icon fa-leaf fa-2x %}
</td>
</tr>
<tr>
<td>
size="3x"
</td>
<td>
&lt;fa:icon type="organism" size="3x" /&gt;
</td>
<td>    
{% icon fa-leaf fa-3x %}
</td>
</tr>
<tr>
<td>
size="4x"
</td>
<td>
&lt;fa:icon type="organism" size="4x" /&gt;
</td>
<td>    
{% icon fa-leaf fa-4x %}
</td>
</tr>
<tr>
<td>
size="5x"
</td>
<td>
&lt;fa:icon type="organism" size="5x" /&gt;
</td>
<td>    
{% icon fa-leaf fa-5x %}
</td>
</tr>
</table>

### Available Icons

<table>
<tr>
<td>
file
</td>
<td>
File
</td>
<td>
{% icon fa-file-o %}
</td>
</tr>
<tr>
<td>
remove
</td>
<td>
Non-permanent removal of an item from a list.
</td>
<td>
{% icon fa-remove %}
</td>
</tr>
<tr>
<td>
delete
</td>
<td>
Permanent deletion of an item from the UI.
</td>
<td>
{% icon fa-trash-o %}
</td>
</tr>
<tr>
<td>
merge
</td>
<td>
Merging files
</td>
<td>
{% icon fa-compress %}
</td>
</tr>
<tr>
<td>
copy
</td>
<td>
Copy an item to another location.
</td>
<td>
{% icon fa-copy %}
</td>
</tr>
<tr>
<td>
save
</td>
<td>
Save an item.
</td>
<td>
{% icon fa-save %}
</td>
</tr>
<tr>
<td>
download
</td>
<td>
Download
</td>
<td>
{% icon fa-download %}
</td>
</tr>
<tr>
<td>
warning
</td>
<td>
Prefix warning messages to warning messages
</td>
<td>
{% icon fa-exclamation-triangle %}
</td>
</tr>
<tr>
<td>
id
</td>
<td>
Any IRIDA item's identifier
</td>
<td>
{% icon fa-barcode %}
</td>
</tr>
<tr>
<td>
organism
</td>
<td>
Organism
</td>
<td>
{% icon fa-leaf %}
</td>
</tr>
<tr>
<td>
date
</td>
<td>
Any date or calendar
</td>
<td>
{% icon fa-calendar-o %}
</td>
</tr>
<tr>
<td>
loading
</td>
<td>
Prefix loading messages
</td>
<td>
{% icon fa-spinner fa-pulse %}
</td>
</tr>
<tr>
<td>
terminal
</td>
<td>
Displaying terminal commands
</td>
<td>
{% icon fa-terminal %}
</td>
</tr>
<tr>
<td>
show
</td>
<td>
Disclosure panels (accordions) open icon
</td>
<td>
{% icon fa-chevron-right %}
</td>
</tr>
<tr>
<td>
hide
</td>
<td>
Disclosure panels (accordions) hide icon
</td>
<td>
{% icon fa-chevron-down %}
</td>
</tr>
<tr>
<td>
pipelineType
</td>
<td>
Refers to pipeline type
</td>
<td>
{% icon fa-cogs %}
</td>
</tr>
<tr>
<td>
pipelineState
</td>
<td>
Refers to pipeline state
</td>
<td>
{% icon fa-heartbeat %}
</td>
</tr>
</table>
