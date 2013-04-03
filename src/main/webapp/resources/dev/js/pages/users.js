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
          viewModel.newUserErrors[key](true);
        });
                    var fred = 1;
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
    self.newUserErrors = {
      username: ko.observable(false),
      password: ko.observable(false)
    };
    self.users = ko.observableArray();

    self.getUser = function (user) {
      alert('Asking for ' + user.firstName() + ' at ' + user.link());
    };

    self.checkError = function (field) {
      return ko.computed({
         read: function () {
           return self.newUserErrors.username() === true ? "error" : "";
         },
        write: function (value) {
          this.newUserErrors[field](value);
        }
      });
    }

//      ko.computed(function () {
//      console.log("HELP " + self.newUserErrors.username());
//      return self.newUserErrors.username() === true ? "error" : "";
//    }, self);

//    $.getJSON('/users', function (d) {
//      var mappedUsers = $.map(d.users, function (i) {
//        return new UserModel(i);
//      });
//      self.users(mappedUsers);
//    });
  }

  viewModel = new ViewModel();
  ko.applyBindings(viewModel);
})(jQuery);