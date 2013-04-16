function User(data) {
  "use strict";
  this.href = data.links[0].href;
  this.username = data.username;
  this.firstName = data.firstName;
  this.lastName = data.lastName;
  this.email = data.email;
  this.phoneNumber = data.phoneNumber;
}

function UsersViewModel() {
  "use strict";
  var self = this;

  self.users = ko.observableArray([]);

  $.getJSON("/users", function (allData) {
    var mappedUsers = $.map(allData.users, function (item) {
      return new User(item)
    });
    self.users(mappedUsers);
  });
}

function viewUser (event, data) {
  "use strict";
  window.location = data.href;
}
$.ajaxSetup({ cache: false });
ko.applyBindings(new UsersViewModel());