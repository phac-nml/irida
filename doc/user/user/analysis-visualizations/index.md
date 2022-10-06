---
layout: default
title: "Advanced Analysis Visualization"
search_title: "IRIDA Analysis Visualization"
description: "An overview of advanced analysis visualization features."
---

IRIDA Analysis Visualizations
=============================
{:.no_toc}

IRIDA has a set of integrated visualization tools that allow for a more thorough analyses of pipeline results.

### Phylogenetic Tree Visualization

Phylogenetic trees created by the SNVPhyl Analysis Pipeline are combined with metadata from the samples in the analysis to display the metadata beside the tree leaves.

![Tree](images/plain_tree.png)

Sample metadata can be added to the sample using the sample metadata uploader (see [Sample Metadata](../sample-metadata) for how to import).

To get to the advanced visualization page, on the analysis page, click the `View Advanced Visualization` button on the `Phylogenetic Tree` tab:

![Link to advanced analysis on analysis results page](images/viz_link.png)

#### Using Sample Metadata Field Templates

[Sample Metadata Templates](../sample-metadata-templates/) can be used to update which metadata fields are displayed in the visualization.  When the page is loaded the default template is presented, all metadata fields are shown.  A different template can be selected from the template selection as demonstrated below.  This will update the view with the desired metadata fields in the proper order.

![Demonstration of applying Sample Metadata Templates to the visualization](images/template_selection.png)

#### Toggling Sample Metadata Fields

Individual Metadata Fields can be displayed or hidden by clicking `Toggle Metadata` button, which will open a side panel displaying a list of metadata fields with checkboxes to make them visible or hidden.  

![Open metadata selection btn](images/toggle_metadata_button.png)

Individual metadata field by clicking on the field label in the side panel.

![Toggle metadata field](images/toggle_metadatafield.png)

After unchecking the checkbox next to 'Province' the metadata column is removed from the image.

![Result of removing 'PFGE-BlnI-pattern'](images/toggle_metadatafield_after.png)

#### Export to SVG

To export a copy of the visualization into SVG format, simply click on the `Export SVG` button in the upper right.

![Export SVG](images/export_svg.png)

This svg file can be opened in most external imaging software (except Adobe Illustrator for Mac).