/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-03-28
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
(function Users ($) {
  'use strict';
  var viewModel;

  var UserModel = function (data) {
    this.firstName = ko.observable(data.firstName);
    this.lastName = ko.observable(data.lastName);
    this.email = ko.observable(data.email);
    this.phoneNumber = ko.observable(data.phoneNumber);
    this.username = ko.observable(data.username);
    this.link = ko.observable(data.links[0].href);
  };

  function ViewModel () {
    var self = this;

    self.users = ko.observableArray();

    self.getUser = function (user) {
      alert('Asking for ' + user.firstName() + ' at ' + user.link());
    };

    $.getJSON('/users', function (d) {
      var mappedUsers = $.map(d.userResourceList, function(i) {return new UserModel(i);});
      self.users(mappedUsers);
    });
  }

  viewModel = new ViewModel();
  ko.applyBindings(viewModel);

//  var ViewModel = function (users) {
//    var self = this;
//    self.users = ko.observableArray(users);
//
//
//    self.getUser = function (user) {
//      alert('Asking for ' + user.firstName() + ' at ' + user.link());
//    };
//  };
//
//  $.getJSON('/users', function (d) {
//    var mappedUsers = $.map(d.userResourceList, function(i) {return new User(i);});
//    viewModel = new ViewModel(mappedUsers);
//    ko.applyBindings(viewModel);
//  });


})(jQuery);