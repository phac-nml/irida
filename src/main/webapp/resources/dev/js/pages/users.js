/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-03-28
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
(function Users($) {
  'use strict';
  var viewModel, prevRow;

  $('#addUserBtn').on('click', function () {
    $.ajax({
      type: 'POST',
      data: viewModel.currUser,
      success: function (d) {
        console.log(d);
      },
      error: function (XMLHttpRequest, textStatus, errorThrown) {
        console.log(XMLHttpRequest.responseText);
      }
    })
  });

  $(".listTable>.row").on('click', function () {
      if(prevRow){
          $(prevRow).removeClass('active');
      }
      $(this).addClass('active');
      prevRow = this;
  });

  var UserModel = function (data) {
    if (data.length) {
      this.firstName = ko.observable(data.firstName);
      this.lastName = ko.observable(data.lastName);
      this.email = ko.observable(data.email);
      this.phoneNumber = ko.observable(data.phoneNumber);
      this.username = ko.observable(data.username);
      this.link = ko.observable(data.links[0].href);
    }
  };

  function ViewModel() {
    var self = this;

    self.currUser = {
      firstName: "",
      lastName: "",
      username: "",
      password: "",
      email: "",
      phoneNumber: ""
    };
    self.users = ko.observableArray();

    self.getUser = function (user) {
      alert('Asking for ' + user.firstName() + ' at ' + user.link());
    };

    $.getJSON('/users', function (d) {
      var mappedUsers = $.map(d.users, function (i) {
        return new UserModel(i);
      });
      self.users(mappedUsers);
    });
  }

  viewModel = new ViewModel();
  ko.applyBindings(viewModel);
})(jQuery);