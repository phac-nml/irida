---
layout: "default"
toc_levels: 1..3
---

REST API
========
{:.no_toc}

This document describes the REST API for IRIDA.

* This comment becomes the toc
{:toc}

Formats
=======

The IRIDA REST API follows a standard output format, regardless of the resource being accessed. Resources can be accessed in two ways:

  1. As an individual resource,
  2. As part of a resource collection.

Both XML and JSON are valid output formats for all resources. Some resources (notably resources representing files produced by a sequencer stored on the file-system) support output in FASTA or FASTQ format.

JSON Format
-----------
{:.no_toc}

### Individual Resource
{:.no_toc}

Resources requested in JSON format will always have the following structure:

```javascript
{
  "resource" : {
    "links" : [ {
      "rel" : "self",
      "href" : "http://www.example.com"
    }, {
      "rel" : "magic/link",
      "href" : "http://www.example.com/magic"
    }, ...
    ],
    "resourceProperty1" : "resourceValue",
    "resourceProperty2" : "resourceValue",
    ...
  },
}
```

The entire response consists of the `resource` object. The `resource` object contains links related to the current resource as well as the properties of the resource.

The `resource` section contains an array of link objects under the `links` key. Each link has a `rel` and an `href`. In addition to the `links` section, the `resource` section contains all properties associated with the resource.

### Resource Collection
{:.no_toc}

Resource collections requested in JSON format will always have the following structure:

```javascript
{
  "resource" : {
    "links" : [ {
      "rel" : "magic/link",
      "href" : "http://www.example.com/magic"
    }, {
      "rel" : "self",
      "href" : "http://www.example.com"
    }, ...
    ],
    "resources" : [ {
      "links" : [ {
        "rel" : "self",
        "href" : "http://www.example.com/resources/1"
      } ],
      "resourceProperty" : "resourceValue",
      "resourceProperty2" : "resourceValue2",
      ...
      }, {
      ...
    } ]
  }
}
```

Similar to requesting an individual resource, a resource collection is wrapped with a `resource` object. Within the `resource` object is an array of links, and an array of `resources`. The `resources` array may contain complete records for the resource in the resource collection. If the record is not complete, each entry in the `resources` array will have links about that specific resource to get more information.

Authentication
==============

IRIDA does not allow any un-authenticated interaction with the REST API. IRIDA uses [OAuth2](http://oauth.net/2/) for authentication and authorization of clients. Command-line tools must use the password grant type for OAuth2. Our examples are primarily showing how to interact with the IRIDA REST API over the using the password flow. IRIDA also supports the authorization code flow, so other web services can interact with IRIDA.

Most programming languages have libraries with convenient interfaces for dealing with OAuth2 authorization. We provide some examples for the programming languages where we've written our own clients, but a comprehensive list of libraries can be found here: <http://oauth.net/code/>

### Java
{:.no_toc}

For Python, we recommend that you use [Spring Security OAuth2](http://projects.spring.io/spring-security-oauth/) or [Apache OLTU](https://oltu.apache.org/). We internally use Spring Security OAuth2 to implement server-side OAuth2.

### Python
{:.no_toc}

For Python, we recommend that you use [Rauth](http://rauth.readthedocs.org/en/latest/) or [Requests-OAuthlib](https://requests-oauthlib.readthedocs.org/en/latest/). Both libraries are straightforward to use, so we provide some quick examples for both.

A complete application that uses Rauth is the [command-line concatenater](https://irida.corefacility.ca/gitlab/irida/irida-tools/blob/development/scripts/ngsArchiveLinker/ngs2galaxy.py) for IRIDA.

Another option for using Python with IRIDA is the [Requests-OAuthlib](https://requests-oauthlib.readthedocs.org/en/latest/).

A complete example application that uses Requests-OAuthlib is the [IRIDA Galaxy Import Tool](https://irida.corefacility.ca/gitlab/irida/import-tool-for-galaxy). This application uses the authorization code flow.

### Perl
{:.no_toc}

For Perl, we recommend that you use the [`OAuth::Lite2::Client::UsernameAndPassword`](https://metacpan.org/pod/OAuth::Lite2::Client::UsernameAndPassword) package. 

A complete application that uses `OAuth::Lite2::Client::UsernameAndPassword` is the [command-line](https://irida.corefacility.ca/gitlab/irida/irida-tools/blob/development/scripts/ngsArchiveLinker/ngsArchiveLinker.pl) tool for IRIDA.

### HTTP
{:.no_toc}

If you *really* want to interact with IRIDA on the command line, or with an esoteric programming language that hasn't yet built an OAuth2 library, you can do so with basic HTTP. The example provided here shows how to use the password flow (you can't really use the authorization code flow without a web browser...) using shell and `curl`:

```bash
curl --silent http://localhost:8080/irida/api/oauth/token -X POST -d "client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&grant_type=password&username=$USERNAME&password=$PASSWORD" | python -m json.tool
```

In the example above, you would need to replace `$CLIENT_ID`, `$CLIENT_SECRET`, `$USERNAME` and `$PASSWORD` with your own client and user credentials. The `--silent` option on `curl` suppresses the progress display, and `python -m json.tool` will pretty-print the JSON that the server responds with.

An example response from the server is:

```javascript
{
  "access_token": "my-53cr37-04u7h-4cc355-70k3n",
  "expires_in": 43199,
  "scope": "read write",
  "token_type": "bearer"
}
```

Now, to access any other resources in the REST API you must include the `access_token` value in the `Authorization` header:

```bash
curl --silent http://localhost:8080/irida/api/projects/1 \\
   -H 'Authorization: Bearer my-53cr37-04u7h-4cc355-70k3n' | python -m json.tool
```

General Contracts
=================

### Formats

By default, IRIDA will respond to requests with JSON if no `Accept` is specified. IRIDA can also generally respond to requests for `application/xml`. Some resources (specifically marked) will be able to respond to requests for `application/fastq` and `application/fasta`.

All timestamps in IRIDA are returned in **milliseconds** since the [epoch](http://en.wikipedia.org/wiki/Unix_time).

### Client Errors

As in the [authentication](#authentication) section, all resources in the IRIDA REST API are protected and require authorization to access. Clients attempting to access *any* URL under the `/api` path without an authorization token will receive an HTTP `401 Unauthorized` response, regardless of the existence of a resource.

When sending resources to the server, the following will result in `400 Bad Request`:

* Sending invalid JSON (malformed, wrong type of quotes, etc.).
* Sending data that is considered invalid (field length is too short, for example).
* Sending data that includes unexpected fields (the server will respond with a list of acceptable field names).

Some resources may not respond to all HTTP verbs. If an HTTP verb is not supported, the response will be `405 Request method not supported`.

If you are not permitted to execute an HTTP verb on a resource, the response will be `403 Forbidden`.

### HTTP Verbs

All endpoints will respond to HTTP `GET` requests.

[Resource collections](#resource-collection) will respond to HTTP `POST` requests to create a new instance of that resource.

[Individual resources](#individual-resource) will respond to HTTP `PATCH` requests to update a resource and `DELETE` requests to delete a resource (if the resource is allowed to be deleted). A response of `403 Forbidden` will be issued if you are not permitted to modify or delete a resource (you may have read-only permissions).

Root Endpoints
==============

To begin accessing IRIDA, you can issue a `GET` request to get a collection of links to the top-level resources:

```bash
$ curl http://irida.corefacility.ca/irida/api
```

The root resource returned matches the [individual resource](#individual-resource) format.

### Links
{:.no_toc}

The root endpoint has links to the top-level resource collections in IRIDA.

| Name       | Description             |
|------------------|-------------------------------------|
| `self`       | the root resource           |
| `projects`     | the collection of project resources |
| `users`      | the collection of user resources  |
| `sequencingRuns` | the collection of sequencing runs (only administrative users can access this collection) |
| `analysisSubmissions` | the collection of analysis submissions created by the current user account |

Resources
=========

IRIDA has several major resources:

1. [Users](#users)
2. [Projects](#projects)
  * [Samples](#samples)
  * [Sequence files](#sequence-files)
3. [Sequencing Runs](#sequencing-runs)
4. [Analysis Submissions](#analysis-submissions)
5. [Analysis](#analysis)

### Users

Users can be accessed as a [collection](#user-collection) or as an [individual resource](#user-individual).

#### User Collection
{:.no_toc}

The user collection contains a reference to all users in the system. Each user resource has a `self` rel that can be followed to access details about the [individual user account](#user).

##### Links
{:.no_toc}

| Name   | Description          |
|--------|--------------------------------|
| `self` | A link to the users collection |

##### Example Response
{:.no_toc}

```javascript
{
  "resource" : {
  "resources" : [ {
    "username" : "exampleuser",
    "email" : "user@example.org",
    "firstName" : "Example",
    "lastName" : "User",
    "phoneNumber" : "867-5309",
    "identifier" : "1",
    "createdDate" : 1431111435000,
    "links" : [ {
    "rel" : "user/projects",
    "href" : "http://localhost:8080/api/users/exampleuser/projects"
    }, {
    "rel" : "self",
    "href" : "http://localhost:8080/api/users/1"
    } ]
  } ],
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/users"
  } ]
  }
```

#### User Individual
{:.no_toc}

Each user account can be accessed by a unique URL.

##### Properties
{:.no_toc}

| Name      | Description            | Validation                                                                                                                 |
|---------------|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `username`  | A username (used for logging in) | Required, minimum length 3, must be unique                                                                                                 |
| `email`     | An e-mail address        | Required, minimum length 5, must be unique, must be a valid e-mail address (validated with [Hibernate Validator E-mail constraint](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html_single/#table-custom-constraints)) |
| `firstName`   | The user's first name      | Required, minimum length 2                                                                                                         |
| `lastName`  | The user's last name       | Required, minimum length 2                                                                                                         |
| `phoneNumber` | A contact phone number       | Required, minimum length 4 (no other phone-number-related validation is done on this field)                                                                        |

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this user record |
| `user/projects` | The collection of projects that this user has permissions to view |


##### Example Response
{:.no_toc}

```javascript
{
  "resource": {
    "createdDate": 1431301716000,
    "email": "user@example.org",
    "firstName": "Example",
    "identifier": "1",
    "lastName": "User",
    "links": [
      {
        "href": "http://localhost:8080/api/users/example/projects",
        "rel": "user/projects"
      },
      {
        "href": "http://localhost:8080/api/example/1",
        "rel": "self"
      }
    ],
    "phoneNumber": "867-5309",
    "username": "example"
  }
}
```

### Projects

The [projects collection](#project-collection) provides the list of projects that the authorized user account has permissions to read. Each project entry in the list has a `self` rel that can be followed to view details about the [individual project](#project-individual).

#### Project Collection
{:.no_toc}

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | The list of projects that the user has permissions to read. |

##### Example Response
{:.no_toc}

```javascript
{
  "resource": {
    "links": [
      {
        "href": "http://localhost:8080/api/projects",
        "rel": "self"
      }
    ],
    "resources": [
      {
        "createdDate": 1431301716000,
        "identifier": "1",
        "links": [
          {
            "href": "http://localhost:8080/api/projects/1/users",
            "rel": "project/users"
          },
          {
            "href": "http://localhost:8080/api/projects/1/samples",
            "rel": "project/samples"
          },
          {
            "href": "http://localhost:8080/api/projects/1",
            "rel": "self"
          }
        ],
        "name": "Project 1",
        "projectDescription": null
      }
    ]
  }
}

```

#### Project Individual
{:.no_toc}

Each project can be accessed by a unique URL.

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this project |
| `project/users` | A link to view the collection of users that can view this project (the same format as [the list of users](#users) |
| `project/samples` | A link to view the collection of samples that are contained within this project. |

##### Properties
{:.no_toc}

| Name | Description | Validation |
|------|-------------|------------|
| `name` | The project name. | Required. Must be at least 5 characters long. Must not contain any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | &`|
| `projectDescription` | A description of the project | Optional. Not validated. |

##### Example Response
{:.no_toc}

```javascript
{
  "resource": {
    "createdDate": 1431301716000,
    "identifier": "1",
    "links": [
      {
        "href": "http://localhost:8080/api/projects/1/users",
        "rel": "project/users"
      },
      {
        "href": "http://localhost:8080/api/projects/1/samples",
        "rel": "project/samples"
      },
      {
        "href": "http://localhost:8080/api/projects/1",
        "rel": "self"
      }
    ],
    "name": "Project 1",
    "projectDescription": null
  }
}

```

### Samples

A sample corresponds to a single isolate and contains the sequencing data and metadata. A [collection of samples](#sample-collection) can only be accessed via a [project](#project), and an [individual sample](#sample-individual) can only be accessed from a sample collection.

#### Sample Collection
{:.no_toc}

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | The link back to this collection of samples. |

##### Example Response
{:.no_toc}

```javascript
{
  "resource" : {
  "resources" : [ {
    "sequenceFileCount" : 0,
    "description" : "The first sample",
    "sampleName" : "Sample 1",
    "sequencerSampleId" : "sample1",
    "strain" : null,
    "collectionDate" : null,
    "collectedBy" : null,
    "latitude" : null,
    "longitude" : null,
    "organism" : "E. coli",
    "isolate" : null,
    "geographicLocationName" : null,
    "isolationSource" : null,
    "cultureCollection" : null,
    "genotype" : null,
    "passageHistory" : null,
    "pathotype" : null,
    "serotype" : null,
    "serovar" : null,
    "specimenVoucher" : null,
    "subtype" : null,
    "hostTaxonomicName" : null,
    "hostDisease" : null,
    "hostDescription" : null,
    "hostDiseaseOutcome" : null,
    "hostDiseaseStage" : null,
    "hostHealthState" : null,
    "hostSex" : null,
    "hostSubjectId" : null,
    "hostTissueSampleId" : null,
    "hostAge" : null,
    "identifier" : "1",
    "createdDate" : 1406733849000,
    "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/5/samples/1"
    }, {
    "rel" : "sample/sequenceFiles",
    "href" : "http://localhost:8080/api/projects/5/samples/1/sequenceFiles"
    }, {
    "rel" : "sample/project",
    "href" : "http://localhost:8080/api/projects/5"
    } ]
  }],
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/5/samples"
  } ]
  }
}
```

#### Sample Individual
{:.no_toc}

An individual sample contains the metadata associated with an isolate. The sample will also link to the collection of [sequence files](#sequence-files) produced by a sequencer for the isolate.

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this sample. |
| `sample/project` | A link to the project that owns this sample. |
| `sample/sequenceFiles` | A link to the collection of sequence files in this sample. |

##### Properties
{:.no_toc}


| Name | Description | Validation |
|------|-------------|------------|
| `sampleName` | The name used to refer to the sample by the user. This is often the same as `sequencerSampleId`, but *may* be different. | Required. Must be at least 3 characters long. Must not contain any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | & ' . |` (note: this blacklist of characters is defined by the set of invalid characters on the Windows file system)|
| `sequencerSampleId` | The name used to refer to this sample by a sequencer. For Illumina, this is the value of the `Sample_Name` column in `SampleSheet.csv`.| Required. Must be at least 3 characters long. Must not contain any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | & ' .` (note: this blacklist of characters is defined by the set of invalid characters on the Windows file system)|
| `description` | A plain-text description of the sample. | Not required. |
| `strain` | The microbial or eukaryotic strain name. | Not required. Must be at least 3 characters long. |
| `collectionDate` | The date that the sample was collected. | Not required. Must be a valid date (in IRIDA, that is the number of milliseconds since the epoch). |
| `collectedBy` | The person (or organization) that collected the sample. | Not required. Must be at least 3 characters long. |
| `latitude` | The latitude of the location where the sample was collected. | Not required. Must be a valid latitude (must match the pattern `^-?(\d){1,2}(\.\d+)?$` and the first number group must be in the range `[-90, 90]`). |
| `longitude` | The longitude of the location where the sample was collected. | Not required. Must be a valid longitude (must match the pattern `-?(\d){1,3}(\.\d+)?$` and the first number group must be in the range `[-180, 180]`). |
| `organism` | The name of the organism that is contained by the sample. | Not required. Must be at least 3 characters long. |
| `isolate` | The identification or description of the specific individual from which this sample was obtained. | Not required. Must be at least 3 characters long. |
| `geographicLocationName` | A human-readable geographic location name, complementing the latitude and longitude of collection location. | Not required. Must be at least 3 characters long. Must match the pattern `\w+(:\w+(:\w+)?)?`, as defined by the NCBI location name pattern. |
| `isolationSource` | Describes the physical, environmental, and/or the geographical source of the biological sample from which the sample was derived. | Not required. |
| `cultureCollection` | Name of source institute and unique culture identifier. See the NCBI description for the proper format and list of allowed institutes: <http://www.insdc.org/controlled-vocabulary-culturecollection-qualifier> | Not required. |
| `genotype` | The observed genotype. | Not required. |
| `passageHistory` | Number of passages and passage method. | Not required. |
| `pathotype` | The bacterial-specific pathotype (examples: Eschericia coli - STEC, UPEC) | Not required. |
| `serotype` | The taxonomy below subspecies; a variety (in bacteria, fungi, or virus) usually based on its antigenic properties. Same as serovar and serogroup. e.g., serotype="H1N1" in Influenza A virus CY098518. | Not required. |
| `serovar` | The taxonomy below subspecies. | Not required. |
| `specimenVoucher` | Individual identifier for the physical specimen. | Not required. |
| `subgroup` | Taxonomy below subspecies. | Not required. |
| `subtype` | Used as classifier in viruses. | Not required. |


##### Example Response
{:.no_toc}

```javascript
{
  "resource" : {
  "sequenceFileCount" : 0,
  "description" : "The fifth sample",
  "sampleName" : "Sample 5",
  "sequencerSampleId" : "sample5",
  "strain" : null,
  "collectionDate" : null,
  "collectedBy" : null,
  "latitude" : null,
  "longitude" : null,
  "organism" : "E. coli",
  "isolate" : null,
  "geographicLocationName" : null,
  "isolationSource" : null,
  "cultureCollection" : null,
  "genotype" : null,
  "passageHistory" : null,
  "pathotype" : null,
  "serotype" : null,
  "serovar" : null,
  "specimenVoucher" : null,
  "subtype" : null,
  "hostTaxonomicName" : null,
  "hostDisease" : null,
  "hostDescription" : null,
  "hostDiseaseOutcome" : null,
  "hostDiseaseStage" : null,
  "hostHealthState" : null,
  "hostSex" : null,
  "hostSubjectId" : null,
  "hostTissueSampleId" : null,
  "hostAge" : null,
  "identifier" : "5",
  "createdDate" : 1406733854000,
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/5/samples/5"
  }, {
    "rel" : "sample/project",
    "href" : "http://localhost:8080/api/projects/5"
  }, {
    "rel" : "sample/sequenceFiles",
    "href" : "http://localhost:8080/api/projects/5/samples/5/sequenceFiles"
  } ]
  }

```

### Sequence Files

Each sample will refer to a [collection of sequence files](#sequence-file-collection) that have been sequenced and uploaded to IRIDA. Every record in the sequence files resource collection has a `self` rel to access the [individual sequence file](#sequence-file-individual).

#### Sequence File Collection
{:.no_toc}

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this collection of sequence files. |
| `sample` | A link back to the sample that owns this sequence file collection. |

##### Example response
{:.no_toc}

```javascript
{
  "resource" : {
  "resources" : [ {
    "file" : "/IRIDA/sequence-files/9/2/01-1111_S1_L001_R1_001.fastq",
    "fileName" : "01-1111_S1_L001_R1_001.fastq",
    "identifier" : "9",
    "createdDate" : 1407344463000,
    "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/4/samples/51/sequenceFiles/9"
    } ]
  }, {
    "file" : "/IRIDA/sequence-files/10/2/01-1111_S1_L001_R2_001.fastq",
    "fileName" : "01-1111_S1_L001_R2_001.fastq",
    "identifier" : "10",
    "createdDate" : 1407344463000,
    "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/4/samples/51/sequenceFiles/10"
    } ]
  } ],
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/4/samples/51/sequenceFiles"
  }, {
    "rel" : "sample",
    "href" : "http://localhost:8080/api/projects/4/samples/51"
  } ]
  }

```

#### Sequence File Individual
{:.no_toc}

Each sequence file corresponds to a single file (may be one of a pair for paired-end sequencing) generated for an isolate. A sequence file record contains a reference to the file-system location where the file can be found locally. A file can also be downloaded from IRIDA by making a request for the file using an `Accept` header of `application/fastq`.

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this sequence file record. |
| `sample` | A link to the sample that contains this sequence file. |
| `sample/sequenceFiles` | A link to the collection of sequence files for the parent sample. |

##### Properties
{:.no_toc}

| Name | Description | Validation |
|------|-------------|------------|
| `file` | The local file system location where the file can be found. | Not specified by a client. |
| `fileName` | The `basename` of the file, without the directory. | Derived from the `file` property, not specified by a client. |

##### Example response
{:.no_toc}

```javascript
{
  "resource" : {
  "file" : "/IRIDA/sequence-files/9/2/01-1111_S1_L001_R1_001.fastq",
  "fileName" : "01-1111_S1_L001_R1_001.fastq",
  "identifier" : "9",
  "createdDate" : 1407344463000,
  "links" : [ {
    "rel" : "sample/sequenceFiles",
    "href" : "http://localhost:8080/api/projects/4/samples/51/sequenceFiles"
  }, {
    "rel" : "self",
    "href" : "http://localhost:8080/api/projects/4/samples/51/sequenceFiles/9"
  }, {
    "rel" : "sample",
    "href" : "http://localhost:8080/api/projects/4/samples/51"
  } ]
  }

```

### Sequencing Runs

#### Sequencing Run Collection
{:.no_toc}

[Sequence files](#sequence-files) are commonly uploaded to IRIDA as part of a set of files generated by a single execution of a sequencer. Sequencing runs can only be viewed by administrative user accounts. Each record in the sequencing runs collection has a `self` rel that can be used to access details about the inddividual sequencing run resource.

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to the collection of sequencing runs. |

##### Example response
{:.no_toc}

```javascript
{
  "resource" : {
  "resources" : [ {
    "projectName" : "Test Project",
    "workflow" : "test workflow",
    "experimentName" : "Test Experiment",
    "application" : "FASTQ",
    "assay" : "Nextera",
    "chemistry" : "Amplicon",
    "readLengths" : 250,
    "investigatorName" : "Jon Doe",
    "description" : "Superbug",
    "uploadStatus" : "COMPLETE",
    "layoutType" : "SINGLE_END",
    "identifier" : "1",
    "createdDate" : 1406733873000,
    "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/sequencingrun/1"
    } ]
  } ],
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/sequencingrun"
  } ]
  }

```

#### Sequencing Run
{:.no_toc}

A sequencing run contains a reference to all of the files that were generated by the same execution of a sequencer (may span many samples). The run resource also contains metadata captured when the files were uploaded (on Illumina this is metadata in the `SampleSheet.csv` file).

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this sequencing run. |

##### Properties
{:.no_toc}

##### Example response
{:.no_toc}

```javascript
{
  "resource" : {
  "projectName" : "Test Project",
  "workflow" : "test workflow",
  "experimentName" : "Test Experiment",
  "application" : "FASTQ",
  "assay" : "Nextera",
  "chemistry" : "Amplicon",
  "readLengths" : 250,
  "investigatorName" : "Jon Doe",
  "description" : "Superbug",
  "uploadStatus" : "COMPLETE",
  "layoutType" : "SINGLE_END",
  "identifier" : "1",
  "createdDate" : 1406733873000,
  "links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8080/api/sequencingrun/1"
  } ]
  }

```

### Analysis Submissions

An analysis submission can provide clients with the complete set of inputs (consisting of [sequence files](#sequence-files) and workflow parameters), the selected IRIDA workflow, and the outputs of a completed analysis. The API provides convenient endpoints for filtering on the different types of workflows in IRIDA, currently assembly and annotation and phylogenomics.

#### Analysis Submissions Collection
{:.no_toc}

The collection of analysis submissions is the complete set of **all** analysis that the currently logged-in user has submitted for execution in IRIDA. You can filter on the different types of analysis by following the links below.

##### Links
{:.no_toc}

If your client needs to know about the specific types of outputs available to it, you should generally follow the link to the specific type of analysis that you're looking for rather than listing the entire collection of analysis for the user. For example, if your client is interested in showing some results using a phylogenetic tree, then you should configure your client to immediately follow the `analysisSubmissions/phylogenomics` link.

| Name | Description |
|------|-------------|
| `self` | A link to the collection of analysis submissions for this user. |
| `analysisSubmissions/phylogenomics` | A link to the collection of phylogenomics analysis that this user has submitted. |
| `analysisSubmissions/assembly` | A link to the collection of assembly and annotation analysis that this user has submitted. |

##### Example response
{:.no_toc}

```javascript
{
    "resource": {
        "links": [
            {
                "href": "http://localhost:8080/api/analysisSubmissions",
                "rel": "self"
            },
            {
                "href": "http://localhost:8080/api/analysisSubmissions/analysisType/phylogenomics",
                "rel": "analysisSubmissions/phylogenomics"
            },
            {
                "href": "http://localhost:8080/api/analysisSubmissions/analysisType/assembly",
                "rel": "analysisSubmissions/assembly"
            }
        ],
        "resources": [
            {
                "analysisCleanedState": "NOT_CLEANED",
                "analysisState": "SUBMITTING",
                "createdDate": 1434990870000,
                "identifier": "1",
                "inputParameters": {
                    "alternative-allele-fraction": "0.75",
                    "minimum-base-quality": "30",
                    "minimum-mapping-quality": "30",
                    "minimum-read-coverage": "15",
                    "repeat-minimum-length": "150",
                    "repeat-minimum-pid": "90"
                },
                "label": "SNVPhyl_20150622",
                "links": [
                    {
                        "href": "http://localhost:8080/api/analysisSubmissions/1",
                        "rel": "self"
                    }
                ],
                "modifiedDate": 1434990873000,
                "name": "SNVPhyl_20150622",
                "remoteFilesPaired": [],
                "remoteFilesSingle": [],
                "remoteInputDataId": null,
                "remoteWorkflowId": "f605f7fc4b86cc72",
                "workflowId": "ccca532d-b0be-4f2c-bd6d-9886aa722571"
            }
        ]
    }
}
```

#### Analysis Submission Individual
{:.no_toc}

Each analysis submission corresponds to a collection of inputs (files and parameters), and a collection of outputs. The outputs for an analysis submission depend on the type of analysis that was submitted. Analysis submissions and analysis outputs are immutable once created.

##### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this analysis submission. |
| `analysis` | A link to the **completed** analysis created for this submission. This link will only appear when the analysis submission is in the `COMPLETED` state. |

##### Properties
{:.no_toc}

**Note**: No validation is marked for analysis submission properties because an analysis submission is immutable.

| Name | Description |
|------|-------------|
| `analysisCleanedState` | Indicates whether or not temporary files created by the analysis submission execution have been cleaned in the workflow execution manager. |
| `analysisState` | Indicates the current state of the analysis in the workflow execution manager. Can be one of: `NEW`, `DOWNLOADING`, `FINISHED_DOWNLOADING`, `PREPARING`, `PREPARED`, `SUBMITTING`, `RUNNING`, `FINISHED_RUNNING`, `COMPLETING`, `COMPLETED`, or, `ERROR`. |
| `inputParameters` | A map of *submitted* parameters exposed by the workflow. |
| `name` | The name of the analysis submission (defined by the user). |
| `remoteFilesPaired` | A (possibly empty) collection of paired-end files used in this analysis that were downloaded from remote servers. |
| `remoteFilesSingle` | A (possibly empty) collection of single-end files used in this analysis that were downloaded from remote servers. |
| `remoteInputDataId` | The ID used to refer to the data collection created in the workflow execution manager. |
| `remoteWorkflowId` | The ID used to refer to the workflow created in the workflow execution manager. |
| `workflowId` | The IRIDA-specific workflow identifier used for this analysis submission. |

##### Example Response
{:.no_toc}

```javascript
{
    "resource": {
        "analysisCleanedState": "NOT_CLEANED",
        "analysisState": "COMPLETED",
        "createdDate": 1434990870000,
        "identifier": "1",
        "inputParameters": {
            "alternative-allele-fraction": "0.75",
            "minimum-base-quality": "30",
            "minimum-mapping-quality": "30",
            "minimum-read-coverage": "15",
            "repeat-minimum-length": "150",
            "repeat-minimum-pid": "90"
        },
        "label": "SNVPhyl_20150622",
        "links": [
            {
                "href": "http://localhost:8080/api/analysisSubmissions/1/analysis",
                "rel": "analysis"
            },
            {
                "href": "http://localhost:8080/api/analysisSubmissions/1",
                "rel": "self"
            }
        ],
        "modifiedDate": 1434991388000,
        "name": "SNVPhyl_20150622",
        "remoteFilesPaired": [],
        "remoteFilesSingle": [],
        "remoteInputDataId": "f605f7fc4b86cc72",
        "remoteWorkflowId": "f605f7fc4b86cc72",
        "workflowId": "ccca532d-b0be-4f2c-bd6d-9886aa722571"
    }
}
```

### Analysis

An analysis is the final resulting outputs of an [analysis submission](#analysis-submissions). You must follow the `analysis` link from an analysis submission, once it's completed.

#### Links
{:.no_toc}

The links for an anlysis generally correspond to the files that are produced as output from a specific type of analysis submission. You can infer the type of analysis submission based on the filter link that you used to list the analysis submissions. For example, if you filtered analysis submissions using the `analysisSubmissions/assembly` link, then the linked type of analysis will have output links corresponding to the assembly and annotation pipeline.

To access the file contents of each analysis output, use an `Accept` header of `text/plain`.

| Name | Description |
|------|-------------|
| `self` | A link to this analysis. |

##### Phylogenomics links
{:.no_toc}

| Name | Description |
|------|-------------|
| `outputFile/tree` | A newick-formatted tree file. |
| `outputFile/matrix` | The SNP matrix. |
| `outputFile/table` | The SNP table. |

##### Assembly and annotation links
{:.no_toc}

| Name | Description |
|------|-------------|
| `outputFile/read-merge-log` | Log file from merging reads (with FLASH). |
| `outputFile/assembly-log` | Log file from assembly (with SPAdes). |
| `outputFile/filter-assembly-log` | Log from filtering outputs from SPAdes. |
| `outputFile/contigs-all` | All contigs produced by the assembly. | 
| `outputFile/contigs-with-repeats` | All contigs produced by the assembly (including repeats), but filtering short contigs. |
| `outputFile/contigs-without-repeats` | All contigs produced by the assembly, excluding repeats and short contigs. |
| `outputFile/assembly-stats-repeats` | Log file for assembly filtering step (stats about number of repeats). |
| `outputFile/annotations-genbank` | Annotated genome in genbank format. | 
| `outputFile/annotations-stats` | Annotation statistics from Prokka. |
| `outputFile/annotations-log` | Log file produced by Prokka on `STDOUT`. |
| `outputFile/annotations-error` | Log file produced by Prokka on `STDERR`. |
