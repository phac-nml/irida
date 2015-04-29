---
layout: "default"
---

Proposal for a Unifying REST API for GMI-Compliant Repositories
===============================================================
{:.no_toc}

The goal of the [Global Microbial Identifier (GMI)](http://www.globalmicrobialidentifier.org/) initiative is to create a global platform for storing whole-genome sequencing data and epidemiological metadata, and to implement an analysis platform for detecting outbreaks and emerging pathogens.

A major component of such a global resource, as envisioned by the GMI, is a repository for storing next-generation sequencing data and epidemiological metadata. Several repositories already exist for storing sequencing data and metadata, unified by the [International Nucleotide Sequence Database Collaboration (INSDC)](http://www.insdc.org/). Each participant in the INSDC implements their repository independently and mirrors other participating repositories on a regular basis. In this document, we propose a new, unifying **REST API** for GMI-Compliant Repositories, including, *but not limited to*, the INSDC repositories.

First, we provide a brief review of the existing INSDC repositories, and the available methods for accessing those repositories. Then we describe, at a high-level, a hierarchical data model that follows the existing INSDC data model. We then provide a basic description of REST and some motivation for the creation of a common REST API, followed by a detailed description of the metadata, links and media types associated with the proposed data model. Finally, we show some example usage scenarios of how an analytical tool developer might interact with the common REST API.

Please note that this document is a **DRAFT**. Comments, errata, discussion, and constructive criticism are welcome.

* this comment becomes the toc
{:toc}

Review
------
The INSDC, consisting of the [National Center for Biotechnology Information (NCBI)](http://www.ncbi.nlm.nih.gov/), the [European Bioinformatics Institute (EBI)](https://www.ebi.ac.uk/), and the [DNA Data Bank of Japan (DDBJ)](http://www.ddbj.nig.ac.jp/), is the current *de facto* standard for storing sequence read data in a centralized, public repository.

The members of the INSDC exchange and synchronize data on a regular basis so that the data at each location (NCBI in North America, EBI in Europe, and DDBJ in Japan) has a complete mirror of the sequence data submitted to each respective read archive.

While each of these repositories share the same data, submitting and accessing data from each repository is inconsistent for both human and software clients. Furthermore, both the NCBI and EBI offer XML schemas for each type of resource in their system, but the schemas differ across institutions. Finally, the automated submission process for each site is also different; the NCBI requires submission of XML formats to CGI-based web services, where the EBI offers REST web-services for the submission of XML formats.

Unifying these APIs with a single, modern API will reduce the complexity of accessing these repositories on the part of developers. 

REST API
--------
Representational State Transfer (REST) is an architectural style formalized by Roy Fielding in his Ph.D. thesis "Architectural Styles and the Design of Network-based Software Architectures". In short, REST is an architectural style that models resources and their relatedness, and promotes the use of the features exposed by a communication protocol (often HTTP) for creating, reading, and modifying those resources.

The REST architectural style has several core tenets:

1. Identification of resources (a URI in HTTP),
2. Manipulation of resources (methods like `GET`, `POST`, `DELETE`, etc. in HTTP),
3. Self-describing messages (`Content-Type` headers in HTTP),
4. Hypermedia driving application state (named links between resources).

By implementing a common REST API, we will improve the ease-of-use for developers working with GMI-compliant repositories (analytical tools accessing resources, sequencers and sequencing facilities creating new resources in the repository) because developers only need to write software to target a single, common API. Furthermore, accessing GMI-compliant repositories can be simplified for developers by providing per-language libraries on behalf of the GMI that interact with **all** GMI-compliant repositories.

In addition to improving developer performance, implementing a common REST API (using formats like JSON and XML) provides an easy-to-extend model. We propose that the GMI define a minimal set of properties for each of the resources exposed by the common REST API. Each individual GMI-compliant repository is then free to extend upon that model using their own custom JSON or XML media types, provided that the GMI-defined minimal metadata is included when a client requests a resource using the media types defined by this document.

Next, the documentation for the REST API can be confined to a single location. The NCBI BioProject interface currently has documentation spread across several different locations.

Finally, by proposing a unified REST API for the GMI project, all interested participants in the GMI project can guide the construction of the unified REST API.

REST API Description
--------------------
### Technical Notes
* In metadata property descriptions, properties in *italics* are optional properties.
* The metadata and data model described in this document adhere strictly to the INSDC Sequence Read Archive data model (see: ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/). Some of the resources might better be described as composite resources (i.e., the resource consists of several sub-resources). The data model *could* be refactored to move these sub-resources into their own, uniquely addressable resource. Sub-resources that are candidates for extraction are in **bold**.
* Links are prefixed using `http://www.g-m-i.org/` to guarantee uniqueness of the relation name; the prefix is free to change, provided that uniqueness can be guaranteed. For example, the prefix could be `http://www.insdc.org/`, `http://www.ncbi.nlm.nih.gov`, `http://www.sra.ebi.ac.uk`, etc., provided that *everyone* implementing a GMI-compliant repository agrees on a single namespace.

### Authentication
For resources that require authentication, specifically for the creation of new resources, we propose using basic HTTP authentication. Please see [RFC 2617](https://tools.ietf.org/html/rfc2617) for a description of basic HTTP authentication. Storing user or client credentials is left to the implementor.

Going forward, GMI-compliant repositories may use [OAuth2](http://oauth.net/2/), [OpenID connect](http://openid.net/connect/), [Persona](https://www.mozilla.org/en-US/persona/), or other types of authentication/authorization schemes for accessing resources.

### Proposed Media Types
Implementors **must** implement the minimal complement of media types defined in the sections below. The proposed media types include a version number so that the minimal metadata can be changed over time without breaking clients targeting specific media type versions.

We propose the use of two formats as a basis for the metadata media types exposed by GMI-compliant REST APIs:

1. XML (see: http://www.w3.org/standards/xml/), and
2. JSON (see: http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf)

We propose XML because a large volume of existing clients can already parse the XML documents provided by INSDC participants. We propose the addition of JSON as a convenience for software developers targeting GMI-compliant REST APIs.

Furthermore, we propose the adoption of the existing XML media types provided by the INSDC for the Sequence Read Archive (SRA) initiative (the schemas can be found on the [EBI FTP Archive](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/)). The only modifications that we propose to these XML formats is the replacement of the custom `LINK`-type elements with standard `xml` `link` elements. Furthermore, we suggest that the identifiers specified in the schemas be replaced with fully-qualified URLs so that clients do not need to know how to construct the URLs themselves.

### Data Model
Members of the INSDC implement a five-level, hierarchical data model (See: [Kodama, Y. et al.](http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3245110/#__sec5title)):

1. Study (BioProject at NCBI and DDBJ)
2. Sample (BioSample at NCBI and DDBJ)
3. Experiment
4. Run
5. Analysis
6. Submission

We **do not** propose to replace this model; the existing data model is widely accepted by software developers and biologists alike, and is already implemented across the members of the INSDC. Instead, we propose to use the existing data model as the basis for a new REST API.

### Resources
Below we describe a *possible* set of properties and metadata for the data model described above. The properties and metadata here are generally used for data submission and resource creation.

#### Study
From [NCBI submission portal](https://submit.ncbi.nlm.nih.gov/subs/bioproject/): A BioProject (study) is a collection of biological data related to a single initiative, originating from a single organization or from a consortium of coordinating organizations.

##### Metadata
{:.no_toc}

The metadata for a study is already defined by the INSDC. Please see the [XML schema for study](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.study.xsd) hosted at the EBI. A summary of the metadata for a study is listed below:

* *A collection of identifiers* (both INSDC and non-INSDC); these could be rendered as links in the media type.
* The name of the center submitting the study; this could be rendered as a link in the media type with a `rel` of `http://www.g-m-i.org/links/submitter`.
* A study description:
    * Title (very short title).
    * An internal name for the study (linking to submitters LIMS).
    * *Abstract* (a longer, abstract-length description).
    * *A description* (a longer, free-form description of the project).
    * Type (the schema defines a wide selection of study types, GMI-related studies will likely have a more restricted set of types, 'Whole Genome Sequencing', 'Forensic or Paleo-genomics' (?) or 'Other').
    * *Links to related studies*. (link `rel` described below as `http://www.g-m-i.org/links/study/related-studies`)
* *Links to related resources* (**NOT** other studies, link `rel` described below as `http://www.g-m-i.org/links/related-resources`)
* *Additional, submitter-defined properties*. (the XML schema describes free-form tags here)

##### Links
{:.no_toc}

* `http://www.g-m-i.org/links/study`: a link to return to the collection of studies.
* `http://www.g-m-i.org/links/study/samples`: 0 or 1 links to the collection of samples associated with the study.
* `http://www.g-m-i.org/links/submitter`: a link to the entity responsible for submitting the study resource to the repository.
* `http://www.g-m-i.org/links/study/related-studies`: 0 or 1 links to a collection of other studies related to this study.
* `http://www.g-m-i.org/links/related-resources`: 0 or 1 links to a collection of other resources related to this study (publications, datasets, databases, etc.)

##### Acceptable media types
{:.no_toc}

* `application/vnd.gmi.study-v1+xml`
* `application/vnd.gmi.study-v1+json`
* `text/html`

#### Sample
From [NCBI submission portal](https://submit.ncbi.nlm.nih.gov/subs/biosample/): A BioSample (sample) is a description of the biological source materials used in experimental assays.

##### Metadata
{:.no_toc}

The metadata for a sample is already defined by the INSDC. Please see the [XML schema for sample](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.sample.xsd) hosted at the EBI. A summary of the metadata for a sample is listed below:

* *A collection of identifiers* (both INSDC and non-INSDC); these could be rendered as links in the media type.
* *The name of the center submitting the sample*; this could be rendered as a link in the media type with a `rel` of `http://www.g-m-i.org/links/submitter`.
* *A sample title*. (very short description, for searching purposes; perhaps a summary of the sample name)
* A collection of names for the sample:
    * The taxonomic identifier for the sample.
    * *The scientific name of the sample*. (this duplicates taxonomic identifier?)
    * *The common name of the sample*. (this duplicates taxonomic identifier?)
    * *The anonymized name of the sample*.
    * *The individual name of the sample* (where it came from); not suitable for human names.
* *A sample description* (free-form text)
* *Links to related resources* (link `rel` described above as `http://www.g-m-i.org/links/related-resources`)
* *Additional, submitter-defined properties*. (the XML schema describes free-form tags here)

##### Acceptable media types
{:.no_toc}

* `application/vnd.gmi.sample-v1+xml`
* `application/vnd.gmi.sample-v1+json`
* `text/html`

##### Links
{:.no_toc}

* `http://www.g-m-i.org/links/study/sample/experiments`: 0 or 1 links to the collection of experiments executed to produce data for the sample resource.
* `http://www.g-m-i.org/links/study/samples`: a link to the collection of samples in a study.
* `http://www.g-m-i.org/links/study`: a link to the study that owns the sample.
* `http://www.g-m-i.org/links/submitter`: a link to the entity responsible for submitting the sample resource to the repository.
* `http://www.g-m-i.org/links/related-resources`: 0 or 1 links to a collection of other resources related to this sample (publications, datasets, databases, etc.)
    
#### Experiment
From [NCBI SRA Handbook](http://www.ncbi.nlm.nih.gov/books/NBK47533/#SRA_Concepts_BK.2_Concepts): An experiment is a consistent set of laboratory operations on input material with an expected result.

##### Metadata
{:.no_toc}

The metadata for a experiment is already defined by the INSDC. Please see the [XML schema for experiment](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.experiment.xsd) hosted at the EBI. A summary of the metadata for an experiment is listed below:

* *A collection of identifiers* (both INSDC and non-INSDC); these could be rendered as links in the media type.
* The name of the center submitting the sample; this could be rendered as a link in the media type with a `rel` of `http://www.g-m-i.org/links/submitter`.
* *An experiment title*. (very short description, for searching purposes)
* A link to the study that owns the experiment; this could be rendered as a link with a `rel` of `http://www.g-m-i.org/links/study`, as described above.
* **The experimental design**:
    * A description of the experimental design (schema describes as "Goal and setup of the individual library").
    * A sample description, could be rendered as links in the media type with a `rel` of `http://www.g-m-i.org/links/study/sample`.
    * **A library description**:
        * The name
        * The strategy (schema describes as "Sequencing technique intended for this library")
        * The source (schema describes as "type of source material that is being sequenced")
        * The selection (schema describes as "Method used to enrich the target in the sequence library preparation")
        * The layout (one of SINGLE or PAIRED); paired has additional attributes of NOMINAL_LENGTH and NOMINAL_SDEV
        * The targeted loci (schema has no description, but this is an optional element)
        * The pooling strategy (schema describes as "indicates how the library or libraries are organized if multiple samples are involved")
        * The construction protocol (schema describes as "free-form text describing the protocol by which the sequencing library was constructed")
    * _**A spot description**_ (schema describes as "specifies how to decode the individual reads of interest from the monolithic spot sequence"; I *think* that this is useful for things like `sff_extract`)
        * A spot decode specification
            * *Spot length* (schema describes as "Number of base/color calls, cycles, or flows per spot")
            * Read specification
                * Read index (schema describes as "READ_INDEX starts at 0 and is incrementally increased for each sequential READ_SPEC within a SPOT_DECODE_SPEC")
                * *Read label* (schema describes as "READ_LABEL is a name for this tag, and can be used on output to determine read name, for example F or R")
                * Read class (no description in schema)
                * Read type (no description in schema)
                * One of:
                    * Relative order (schema describes as "The read is located beginning at the offset or cycle relative to another read.")
                    * Base coordinate (schema describes as "The location of the read start in terms of base count (1 is the beginning of the spot)")
                    * Expected base call table (a collection of basecalls, schema describes as "attributes provide description of this read meaning as well as matching rules")
    * Platform; one of (all include an instrument model attribute):
        * LS454
        * Illumina
        * Helicos
        * ABI SOLiD
        * Complete Genomics
        * PacBio SMRT
        * Ion Torrent
        * Capillary
    * *Processing* (not sure if this is supposed to be for how the data was processed prior to upload, or instructions on how to process the data post-upload, schema does not describe)
        * *Pipeline*
            * One or more pipeline section, where the pipeline section describes:
                * The program name
                * The version
                * *additional notes*
        * *Directives*
            * A demultiplexing directive (schema describes as "Tells the Archive who will execute the sample demultiplexing operation")
* *Links to related resources* (link `rel` described above as `http://www.g-m-i.org/links/related-resources`)
* *Additional, submitter-defined properties*. (the XML schema describes free-form tags here)

##### Acceptable media types
{:.no_toc}

* `application/vnd.gmi.experiment-v1+xml`
* `application/vnd.gmi.experiment-v1+json`
* `text/html`

##### Links
{:.no_toc}
* `http://www.g-m-i.org/links/study/sample/experiment/runs`: 0 or 1 links to the collection of runs for the experiment.
* `http://www.g-m-i.org/links/study/sample`: a link to the sample that owns the experiment.
* `http://www.g-m-i.org/links/study`: a link to the study that owns the experiment.
* `http://www.g-m-i.org/links/related-resources`: 0 or 1 links to other resources related to this experiment (publications, datasets, databases, etc.)

#### Run
From [NCBI SRA Handbook](http://www.ncbi.nlm.nih.gov/books/NBK47533/#SRA_Concepts_BK.2_Concepts): Results are called runs. Runs comprise the data gathered for a sample or sample bundle and refer to a defining experiment.

##### Metadata
{:.no_toc}
The metadata for a run is already defined by the INSDC. Please see the [XML schema for run](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.run.xsd) hosted at the EBI. A summary of the metadata for an experiment is listed below:

* *A collection of identifiers* (both INSDC and non-INSDC); these could be rendered as links in the media type.
* *A run title*. (very short description, for searching purposes)
* A link to the experiment that owns the run; this could be rendered as a link with a `rel` of `http://www.g-m-i.org/links/study/sample/experiment`, as described below.
* *A spot description* (This is the same type as spot description in the experiment type; please see above)
* *A platform* (This is the same type as platform in the experiment type; please see above)
* *Processing* (This is the same type as processing in the experiment type; please see above)
* *Links to related resources* (link `rel` described above as `http://www.g-m-i.org/links/related-resources`)
* *Additional, submitter-defined properties*. (the XML schema describes free-form tags here)
* *Data Blocks* (0 or more file descriptions)
    * Note: In the existing implementation, the files themselves are included in a 'Submission', not as part of the 'Run' directly. The information in the run refers to the file names included as part of a submission. We propose that the Run resource should be allowed to be created in a single `POST` using the `multipart/mixed` media type.

##### Links
{:.no_toc}
Links for run resources are only exposed in media types that support hypermedia (i.e., `json` or `xml` file formats).

* `http://www.g-m-i.org/links/study/sample/experiment/analyses`: 0 or 1 links to the collection of analyses executed on the sequence data produced by a run.
* `http://www.g-m-i.org/links/study/sample/experiment`: a link to the experiment that owns the run.
* `http://www.g-m-i.org/links/related-resource`: 0 or 1 links to a collection of other resources related to this run (publications, datasets, databases, etc.)
* `http://www.g-m-i.org/links/run/data`: 0 or 1 links to a collection of data files generated by this run.

##### Acceptable media types
{:.no_toc}
* `application/vnd.gmi.run-v1+xml`
* `application/vnd.gmi.run-v1+json`
* `application/vnd.ncbi.run+sra`	(SRA format)
* `multipart/mixed`                 (included for resource creation purposes; `multipart/mixed` can include multiple request bodies with different media types in a single request. See Section 7.2.1 of http://tools.ietf.org/html/rfc1341)`

#### Analysis

##### Metadata
{:.no_toc}
The metadata for an analysis is already defined by the INSDC. Please see the [XML schema for analysis](ftp://ftp.sra.ebi.ac.uk/meta/xsd/sra_1_5/SRA.analysis.xsd) hosted at the EBI. A summary of the metadata for an analysis is listed below:

* *A collection of identifiers* (both INSDC and non-INSDC); these could be rendered as links in the media type.
* An analysis title. (very short description, for searching purposes)
* Description. (detailed description of the analysis)
* *A link to the study that owns the analysis*; this could be rendered as a link with a `rel` of `http://www.g-m-i.org/links/study`, as described above.
* *A set of links to the samples that were used in the analysis*; this could be rendered as a link with a `rel` of `http://www.g-m-i.org/links/sample`, as described above.
* *A set of links to the runs that were used in the analysis*; this could be rendered as a link with a `rel` of `http://www.g-m-i.org/links/run`, as described above.
* *A set of links to related analysis resources*; this could be rendered as a link with a `rel` of `http://www.g-m-i.org/links/related-analysis`, as described below.
* The type of analysis, one of:
    * Reference alignment
        * A reference sequence (or a link to one)
        * Details of the assembly
    * Sequence variation
        * A reference assembly (including a reference sequence)
        * Experiment type (no description in schema)
        * Program (no description in schema)
        * Platform (no description in schema)
        * Imputation (no description in schema)
    * Sequence assembly
        * Name (no description in schema)
        * Partial (no description in schema)
        * Program (no description in schema)
        * Coverage (no description in schema)
        * Platform (no description in schema)
        * *Minimum gap length* (no description in schema)
    * Sequence annotation (no description in schema)
    * Reference sequence (no description in schema)
    * Sample phenotype (no description in schema)

##### Links
{:.no_toc}
Links for analysis resources are only exposed in media types that support hypermedia (i.e., non-sequence file formats).

* `http://www.g-m-i.org/links/study`: 1 or more links to the studies associated with the analysis.
* `http://www.g-m-i.org/links/sample`: 1 or more links to the samples associated with the analysis.
* `http://www.g-m-i.org/links/run`: 1 or more links to the runs associated with the analysis.
* `http://www.g-m-i.org/links/related-analysis`: 0 or more links to related analysis resources.
* `http://www.g-m-i.org/links/related-resources`: 0 or 1 links to a collection of other resources related to this analysis (publications, datasets, databases, etc.)

##### Acceptable media types
{:.no_toc}
* `application/vnd.gmi.analysis-v1+xml`
* `application/vnd.gmi.analysis-v1+json`
* (Other media types specific to the type of analysis executed)
* `multipart/mixed`                 (included for resource creation purposes; `multipart/mixed` can include multiple request bodies with different media types in a single request. See Section 7.2.1 of http://tools.ietf.org/html/rfc1341)

### File Upload Considerations
Many large-scale, widely available products that deal with large, binary files (notably YouTube, Google Drive, and Dropbox) provide file uploads over HTTP. Nevertheless, uploading large, binary data files over HTTP does have some realistic concerns:

1. Transfer speed: is the performance of uploading over HTTP worse than FTP or some other file transfer protocol? NCBI uses Aspera connect as a file transfer protocol for uploading files. Given the limited bandwidth of some centers, the file transfer protocol may be of little concern in terms of file transfer performance. Furthermore, with a sufficiently fast connection to the API, a client may overload the web server by uploading large files.
2. Reliability: what happens if the file transfer is interrupted during transmission? Is the client expected to re-submit a very large set of files to the server upon failure? Other file transfer protocols provide resilience over HTTP in terms of resuming file transmission after an interruption.
3. Browser limitations: some popular web browsers have a limit of 2GB for file uploads (notably, Firefox and Internet Explorer). While the intended audience of this REST API *explicitly does not* include web browsers, this is a realistic consideration.
4. Web server limitations: systems administrators may not feel comfortable allowing large files to be uploaded via the web server. Most web servers allow the administrator to specify the maximum file upload size, and the maximum file upload size may be much smaller than the type of files needed to be submitted to a sequence data repository.

Possible solutions to the problems exposed by uploading files over HTTP:

1. Simply use an alternative protocol more suited to transferring large, binary files (like FTP, SFTP, SCP/SSH, BitTorrent, Aspera Connect, etc.) to upload the files, then include a link to the created resource in the metadata package to be submitted. One possible problem with using alternative protocols compared to using HTTP/HTTPS is that some networks prohibit or hinder the use of other types of protocols, notably BitTorrent, for transferring data. The ports used by HTTP and HTTPS are often left without such prohibition. Furthermore, the addition of another protocol over HTTP for the REST API increases the complexity of clients, as each client would need to understand how to work with all necessary protocols (possibly irrelevant; most programming languages have libraries available for natively communicating over a variety of protocols).
2. Adopt a resumable HTTP upload protocol. Google and YouTube allow the transfer of large, binary video files over HTTP using a wide variety of network links. The [YouTube Data API](https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol) describes a protocol for uploading video files to YouTube over HTTP in a resumable fashion. Chunking the large file addresses the reliability and web server limitations. Web browsers cannot work with files at such a low level. Chunking the large file does not address the problem of transfer speed.

### HTTP Verbs
Each resource type can be represented as a resource collection or as an individual resource. This section outlines the HTTP verbs that can be invoked on resource collections and individual resources.

#### Resource Collections
Clients can invoke the following HTTP verbs on a resource collection (studies, samples, experiments, etc.) and expect the corresponding response codes outlined below.

* `GET`
    * Access a complete list of public resources stored in the repository.
    * Response code is one of the following:
        * `200 OK`, response body includes links to all public resources stored in the repository.
* `POST`
    * Create a new resource in the repository.
    * Requires a user account. Clients **must** provide an `Authorization` header; see [RFC 2617](https://tools.ietf.org/html/rfc2617).
    * `Content-Type` header **must** be one of the media types listed above.
    * Response code is one of the following:
        * `201 Created`, the resource was valid and created. Includes a `Location` header where the newly created resource can be found. Response body includes the resource, as stored in the repository, using the format specified by the `Content-Type` header.

          Example response:

              HTTP/1.1 201 Created
              Location: http://repository.g-m-i.org/studies/123
              
              ... response body omitted ...

        * `202 Accepted`, the resource metadata was accepted for processing, but not yet actually created in the repository. This response code should be used for resources that require human intervention for validation.
        * `400 Bad Request`, the resource metadata was malformed or incomplete. The response body **must** include the malformed properties along with an explanation (if available) of why the property was malformed. The format of the error message should either be XML or JSON, whichever format most closely matches the `Content-Type` header specified by the client.

          Example request/response:

              POST /studies HTTP/1.1
              Authorization: Basic XXXXXXXXXXXXXXXXXX
              Content-Type: application/vnd.gmi.study-v1+json

              {"abstract": "a very interesting study"}

              HTTP/1.1 400 Bad Request
              
              {"name": ["Study name is a required field."]}

        * `401 Unauthorized`, the credentials used to create the resource were not recognized.

#### Individual resource
Individual resources are accessed via the `Location` header upon successful creation of a resource, by navigating to a specific resource via a parent collection, or by bookmarking the address of a resource.

* `GET`
    * Access an individual resource stored in the repository.
    * May require authentication details if the resource is not publicly available.
    * `Accepts` header **must** be one of the acceptable media types listed above.
    * Response code is one of the following:
        * `200 OK`, response body includes the resource in the media type specified by the `Accepts` header.
        * `401 Unauthorized`, the credentials used to access the resource were not recognized. Repositories should respond with `401 Unauthorized` if the credentials were not recognized, regardless of the public availability of the resource.
        * `403 Forbidden`, the credentials used to access the resource were recognized, but do not have permission to view the requested resource.
        * `404 Not Found`, the requested resource does not exist in the repository.
* `PUT`
    * Completely replace the resource at the specified location, or creates a new resource at the location using the provided metadata. The request **must** include all properties of the study.
    * Requires a user account. Clients **must** provide an `Authorization` header; see [RFC 2617](https://tools.ietf.org/html/rfc2617).
    * `Content-Type` header **must** be one of the media types listed above.
    * Response code is one of the following:
        * `200 OK`, the replacement resource was valid and replaced. Includes a `Location` header where the replaced resource can be found (may be the same as the current URL for the resource). Response body includes the resource, as stored in the repository in the media type specified by the `Content-Type` header.
        * `400 Bad Request`, the resource metadata was malformed or incomplete. The response body **must** include the malformed properties along with an explanation (if available) of why the property was malformed. The format of the error message should either be XML or JSON, whichever format most closely matches the `Content-Type` header specified by the client. See the resource collection resource `400 Bad Request` for an example.
        * `401 Unauthorized`, the credentials used to update the resource were not recognized.
        * `403 Forbidden`, the credentials used to update the resource were recognized, but do not have permission to update the requested resource.
* `PATCH`
    * Replace only the provided properties in the resource metadata at the specified location
    * Same requirements and response codes as `PUT`.
* `DELETE`
    * Delete the resource from the system.
    * Response code is one of the following:
    	* `200 OK`, the deletion was accepted. Response body includes a link back to the collection of resource.
        * `401 Unauthorized`, the credentials used to delete the resource were not recognized.
        * `403 Forbidden`, the credentials used to delete the resource were recognized, but do not have permission to delete the requested resource.
        * `404 Not Found`, the requested resource does not exist in the repository.

Examples
========

### Creating a Study
This example shows the HTTP conversation that would take place by a client intending to create a new study. Note: the links and information shown in this example *do not* refer to a real example; the links and names were chosen arbitrarily.

Initial request for creation of study:

    POST /studies HTTP/1.1
    Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    Content-Type: application/vnd.gmi.study-v1+json

    {
        "external-database-identifiers": {
            "ncbi/genbank": {"href": "http://www.ncbi.nlm.nih.gov/bioproject/18649"},
            "ebi/ena": {"href": "https://www.ebi.ac.uk/ena/data/view/SRP029705"}
        },
        "description": {
            "title": "A very interesting study.",
            "internal-name": "Internal study name.",
            "abstract": "This is a very interesting study, completed by very interesting people.",
            "description": "A supremely interesting study, this is a very long-winded description of what this project is about.",
            "type": "Whole Genome Sequencing",
            "related-studies": [
                "http://www.ncbi.nlm.nih.gov/bioproject/18651",
                "http://www.ncbi.nlm.nih.gov/bioproject/58825"
            ]
        },
        "related-resources": {
            "http://www.google.com"
        },
        "additional-properties": {
            "not-used-internally": "Properties in this section are not parsed internally; submitters should use this as an opportunity to define metadata not officially required or specified by GMI-compliant repositories."
        }
        "submitter": [
            { "href": "http://repository.g-m-i.org/submitters/cdc" }
        ]
    }

Response (study has been accepted for human review):

    HTTP/1.1 202 Accepted
    Location: http://repository.g-m-i.org/studies-to-review/123
    Content-Type: application/json

    {
        "response": "Your study has been accepted for review. Automated parsers have briefly verified the metadata supplied with your study and have found no errors, however human review is required. Please see http://repository.g-m-i.org/studies-to-review/123 to monitor the review progress of your study."
    }

Client begins to monitor progress of review:

    GET /studies-to-review/123 HTTP/1.1
    Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    Accept: application/json

Repository responds with the status of the request for creation:

    HTTP/1.1 200 OK
    Content-Type: application/json
    Last-Modified: Mon, 23 Dec 2013 19:43:31 GMT

    {
        "status": {
            "message": "pending review"
        },
    }

Client continues to monitor progress of review:

    GET /studies-to-review/123 HTTP/1.1
    Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    Accept: application/json
    If-Modified-Since: Mon, 23 Dec 2013 19:43:31 GMT

Content is unchanged on server; server does not send back complete response:

    HTTP/1.1 304 Not Modified
    Date: Mon, 23 Dec 2013 20:43:31 GMT

Client continues to monitor progress of review:

    GET /studies-to-review/123 HTTP/1.1
    Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    Accept: application/json
    If-Modified-Since: Mon, 23 Dec 2013 20:43:31 GMT

Server responds with completed review, and a hyperlink to the location of the study:

    HTTP/1.1 200 OK
    Content-Type: application/json
    Last-Modified: Mon, 23 Dec 2013 21:43:31 GMT

    {
       "status": { 
            "message": "review complete",
            "http://www.g-m-i.org/links/study": "http://repository.g-m-i.org/studies/123"
        }
    }

### Adding a Sample to a Study
In the last example we showed the general process required to add a study to a repository. In this example, we show how a client can add a new sample to an existing study.

Given the location of the study (http://repository.g-m-i.org/studies/123), the client will issue ask the server for more information about the resource:

    GET /studies/123 HTTP/1.1
    Accept: application/vnd.gmi.study-v1+json

The server will respond with the project metadata in the JSON format requested:

    HTTP/1.1 200 OK
    Content-Type: application/vnd.gmi.study-v1+json
    Last-Modified: Mon, 23 Dec 2013 21:43:31 GMT
    
    {
        "links": [
            { "rel": "self", "href": "http://repository.g-m-i.org/studies/123" },
            { "rel": "http://www.g-m-i.org/links/study/samples", "href": "http://repository.g-m-i.org/studies/123/samples" },
            { "rel": "http://www.g-m-i.org/links/submitter", "href": "http://repository.g-m-i.org/users/1" },
            { "rel": "http://www.g-m-i.org/links/study/related-studies", "href": "http://repository.g-m-i.org/studies/123/related-studies" },
            { "rel": "http://www.g-m-i.org/links/related-resources", "href": "http://repository.g-m-i.org/studies/123/related-resources" }
        ],
        // Other project metadata...
    }

From the response, the client can find the link for the collection of samples associated with the study by looking for a link with a `rel` of `http://www.g-m-i.org/links/study/samples`. Once the client has found the link, it can either get the complete list of samples associated with the study by issuing a `GET` request for the URL in the `href` part of the link:

    GET /studies/123/samples HTTP/1.1
    Accept: application/json

The server will respond with the complete set of samples associated with the study:

    HTTP/1.1 200 OK
    Content-Type: application/json
    Last-Modified: Mon, 23 Dec 2013 21:43:31 GMT

    {
        [
            { 
                "sampleName": "sample",
                "links": [
                    { "rel": "self", "href": "http://repository.g-m-i.org/studies/123/samples/1" },
                ],
                // Other sample metadata...
            }
        ]
    }

The client can also add new samples to the study by issuing a `POST` request to the same `href`:

    POST /studies/123/samples HTTP/1.1
    Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
    Content-Type: application/vnd.gmi.sample-v1+json

    {
        "sampleName": "sample",
        // Other sample metadata...
    }

The server might respond immediately indicating success:

    HTTP/1.1 201 Created
    Location: http://repository.g-m-i.org/studies/123/samples/456

Or, the server might respond immediately indicating failure, with an invalid property name, for example:

    HTTP/1.1 400 Bad Request
    Content-Type: application/json

    {
        "message": "Invalid property fields named.",
        "invalidFields": [
            "sampleName"
        ]
    }
   
References
----------

1. [NCBI SRA Handbook](http://www.ncbi.nlm.nih.gov/books/NBK47533/)
    1. [Submission Quick Start Guide](http://www.ncbi.nlm.nih.gov/books/NBK47529/)
2. [Shumway, M. et al. *Archiving next generation sequencing data*.](http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2808927/)
3. [Leinonen, R. et al. *The Sequence Read Archive*.](http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3013647/)
4. [Kodama, Y. et al. *The sequence read archive: explosive growth of sequencing data*.](http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3245110/)
5. [IETF RFC 6838](https://tools.ietf.org/html/rfc4288)
6. [IETF RFC 2167](https://tools.ietf.org/html/rfc2617)
7. [NCBI BioProject Core XML Schema](ftp://ftp.ncbi.nlm.nih.gov/bioproject/Schema.v.1.1/Core.xsd)
8. [IANA Link Relations](https://www.iana.org/assignments/link-relations/link-relations.xhtml)
9. [IETF RFC 2616 - Section 10, Status Codes Definition](http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)

Additional Resources on REST APIs
=================================

* <http://www.ics.uci.edu/~fielding/pubs/dissertation/top.htm>
* <http://blog.dhananjaynene.com/2009/06/why-rest/>
* <http://www.crummy.com/writing/speaking/2008-QCon/>
* <http://www.martinfowler.com/articles/richardsonMaturityModel.html>
* <http://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven>
