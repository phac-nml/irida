/**
 * UI instance of Sample
 * @param sample
 * @constructor
 */
function Sample(sample) {
  "use strict";
  var id = sample.identifier;
  var name = sample.sampleName;
  var dates = {
    created : sample.createdDate,
    modified: sample.modifiedDate
  };
  var project;

// Accessors for private data
  this.getId = function () {
    return id;
  };

  this.getName = function () {
    return name;
  };

  this.getCreatedDate = function () {
    return dates.created;
  };

  this.getModifiedDate = function () {
    return dates.modified;
  };

  this.setProject = function (project) {
    this.project = project;
  };

  this.getProject = function() {
    return this.project;
  };
}