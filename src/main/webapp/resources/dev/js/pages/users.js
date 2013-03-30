/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-03-28
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
;
var usersPage = (function ($, ko) {
  var self = this;

  function User(data) {
    this.firstName = ko.observable(data.firstName);
    this.lastName = ko.observable(data.lastName);
    this.email = ko.observable(data.email);
    this.phoneNumber = ko.observable(data.phoneNumber);
    this.username = ko.observable(data.username);
    this.link = ko.observable(data.links[0].href);
  }

  function UsersViewModel() {
    self.users = ko.observableArray([]);
    $.getJSON("/users", function (d) {
      var mappedUsers = $.map(d.userResourceList, function(i) {return new User(i)});
      self.users(mappedUsers);
    });
  }

  self.getUser = function (user) {
    alert("Asking for " + user.firstName() + " at " + user.link());
  }

  ko.applyBindings(new UsersViewModel());
})(jQuery, ko);