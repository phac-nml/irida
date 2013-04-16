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

  $('#myModal').foundation('reveal', {
    close: function () {
      self.errors.username('');
      self.newUser.username('');
      self.errors.password('');
      self.newUser.password('');
      self.errors.firstName('');
      self.newUser.firstName('');
      self.errors.lastName('');
      self.newUser.lastName('');
      self.errors.email('');
      self.newUser.email('');
      self.errors.phoneNumber('');
      self.newUser.phoneNumber('');
    }
  });

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
    var mappedUsers = $.map(allData.userResources.users, function (item) {
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
        $("#myModal").foundation('reveal', 'close');
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