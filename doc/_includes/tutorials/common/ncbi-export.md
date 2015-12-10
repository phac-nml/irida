IRIDA can assist in uploading sequence files to NCBI's [Sequence Read Archive](http://www.ncbi.nlm.nih.gov/Traces/sra/).  IRIDA requires that [BioProjects](http://www.ncbi.nlm.nih.gov/bioproject/) and [BioSamples](http://www.ncbi.nlm.nih.gov/biosample) be created before uploading, and will assign uploaded sequence files to the given BioProject and BioSample identifiers.  More information about the metadata which must be entered during the upload process can be found at [NCBI Submission Quick Start Guide](http://www.ncbi.nlm.nih.gov/books/NBK47529/#_SRA_Quick_Sub_BK_Experiment_).

To begin submitting sequence files, select which samples you want to upload from the project samples page, then click the Export and Upload to NCBI SRA button.

![Upload NCBI samples button]({{ site.baseurl }}/images/tutorials/common/ncbi-export/ncbi-select-samples.png)

You will be forwarded to a page where you must enter metadata about the uploaded files.  Start by entering information about the upload:

* BioProject ID - BioProject to submit files to. This project must be created in NCBI prior to this submission.
* Organization - Name of organization submitting these samples.
* Identifier Namespace - Prefix to use for submission identifiers in NCBI.  This prefix will be used to assign upload identifiers in the SRA but may not be visible in the uploaded files.
* Release Date - Submission will not be made public until after the chosen release date.

![NCBI project metadata]({{ site.baseurl }}/images/tutorials/common/ncbi-export/ncbi-project-metadata.png)

Next you must fill in information about the samples to be uploaded.  For more detailed information about these fields see [NCBI's SRA Handbook](http://www.ncbi.nlm.nih.gov/books/NBK47528/) ([Library Information](http://www.ncbi.nlm.nih.gov/books/NBK54984/table/SRA_Glossary_BK.T._library_descriptor_te/), [Sequencing Platform Description](http://www.ncbi.nlm.nih.gov/books/NBK54984/table/SRA_Glossary_BK.T._platform_descriptor_t/)).

* BioSample ID - NCBI BioSample to add files to. This sample must be created in NCBI prior to this submission.
* Library Name - The submitter's name for this library.
* Library Strategy - Sequencing technique intended for this library.
* Library Source - The type of source material that is being sequenced.
* Library Construction Protocol - Free form text describing the protocol by which the sequencing library was constructed.
* Instrument Model - The sequencing platform used to produce the data.
* Library Selection - Whether any method was used to select for or against, enrich, or screen the material being sequenced.

After entering this metadata you can select which files should be uploaded from each sample.  Only files selected with checkboxes will be uploaded to NCBI.

![NCBI sample metadata]({{ site.baseurl }}/images/tutorials/common/ncbi-export/ncbi-sample-metadata.png)

Click the `Submit` at the bottom of the page when the information is complete.

After submitting you will be redirected to a page showing the information you have entered for the upload and the status of the upload.  IRIDA will periodically check the status of uploads in the SRA and update their status as necessary.  After NCBI has assigned an accession number to your upload it will be displayed on this page.

![NCBI submission details]({{ site.baseurl }}/images/tutorials/common/ncbi-export/ncbi-submission-details.png)
