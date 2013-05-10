/*global angular */
/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-09
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
angular.module('logincheck', ['ngCookies'])
  .factory('logincheck', ['$cookies', function ($cookies) {
    'use strict';
    return {
      isLoggedIn: function () {
        return typeof $cookies['JSESSIONID'] === 'string';
      }
    }
  }]);