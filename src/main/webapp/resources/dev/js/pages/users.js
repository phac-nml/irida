/**
 * User: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date: 2013-03-28
 * Time: 11:25 AM
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
        $.each($.parseJSON(XMLHttpRequest.responseText), function (key, value) {
          var message = "";
          $.each(value, function (i, v) {
            message += v;
          });
          viewModel.newUserErrors[key].message(message);
          viewModel.newUserErrors[key].doesExist(true);
        });
      }
    })
  });

  $(".listTable>.row").on('click', function () {
    if (prevRow) {
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
      username: ko.observable(""),
      password: ko.observable(""),
      email: "",
      phoneNumber: ""
    };
    self.newUserErrors = {
      username: {
        message: ko.observable(""),
        doesExist: ko.observable(false)
      },
      password: {
        message: ko.observable(""),
        doesExist: ko.observable(false)
      }
    }

    self.users = ko.observableArray();

    self.getUser = function (user) {
      alert('Asking for ' + user.firstName() + ' at ' + user.link());
    };

    self.checkError = function (field) {
      return ko.computed({
        read: function () {
          return self.newUserErrors[field].length > 0 ? "error" : "";
        },
        write: function (value) {
          self.newUserErrors[field](value);
        }
      });
    };

    self.currUser.username.subscribe(function(){
      self.newUserErrors.username(false);
    });

    self.currUser.password.subscribe(function(){
      self.newUserErrors.password(false);
    });
  }

  viewModel = new ViewModel();
  ko.applyBindings(viewModel);
})(jQuery);