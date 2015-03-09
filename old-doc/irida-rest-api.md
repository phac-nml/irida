IRIDA REST API (v1)
===================
Everything responds to requests for `application/json`; we haven't used any special media types (yet).

Resources
---------
Every resource has a predictable structure:

* The entire JSON response is wrapped in an object with a key of "resource".
* Every "resource" object then has a "links" key (an array), and 0 or more key-value pairs.
* Each entry in the array of links consists of a "rel" key and an "href" key.

An example of a resource is: 

```javascript
{
	"resource" : { 
		"links" : [ {
			"rel" : "self",
			"href" : "http://www.example.org"
		}, {
			"rel" : "next",
			"href" : "http://www.example.org/next"
		} ],
		"field" : "field-value",
		"array" : [ "1", "2", "3" ]
	}
}
```

When you ask for the root (/) of the application, you'll get a resource that only contains links. The links from this resource have rels:

* `self`
* `projects`
* `users`

Example response when querying the root of the application:

```javascript
{
	"resource" : { 
		"links" : [ {
			"rel" : "self",
			"href" : "http://irida.ca/"
		}, {
			"rel" : "projects",
			"href" : "http://irida.ca/projects"
		}, {
			"rel" : "users",
			"href" : "http://irida.ca/users"
		} ]
	}
}
```

Resource collections
--------------------
If you follow the `projects` rel, you'll find a collection of project records. Resource collections have a similar predictable structure as resources:

* The entire JSON response is wrapped in an object with a key of "resource".
* The resource object has a "links" key (an array of links, as above) and a "resources" key (an array of resources, as above).

An example of a resource collection:

```javascript
{
	"resource" : { 
		"links" : [ {
			"rel" : "self",
			"href" : "http://www.example.org/"
		} ]
		"resources" : [ {
			"links" : [ {
				"rel" : "self",
				"href" : "http://www.example.org"
			}, {
				"rel" : "next",
				"href" : "http://www.example.org/next"
			} ],
			"field" : "field-value",
			"array" : [ "1", "2", "3" ]
		} ]
	}
}
```

Project
-------
Each project resource will have the following link rels:

* `self`: can be used to interact with that specific project.
* `project/samples`: a link to the resource collection of samples contained in the project.
* `project/users`: a link to the resource collection of users participating in the project.

Each project resource will have the following properties:

* `name`: The name of the project.
* `projectDescription`: A brief description of the project.
* `identifier`: The IRIDA-internal identifier.
* `createdDate`: The date the project was created (in milliseconds past the epoch).

Sample
------
Each sample resource will have the following link rels:

* `self`: can be used to interact with that specific sample.
* `sample/project`: a link to the project that owns this sample.
* `sample/sequenceFiles`: a link to the files contained in the sample.

Each sample resource will have *at least* the following properties:

* `sampleName`: the name of the sample.
* `description`: a brief, plain-text description of the sample.
* `latitude`: the latitude where the sample was collected.
* `longitude`: the longitude where the sample was collected.
* `identifier`: the IRIDA-internal identifier.
* `createdDate`: the date that the sample was created (in milliseconds past the epoch).
