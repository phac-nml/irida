---
layout: default
search_title: "Troubleshooting IRIDA"
description: "A troubleshooting guide for common issues in IRIDA."
---

# Troubleshooting Guide
{:.no_toc}

This document describes common issues with IRIDA and how to resolve them.

* This comment becomes the table of contents
{:toc}

# 1. Analysis Pipeline Errors

When encountering an analysis pipeline error, the first step to troubleshooting is to figure out what sort of error was encountered. There are two types of errors that can occur: error with detailed information, and error with no detailed information.

## 1.1. Types of IRIDA job errors

### 1.1.1. Error with detailed information

This occurs when one of the Galaxy tools in the workflow reported an error and will show up with the status **Error** and a question mark **?** for more details. The stderr/stdout of the tool will be available in the preview.

![jobs-all-error-details.png][]

Additional details will be available after clicking on the job:

![job-error-details.png][]

Here, we get the exact tool **Prokka** and version `1.13` that is causing the error, along with additional details about the Galaxy instance where all these tools are being run.

### 1.1.2. Error with no detailed information

This occurs when there was an issue unrelated to a specific Galaxy tool (and so no detailed information can be obtained about a specific tool).

![job-error-nodetails.png][]

Examples where this error could occur include timeouts when transferring files to Galaxy or missing tools in Galaxy required by the pipeline. Normally, more details about these errors will be found in the IRIDA log file:

1. Timeout when uploading files to Galaxy

    ```
    25 Jul 2019 13:52:30,786 ERROR ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect:65 - Error occured for submission: AnalysisSubmission [id=3, name=AssemblyAnnotation_20190725_SRR1952908, submitter=admin, workflowId=4673cf14-20eb-44e1-986b-ac7714f9a96f, analysisState=SUBMITTING, analysisCleanedState=NOT_CLEANED] changing to state ERROR
ca.corefacility.bioinformatics.irida.exceptions.UploadTimeoutException: Timeout while uploading, time limit = 2 seconds
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService.filesToLibraryWait(GalaxyLibrariesService.java:245)
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService.filesToLibraryToHistory(GalaxyHistoriesService.java:201)
        ...
        at java.lang.Thread.run(Thread.java:748)
Caused by: java.util.concurrent.TimeoutException
        at java.util.concurrent.FutureTask.get(FutureTask.java:205)
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyLibrariesService.filesToLibraryWait(GalaxyLibrariesService.java:241)
        ... 28 more
    ```
    
    A quick solution for this particular error would be to increase the timeout limit for uploading files to Galaxy for pipelines (in this case, set low at 2 seconds). This can be done by setting `galaxy.library.upload.timeout=2` to some larger number in the `/etc/irida/irida.conf` file and restarting IRIDA.

2. Missing tools in Galaxy

    ```
    25 Jul 2019 14:16:16,393 ERROR ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceAspect:65 - Error occured for submission: AnalysisSubmission [id=4, name=AssemblyAnnotation_20190725_SRR1952908, submitter=admin, workflowId=4673cf14-20eb-4
4e1-986b-ac7714f9a96f, analysisState=SUBMITTING, analysisCleanedState=NOT_CLEANED] changing to state ERROR
ca.corefacility.bioinformatics.irida.exceptions.WorkflowException: GalaxyResponseException{status=400, responseBody={"err_msg": "Tool toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot add dummy datasets.", "err_code": 400014}, errorMessage=Tool toolshed.g2.b
x.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot add dummy datasets., errorCode=400014, traceback=null}
        at ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService.runWorkflow(GalaxyWorkflowService.java:123)
        at ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.AnalysisExecutionServiceGalaxyAsync.executeAnalysis(AnalysisExecutionServiceGalaxyAsync.java:152)
        ...
        Caused by: GalaxyResponseException{status=400, responseBody={"err_msg": "Tool toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot add dummy datasets.", "err_code": 400014}, errorMessage=Tool toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2 missing. Cannot ad
d dummy datasets., errorCode=400014, traceback=null}
    ```
    
    A quick solution for this issue would be to log into Galaxy and make sure the required tool (and version of the tool) is installed. In this case it would be `toolshed.g2.bx.psu.edu/repos/iuc/quast/quast/5.0.2`.
    
    
In general, though, you may want to take a look at the specific Galaxy history used by this IRIDA analysis pipeline to run jobs.

## 1.2. Finding the Galaxy History used by an IRIDA pipeline

As mentioned in section (1), there are two types of errors that can occur in a pipeline: those with detailed information, and those without. Getting the Galaxy History used by the pipeline depends on which type of error you encounter. Once we have the Galaxy History id, we can log into Galaxy to find more information about what was going on with this particular jobs in this Galaxy history.

### 1.2.1. Getting Galaxy History when a job has error details

In this case, it's pretty straightforward to get the Galaxy History id, it's displayed in the error details.

![job-details-galaxy-history.png][]

This tells us that the **Galaxy History ID** used by this pipeline is `e85a3be143d5905b`.

### 1.2.2. Getting Galaxy History when job has no error details

In this case, it's a bit more difficult to find the Galaxy History id. We will have to log into the IRIDA database to search for it.

#### 1.2.2.1. Logging into the IRIDA database
{:.no_toc}

If you log into the machine running the IRIDA instance, you can find the database connection details in the `/etc/irida/irida.conf` file. For example:

```
jdbc.url=jdbc:mysql://localhost:3306/irida_test
jdbc.username=test
jdbc.password=test
```

This tells us the database software is running on the machine `localhost` and we want to use the database named **irida_test**, with username **test** and password **test**.

To log into this database, you can run:

```bash
mysql -u test -p --host localhost --database irida_test
```

#### 1.2.2.2. Finding the Galaxy History id
{:.no_toc}

Once we've logged into the database, we can run a query to get the Galaxy History id, but first we need the IRIDA Analysis pipeline id. This can be found in the page listing the pipelines:

![irida-job-id.png][]

In this case, our job id is `3`.

So, now back to the MySQL database query, we want to run:

```sql
SELECT id,name,analysis_state,remote_analysis_id FROM analysis_submission WHERE id = 3;
```

This should give us:

```
+----+----------------------------------------+----------------+--------------------+
| id | name                                   | analysis_state | remote_analysis_id |
+----+----------------------------------------+----------------+--------------------+
|  3 | AssemblyAnnotation_20190725_SRR1952908 | ERROR          | 2a56795cad3c7db3   |
+----+----------------------------------------+----------------+--------------------+
```

The field containing the Galaxy History id is `remote_analysis_id` (so the value we are looking for is `2a56795cad3c7db3`).

#### 1.2.2.3. What if the Galaxy History id is NULL
{:.no_toc}

If this value is `NULL`, then it's possible that the error occurred before a Galaxy History was created. You can get more information about the history of this IRIDA analysis pipeline execution from the audit tables (`analysis_submission_AUD`). Please try running:

```sql
SELECT id,name,analysis_state,modified_date,remote_analysis_id FROM analysis_submission_AUD WHERE id = 3;
```

```
+----+----------------------------------------+----------------+---------------------+--------------------+
| id | name                                   | analysis_state | modified_date       | remote_analysis_id |
+----+----------------------------------------+----------------+---------------------+--------------------+
|  3 | AssemblyAnnotation_20190725_SRR1952908 | NEW            | 2019-07-25 13:48:33 | NULL               |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | PREPARING      | 2019-07-25 13:52:26 | NULL               |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | PREPARED       | 2019-07-25 13:52:27 | 2a56795cad3c7db3   |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | SUBMITTING     | 2019-07-25 13:52:27 | 2a56795cad3c7db3   |
|  3 | AssemblyAnnotation_20190725_SRR1952908 | ERROR          | 2019-07-25 13:52:30 | 2a56795cad3c7db3   |
+----+----------------------------------------+----------------+---------------------+--------------------+
```

This lets us see the history of the job as it was processed through IRIDA (and includes the `modified_date` giving an idea of when each stage occurred). You can see that the first two states **NEW** and **PREPARING** have a `NULL` value for `remote_analysis_id`. If the IRIDA pipeline errored in these states, there would not have been a Galaxy History created. So, you can skip trying to check the Galaxy History for more details about the job.

## 1.3. Viewing the Galaxy History used by the IRIDA analysis pipeline

### 1.3.1. Logging into Galaxy

Once we have the Galaxy History id, we can move on to logging into Galaxy to view more details about what went wrong with the IRIDA analysis pipeline. The first step is logging into Galaxy as the same user used by IRIDA.

*Note: if you do not know which Galaxy instance IRIDA is making use of, you can find this in the `/etc/irida/irida.conf` file as `galaxy.execution.url=http://GALAXY_URL`. You can find the username as `galaxy.execution.email=galaxy-user@galaxy.org`. IRIDA uses the Galaxy API to login, which is different from the password, so you may have to check with your administrator for the password used to log into Galaxy.*

![galaxy-home.png][]

### 1.3.2. Viewing all Galaxy histories

Galaxy will default to one of the histories run by IRIDA. To find the correct history though, we'll have to first go to the **Saved Histories** page.

![galaxy-saved-histories.png][]

This should bring us to a list of all the Galaxy histories.

![galaxy-histories-list.png][]

To switch the the history that we want (the one named by the **Galaxy History id**, e.g., `e85a3be143d5905b`) we need to enter this as a parameter to a specific URL. Note that this is different from the name of the Galaxy history (as shown in the screenshots).

### 1.3.3. Finding the correct history

To find the correct history, we first must copy the URL used to switch to a specific Galaxy History. Please click on one of the Galaxy histories shown in the list and hover over the option **Switch**:

![galaxy-history-switch-hover.png][]

Notice at the bottom of this screenshot there is a URL `localhost:48888/history/list...`. This is the URL we want to copy. To copy it you can right-click while hovering over **Switch** and select the option **Copy Link Location**.

![galaxy-history-copy-link.png][]

This should copy the link. You can now open up a new window (or tab) in your browser and paste the link:



[jobs-all-error-details.png]: images/jobs-all-error-details.png
[job-error-details.png]: images/job-error-details.png
[job-error-nodetails.png]: images/job-error-nodetails.png
[job-details-galaxy-history.png]: images/job-details-galaxy-history.png
[irida-job-id.png]: images/irida-job-id.png
[galaxy-home.png]: images/galaxy-home.png
[galaxy-saved-histories.png]: images/galaxy-saved-histories.png
[galaxy-histories-list.png]: images/galaxy-histories-list.png
[galaxy-history-switch-hover.png]: images/galaxy-history-switch-hover.png
[galaxy-history-copy-link.png]: images/galaxy-history-copy-link.png