function usersListCtrl ($scope) {
  'use strict';
  
}

function newUserModalCtrl ($scope, $http) {
  'use strict';

  $scope.currUser = {};

  $scope.error = {
    username :"required",
    password: "required",
    firstName: "required",
    lastName: "required",
    email: 'required',
    phoneNumber: 'required'
  };

  $scope.postNewUser = function () {
   $.ajax({
     type: 'POST',
     data: $scope.currUser,
     success: function () {
       updateUserList();
     },
     error: function (XMLHttpRequest, textStatus, errorThrown) {
       if(errorThrown){
         // TODO(josh): handle the error
       }
       $scope.$apply($.each($.parseJSON(XMLHttpRequest.responseText), function (key, value) {
         var message = value.join('<br/>');
         $scope.error[key] = message;
         $scope.newUserForm.$setValidity(key, false);
       }));
     }
   });
 }


 $scope.focusedInput = function (field) {
   $scope.errors[field].doesExist = false;
 };
}

function updateUserList () {
  'use strict';

  console.log("This needs to be implemented with a link from Franklin.")
//   $.ajax({
//     type: 'GET',
//     url: '/users',
//     contentType: 'application/json; charset=utf-8',
//     dataType: 'json',
//     data: userListPosn,
//     success: function (d) {
//       console.log(d);
//       // TODO(josh): Clear the users table and plug d.users into it.  Need to compile the template.
//     },
//     error: function (d) {
//       console.log(d);
//     }
//   });
 }
//'use strict';
//
//var app = angular.module('sraApp', ['ngSanitize']);
//
//app.controller('usersListCtrl', ['$scope', '$window', function ($scope, $window) {
//  $scope.goToUser = function (e) {
//    var url = e.currentTarget.childNodes[1].innerHTML;
//    $window.location = url;
//  };
//}]);
//
//app.controller('newUserModalCtrl', ['$scope', function ($scope) {
//  var userListPosn = {
//    start: 21,
//    offset: 20
//  };
//
//  $scope.currUser = {
//    username: '',
//    password: '',
//    email: '',
//    phoneNumber: '',
//    firstName: '',
//    lastName: ''
//  };
//
//  $scope.errors = {
//    username: {
//      message: '',
//      doesExist: false
//    },
//    password: {
//      message: '',
//      doesExist: false
//    },
//    email: {
//      message: '',
//      doesExist: false
//    },
//    phoneNumber: {
//      message: '',
//      doesExist: false
//    },
//    firstName: {
//      message: '',
//      doesExist: false
//    },
//    lastName: {
//      message: '',
//      doesExist: false
//    }
//  };
//
//  $scope.postNewUser = function () {
//  };
//
//
//}]);