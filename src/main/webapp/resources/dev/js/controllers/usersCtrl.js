'use strict';

var app = angular.module('irida', ['ngSanitize']);

function usersListCtrl($scope) {

}

app.controller('newUserModalCtrl', ['$scope', function ($scope) {
    $scope.currUser = {
      username: '',
      password: '',
      email: '',
      phoneNumber: '',
      firstName: '',
      lastName: ''
    };

    $scope.errors = {
      username: {
        message: '',
        doesExist: false
      },
      password: {
        message: '',
        doesExist: false
      },
      email: {
        message: '',
        doesExist: false
      },
      phoneNumber: {
        message: '',
        doesExist: false
      },
      firstName: {
        message: '',
        doesExist: false
      },
      lastName: {
        message: '',
        doesExist: false
      },
    };

    $scope.postNewUser = function () {
      $.ajax({
        type: 'POST',
        data: $scope.currUser,
        success: function (d) {
          console.log(d);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
          $scope.$apply($.each($.parseJSON(XMLHttpRequest.responseText), function (key, value) {
            var message = value.join('<br/>');
            $scope.errors[key].message = message;
            $scope.errors[key].doesExist = true;
          }));
        }
      });
    }

    $scope.focusedInput = function(field) {
      $scope.errors[field].doesExist = false;
    }
  }]);