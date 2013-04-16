function User(data) {
  'use strict';
  this.href = data.links[0].href;
  this.username = data.username;
  this.firstName = data.firstName;
  this.lastName = data.lastName;
  this.email = data.email;
  this.phoneNumber = data.phoneNumber;
}

function UsersViewModel() {
  'use strict';
  var self = this;

  self.users = ko.observableArray([]);
  self.newUser = {
    username   : ko.observable(""),
    password   : ko.observable(""),
    firstName  : ko.observable(""),
    lastName   : ko.observable(""),
    email      : ko.observable(""),
    phoneNumber: ko.observable("")
  };

  self.errors = {
    username   : ko.observable(""),
    password   : ko.observable(""),
    firstName  : ko.observable(""),
    lastName   : ko.observable(""),
    email      : ko.observable(""),
    phoneNumber: ko.observable("")
  };

  $.getJSON("/users", function (allData) {
    var mappedUsers = $.map(allData.users, function (item) {
      return new User(item)
    });
    self.users(mappedUsers);
  });

  self.viewUser = function (data, event) {
    "use strict";
    window.location = data.href;
  };

  self.postNewUser = function () {
    "use strict";
    $.ajax({
      type: 'POST',
      data: $("#myModal").serialize(),
      url: '/users',
      success: function () {
        // TODO: Reload current page view
      },
      error: function (request, status, error) {
        $.map($.parseJSON(request.responseText), function(value, key) {
          self.errors[key](value);
        });
      }
    })
  };
}

$.ajaxSetup({ cache: false });
ko.applyBindings(new UsersViewModel());