/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-17
 * Time:   9:41 AM
 */

var username = $('#username').text();

function Project (data) {
  "use strict";
   this.name = data.name;
}

function UserViewModel () {
  "use strict";
  self.projects = ko.observableArray([]);

  function getUserProjects () {
    "use strict";

    $.getJSON('/users/' + username + '/projects', function (allData) {
      var mappedProjects = $.map(allData.projectList, function (item) {
        return new Project(item)
      });
      self.projects(mappedProjects);
    });
  }

  getUserProjects();
}

$.ajaxSetup({ cache: false });
ko.applyBindings(new UserViewModel());