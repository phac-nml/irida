var uvm = null;

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
    closed: function () {
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

  self.links = {
    prev       : ko.observable(""),
    next       : ko.observable(""),
    currentPage: ko.observable("")
  };

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

  getUsers("http://0.0.0.0:8080/users");

  self.viewUser = function (data, event) {
    "use strict";
    window.location = data.href;
  };

  self.postNewUser = function () {
    "use strict";
    $.ajax({
      type      : 'POST',
      data      : $("#myModal").serialize(),
      url       : '/users',
      statusCode: {
        401: function () {
          alert("Authorization required");
        }
      },
      success   : function () {
        getUsers(self.links.currentPage());
        $("#myModal").foundation('reveal', 'close');
      },
      error     : function (request, status, error) {
        console.log(request);
        $.map($.parseJSON(request.responseText), function (value, key) {
          if (self.errors[key]) {
            self.errors[key](value);
          }
        });
      }
    })
  };

  self.updateTable = function (data, event, stuff) {
    getUsers(self.links[data]());
  };

  function getUsers(url) {
    console.log("about to getJSON at: " + url);
    $.getJSON(url, function (allData) {
      console.log("RETURNING FROM SERVER");
      self.links.next("");
      self.links.prev("");

      self.links.currentPage(allData.userResources.links.self);

      var mappedUsers = $.map(allData.userResources.users, function (item) {
        return new User(item)
      });
      $.map(allData.userResources.links, function (item) {
        if (self.links[item.rel]) {
          self.links[item.rel](item.href);
        }
      });
      self.users(mappedUsers);
    });
  }
}

$.ajaxSetup({ cache: false });

window.onload = function () {
  uvm = new UsersViewModel();
  ko.applyBindings(uvm);
};