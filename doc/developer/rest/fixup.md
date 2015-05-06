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

### Individual Resource

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

IRIDA uses [OAuth2](http://oauth.net/2/) for authentication and authorization of clients. Command-line tools must use the password grant type for OAuth2. Our examples are primarily showing how to interact with the IRIDA REST API over the using the password flow. IRIDA also supports the authorization code flow, so other web services can interact with IRIDA.

Most programming languages have libraries with convenient interfaces for dealing with OAuth2 authorization. We provide some examples for the programming languages where we've written our own clients, but a comprehensive list of libraries can be found here: <http://oauth.net/code/>

### Java

For Python, we recommend that you use [Spring Security OAuth2](http://projects.spring.io/spring-security-oauth/) or [Apache OLTU](https://oltu.apache.org/). We internally use Spring Security OAuth2 to implement server-side OAuth2.

### Python

For Python, we recommend that you use [Rauth](http://rauth.readthedocs.org/en/latest/) or [Requests-OAuthlib](https://requests-oauthlib.readthedocs.org/en/latest/). Both libraries are straightforward to use, so we provide some quick examples for both.

#### Rauth

A complete application that uses Rauth is the [command-line concatenater](https://irida.corefacility.ca/gitlab/irida/irida-tools/blob/development/scripts/ngsArchiveLinker/ngs2galaxy.py) for IRIDA.

#### Requests-OAuthlib

Another option for using Python with IRIDA is the [Requests-OAuthlib](https://requests-oauthlib.readthedocs.org/en/latest/).

A complete example application that uses Requests-OAuthlib is the [IRIDA Galaxy Import Tool](https://irida.corefacility.ca/gitlab/irida/import-tool-for-galaxy). This application uses the authorization code flow.

### Perl

For Perl, we recommend that you use the [`OAuth::Lite2::Client::UsernameAndPassword`](https://metacpan.org/pod/OAuth::Lite2::Client::UsernameAndPassword) package. 

A complete application that uses `OAuth::Lite2::Client::UsernameAndPassword` is the [command-line](https://irida.corefacility.ca/gitlab/irida/irida-tools/blob/development/scripts/ngsArchiveLinker/ngsArchiveLinker.pl) tool for IRIDA.

### HTTP

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

Resources
=========

IRIDA has four major resources:

1. Users,
2. Projects,
3. Samples,
4. Sequence files.

User
----

### Description
{:.no_toc}

This resource contains user information. User objects are used for authentication and authorization.

### Methods
{:.no_toc}

`GET`, `POST`, `PATCH`, `DELETE`.

### Media Types
{:.no_toc}

`application/xml`, `application/json`.

### Properties
{:.no_toc}

* **username**: A unique name that identifies the user. Must be at least 3 characters long.
* **email**: An e-mail address where the user can receive mail. E-mail addresses *can* be validated using [RFC2822](https://tools.ietf.org/html/rfc2822), but our implementation uses [Hibernate e-mail validator](https://docs.jboss.org/hibernate/validator/4.2/api/org/hibernate/validator/constraints/impl/EmailValidator.html).
* **password**: A string of characters that the user can use to authenticate themselves. Passwords must contain at least one upper-case letter, at least one lower-case letter, at least one number, and must be at least 6 characters long. **Note**: the password field is *never* sent back to the client, so is not part of the JSON/XML response.
* **firstName**: The given name of the user. Must be at least 2 characters long.
* **lastName**: The family name of the user. Must be at least 2 characters long.
* **phoneNumber**: The phone number where the user can be called. Must be at least 4 characters long (i.e., at least an internal extension). No other validation is done on this field.

Project
-------

### Description
{:.no_toc}

This resource contains information about a project. A project contains metadata about a project (like the project name, a brief description), a collection of samples, and any other project-related files.

### Methods
{:.no_toc}

`GET`, `POST`, `PATCH`, `DELETE`.

### Media Types
{:.no_toc}

`application/xml`, `application/json`.

### Properties
{:.no_toc}

* **name**: The name of the project.

Sample
------

### Description
{:.no_toc}

This resource contains information about a sample. A sample corresponds to a single strain. A sample contains metadata about the strain (like the sample name, a brief description) and a collection of files. In general, a sample will contain a pair of files (forward and reverse) for a paired-end run, and may also contain more files if top-up runs are executed for the strain.

### Methods
{:.no_toc}

`GET`, `POST`, `PATCH`, `DELETE`.

### Media Types
{:.no_toc}

`application/xml`, `application/json`.

### Properties
{:.no_toc}

* **sampleName**: The name of the sample.

Bookmarks
=========

User collection
---------------

### URI
{:.no_toc}

https://api.irida.ca/users

### Description
{:.no_toc}

`GET` this URI to get the first page of user accounts. By default, the first page contains 20 users and is sorted by account creation date. `POST` this URI to create a new user account. The body of the `POST` request should include a JSON or XML representation of the required properties of a user object (as above).

### Links
{:.no_toc}

As a collection of resources, this resource contains links to other pages (i.e., `first`, `previous`, `next`, `last`, and `self`) and a link to **all** resources of this type (`collection/all`).

Each resource in the collection has a link to itself (`self`) and a link to a list of the projects that the user has permissions to view (`user/projects`).

Project collection
------------------

### URI
{:.no_toc}

https://api.irida.ca/projects

### Description
{:.no_toc}

`GET` this URI to get the first page of projects. The first page contains 20 projects by default and is sorted by project creation date. `POST` this URI to create a new project. The body of the `POST` request should include a JSON or XML representation of the required properties of a project object (as above).

### Links
{:.no_toc}

As a collection of resources, this resource contains links to other pages (i.e., `first`, `previous`, `next`, `last`, and `self`) and a link to **all** resources of this type (`collection/all`).
