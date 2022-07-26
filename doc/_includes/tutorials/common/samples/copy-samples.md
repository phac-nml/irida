## Sharing and Moving Samples

Sharing and moving samples has been completely re-designed for the January 2022 release of IRIDA in an effort to allow
faster sharing or moving greater number of samples. It is important to remember that you must be a manager on both the
current project and the project you are trying to share or move samples to.

Samples can be selected on either the project samples page or the linelist page:

1. Project Samples Page: Click the "Sample Tools" dropdown and select "Share / Move Samples".
2. Linelist Page: click the "Share Samples" button.

![Share samples link.]({{ site.baseurl }}/images/tutorials/common/samples/share-link.png)

Once selected, you will be redirected to the new Share / Move Samples page.

![Share samples page.]({{ site.baseurl }}/images/tutorials/common/samples/share-page.png)

### Destination Project Selection

The first action is to select the destination project. Start typing the name of the destination project in the "Select a
project to share samples with" input. As you type a list of projects will be presented, select the project you want.
Once you select a project to share with, the "Next" button will become enabled, clicking on it will allow you to review
the samples that were selected to copy. the samples that were selected to copy.

![Share samples select destination from dropdown.]({{ site.baseurl
}}/images/tutorials/common/samples/share-destination.png)

### Sample Review

![Share samples select destination from dropdown.]({{ site.baseurl }}/images/tutorials/common/samples/share-sample.png)

Next you will see the list of samples that were selected. If you decide you do not want one of them, just click on the (
remove) at the end of the row.

If a sample has a locked symbol, it means that the sample is locked from modification in the current project and will
not be modifiable in the destination project.

![Share samples locked sample icon.]({{ site.baseurl }}/images/tutorials/common/samples/share-locked-sample.png)

If the destination project already has the same samples (sample ids and/or sample names) in it that are being shared from the source project, then you will see expandable warnings with these samples listed

![Share samples duplicates.]({{ site.baseurl }}/images/tutorials/common/samples/share-sample-duplicates.png)

### Moving Samples

If you want to move samples, which means they will be in the destination project, but removed from the current project,
then select the checkbox "Remove samples from current project (move samples)"

![Share samples move checkbox.]({{ site.baseurl }}/images/tutorials/common/samples/share-move-checkbox.png)

### Locking Samples (ONLY FOR SHARING NOT MOVING)

If you are sharing samples and you do not want them to be modifiable in the destination project, select the checkbox "
Prevent modification of samples in target project (only when copying samples)"

![Share samples locks checkbox.]({{ site.baseurl }}/images/tutorials/common/samples/share-lock-checkbox.png)

**NOTE: Both checkboxes cannot be selected at the same time.**

### Metadata Restrictions

![Share samples metadata restrictions.]({{ site.baseurl }}/images/tutorials/common/samples/share-metadata.png)

Samples are shared/moved with all their corresponding metadata. This step allows you to review the fields that are
included with the sample as well as review the metadata restriction level that will used on the destination project.

If the metadata field does not exist in it will be set to the current metadata restriction level in the current project.

![Share samples metadata restrictions.]({{ site.baseurl }}/images/tutorials/common/samples/share-metadata-current.png)

In this example, metadata field `secondaryPfge` has a restriction level of `Level 4`, the highest level, and `birthDate`
has a restriction level of `Level 1`, the lowest level. They both do not exist in the target project, so they are
initially set to that value. You can always set different levels in the destination project, **but please ensure that
you mean to do this.**  All restriction levels can be updated in the project > settings > metadata panel.

If the metadata field exists in the destination project, then the restriction level is set to the destination project
level and cannot be updated. This can be updated for all samples in that project in the project > settings > metadata
panel.

![Share samples metadata restrictions.]({{ site.baseurl }}/images/tutorials/common/samples/share-metadata-exists.png)

### Sharing

Once you are ready to copy / move the samples, click the "Share Samples" button at the bottom of the form. Once
complete, a message stating that the share or move was successful. From here, you can select to either go back to the
samples page, or continue to the destination project.

![Share samples success.]({{ site.baseurl }}/images/tutorials/common/samples/share-success.png)

### Warnings

#### Destination project already has the samples

If the destination project already has **all** of the samples you are trying to copy, you will be shown a message stating this without the possibility to share again.  You can still select another project at this point and continue.

![Share samples no samples.]({{ site.baseurl }}/images/tutorials/common/samples/share-no-samples.png)

If the destination project already has **some** of the samples you are trying to copy, you will be shown a message saying how many samples cannot be copied.  The list of samples will only display the samples that can be copied.  You can proceed to copy the available samples.

![Share samples some samples.]({{ site.baseurl }}/images/tutorials/common/samples/share-some-samples.png)