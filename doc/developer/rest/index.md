---
layout: "default"
---

IRIDA NGS Archive - REST API
============================

This document describes the REST API for the IRIDA NGS Archive.

We cover the following topics:

  * Format of outputs produced and accepted by the REST API.
  * Authentication.

Formats
-------
The IRIDA NGS Archive REST API follows a standard output format, regardless of the resource being accessed. Resources can be accessed in two ways:

  1. As an individual resource,
  2. As part of a resource collection.

Both XML and JSON are valid output formats for all resources. Some resources (notably resources representing files produced by a sequencer stored on the file-system) support output in FASTA or FASTQ format.

### Output Format Selection

As a REST API, clients must specify their preferred output format using the `Accept` header. Consult the table below for the list of possible output formats and the corresponding `Accept` header.
  
<table border="1">
    <thead>
        <tr><th>Format</th><th><code>Accept</code> header</th></tr>
    </thead>
    <tbody>
        <tr><td>JSON</td><td>application/json</td></tr>
        <tr><td>XML</td><td>application/xml</td></tr>
        <tr><td>FASTA</td><td>application/fasta</td></tr>
        <tr><td>FASTQ</td><td>application/fastq</td></tr>
    </tbody>
</table>

### JSON Format
#### Individual Resource
Resources requested in JSON format will always have the following structure:

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

The entire response consists of the `resource` object. The `resource` object contains links related to the current resource as well as the properties of the resource.

The `resource` section contains an array of link objects under the `links` key. Each link has a `rel` and an `href`. In addition to the `links` section, the `resource` section contains all properties associated with the resource.

#### Resource Collection
Resource collections requested in JSON format will always have the following structure:

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
            } ],
            "totalResources" : 100
        }
    }

Similar to requesting an individual resource, a resource collection is wrapped with a `resource` object. Within the `resource` object is an array of links, and an array of `resources`. The `resources` array contains complete records for the resource in the resource collection, as well as links about that specific resource. Many requests for resource collections are paged, so the last part of the resource collection response is the `totalResources` field. The `totalResources` field tells the client how many resources are in the *entire* collection, not just on the current page.

A resource collection is paged by default, limited to 20 resources per page. Page links can be found in the`resource.links` section and are named according to [RFC5005](https://tools.ietf.org/html/rfc5005) (so `first`, `previous`, `next`, and `last`). Additionally, a link referring to a location where *all* resources are available can be found under the `collection/all` rel.

TODO: Clients can also find a URI template in the collection of links so that they can construct their own page representations instead of relying on the structure of the URIs returned for pages. Please see [RFC6570](https://tools.ietf.org/html/rfc6570) for more information about URI templates, including information about how to expand URI templates.

### XML Format
TODO

Authentication
--------------
The NGS Archive currently uses [HTTP Basic Authentication](https://tools.ietf.org/html/rfc2617). Clients can authenticate against the REST API using user credentials in the `Authorization` HTTP header. The authorization header consists of the keyword 'Basic' followed by a space character, then a base-64 encoding of the string consisting of the user name, a colon, and the password.

For example, the HTTP headers when authenticating against the REST API using `curl` with the user name 'fbristow' and the password 'password1' appear as follows:

    GET /api/users HTTP/1.1
    Authorization: Basic ZmJyaXN0b3c6cGFzc3dvcmQx
    User-Agent: 

Resources
---------
The NGS Archive has four major resources:

1. Users,
2. Projects,
3. Samples,
4. Sequence files.

### User
#### Description
This resource contains user information. User objects are used for authentication and authorization.

#### Methods
`GET`, `POST`, `PATCH`, `DELETE`.

#### Media Types
`application/xml`, `application/json`.

#### Properties
* **username**: A unique name that identifies the user. Must be at least 3 characters long.
* **email**: An e-mail address where the user can receive mail. E-mail addresses *can* be validated using [RFC2822](https://tools.ietf.org/html/rfc2822), but our implementation uses [Hibernate e-mail validator](https://docs.jboss.org/hibernate/validator/4.2/api/org/hibernate/validator/constraints/impl/EmailValidator.html).
* **password**: A string of characters that the user can use to authenticate themselves. Passwords must contain at least one upper-case letter, at least one lower-case letter, at least one number, and must be at least 6 characters long. **Note**: the password field is *never* sent back to the client, so is not part of the JSON/XML response.
* **firstName**: The given name of the user. Must be at least 2 characters long.
* **lastName**: The family name of the user. Must be at least 2 characters long.
* **phoneNumber**: The phone number where the user can be called. Must be at least 4 characters long (i.e., at least an internal extension). No other validation is done on this field.

### Project
#### Description
This resource contains information about a project. A project contains metadata about a project (like the project name, a brief description), a collection of samples, and any other project-related files.

#### Methods
`GET`, `POST`, `PATCH`, `DELETE`.

#### Media Types
`application/xml`, `application/json`.

#### Properties
* **name**: The name of the project.

### Sample
#### Description
This resource contains information about a sample. A sample corresponds to a single strain. A sample contains metadata about the strain (like the sample name, a brief description) and a collection of files. In general, a sample will contain a pair of files (forward and reverse) for a paired-end run, and may also contain more files if top-up runs are executed for the strain.

#### Methods
`GET`, `POST`, `PATCH`, `DELETE`.

#### Media Types
`application/xml`, `application/json`.

#### Properties
* **sampleName**: The name of the sample.

Bookmarks
---------
### User collection
#### URI
https://archive.irida.ca/users

#### Description
`GET` this URI to get the first page of user accounts. By default, the first page contains 20 users and is sorted by account creation date. `POST` this URI to create a new user account. The body of the `POST` request should include a JSON or XML representation of the required properties of a user object (as above).

#### Links
As a collection of resources, this resource contains links to other pages (i.e., `first`, `previous`, `next`, `last`, and `self`) and a link to **all** resources of this type (`collection/all`).

Each resource in the collection has a link to itself (`self`) and a link to a list of the projects that the user has permissions to view (`user/projects`).

### Project collection
#### URI
https://archive.irida.ca/projects

#### Description
`GET` this URI to get the first page of projects. The first page contains 20 projects by default and is sorted by project creation date. `POST` this URI to create a new project. The body of the `POST` request should include a JSON or XML representation of the required properties of a project object (as above).

#### Links
As a collection of resources, this resource contains links to other pages (i.e., `first`, `previous`, `next`, `last`, and `self`) and a link to **all** resources of this type (`collection/all`).
