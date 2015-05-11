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

IRIDA does not allow any un-authenticated interaction with the REST API. IRIDA uses [OAuth2](http://oauth.net/2/) for authentication and authorization of clients. Command-line tools must use the password grant type for OAuth2. Our examples are primarily showing how to interact with the IRIDA REST API over the using the password flow. IRIDA also supports the authorization code flow, so other web services can interact with IRIDA.

Most programming languages have libraries with convenient interfaces for dealing with OAuth2 authorization. We provide some examples for the programming languages where we've written our own clients, but a comprehensive list of libraries can be found here: <http://oauth.net/code/>

### Java

For Python, we recommend that you use [Spring Security OAuth2](http://projects.spring.io/spring-security-oauth/) or [Apache OLTU](https://oltu.apache.org/). We internally use Spring Security OAuth2 to implement server-side OAuth2.

### Python

For Python, we recommend that you use [Rauth](http://rauth.readthedocs.org/en/latest/) or [Requests-OAuthlib](https://requests-oauthlib.readthedocs.org/en/latest/). Both libraries are straightforward to use, so we provide some quick examples for both.

A complete application that uses Rauth is the [command-line concatenater](https://irida.corefacility.ca/gitlab/irida/irida-tools/blob/development/scripts/ngsArchiveLinker/ngs2galaxy.py) for IRIDA.

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

The root endpoint has links to the top-level resource collections in IRIDA.

| Name             | Description                         |
|------------------|-------------------------------------|
| `self`           | the root resource                   |
| `projects`       | the collection of project resources |
| `users`          | the collection of user resources    |
| `sequencingRuns` |                                     |

Resources
=========

IRIDA has several major resources:

1. Users,
2. Projects,
  * Samples
  * Sequence files
3. Sequencing Runs

### Users collection

The user collection contains a reference to all users in the system. Each user resource has a `self` rel that can be followed to access details about the [individual user account](#user).

#### Links
{:.no_toc}

| Name   | Description                    |
|--------|--------------------------------|
| `self` | A link to the users collection |

#### Example Response
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

### User

Each user account can be accessed by a unique URL.

#### Properties
{:.no_toc}

| Name          | Description                      | Validation                                                                                                                                                                                                                               |
|---------------|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `username`    | A username (used for logging in) | Required, minimum length 3, must be unique                                                                                                                                                                                               |
| `email`       | An e-mail address                | Required, minimum length 5, must be unique, must be a valid e-mail address (validated with [Hibernate Validator E-mail constraint](http://docs.jboss.org/hibernate/validator/5.1/reference/en-US/html_single/#table-custom-constraints)) |
| `firstName`   | The user's first name            | Required, minimum length 2                                                                                                                                                                                                               |
| `lastName`    | The user's last name             | Required, minimum length 2                                                                                                                                                                                                               |
| `phoneNumber` | A contact phone number           | Required, minimum length 4 (no other phone-number-related validation is done on this field)                                                                                                                                              |

#### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this user record |
| `user/projects` | The collection of projects that this user has permissions to view |


#### Example Response
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

The projects collection provides the list of projects that the authorized user account has permissions to read. Each project entry in the list has a `self` rel that can be followed to view details about the [individual project](#project).

#### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | The list of projects that the user has permissions to read. |

#### Example Response
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

### Project

Each project can be accessed by a unique URL.

#### Links
{:.no_toc}

| Name | Description |
|------|-------------|
| `self` | A link to this project |
| `project/users` | A link to view the collection of users that can view this project (the same format as [the list of users](#users) |
| `project/samples` | A link to view the collection of samples that are contained within this project. |

#### Properties
{:.no_toc}

| Name | Description | Validation |
|------|-------------|------------|
| `name` | The project name. | Required. Must be at least 5 characters long. Must not contain any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | &`|
| `projectDescription` | A description of the project | Optional. Not validated. |

#### Example Response
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

A sample corresponds to a single isolate and contains the sequencing data and metadata. A collection of samples can only be accessed via a [project](#project).

#### Links
{:.no_toc}

#### Example Response
{:.no_toc}

### Sample

#### Properties
{:.no_toc}

### Sequence Files

#### Links
{:.no_toc}

#### Properties
{:.no_toc}

### Sequencing Runs
