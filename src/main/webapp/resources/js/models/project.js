/**
 * Location of project.
 * LOCAL = current project.
 * ASSOCIATED = local project associated with the current project
 * REMOTE = remote project associated with current project.
 * @type {{LOCAL: number, ASSOCIATED: number, REMOTE: number}}
 */
var PROJECTS_TYPES = {
  LOCAL     : 0,
  ASSOCIATED: 1,
  REMOTE    : 2
};

/**
 * UI instance of Project
 * @param project
 * @param projectType - associated type.
 * @constructor
 */
function Project(project, projectType) {
  "use strict";
  var id = project.identifier;
  var name = project.name;
  var type = PROJECTS_TYPES[projectType] !== undefined ? PROJECTS_TYPES[projectType] : PROJECTS_TYPES.LOCAL;
  var organism = project.organism ? project.organism : '';

  this.getId = function () {
    return id;
  };

  this.getName = function () {
    return name;
  };

  this.getType = function () {
    return type;
  };

  this.getOrganism = function() {
    return organism;
  };
}