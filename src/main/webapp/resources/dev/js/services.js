angular.module('usersServices', ['ngResource']).
  factory('Users', function($resource){
    return $resource('/users', {}, {
      query: {method:'JSONP', params:{}, isArray:true}
    });
  });