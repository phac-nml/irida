/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-05-06
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */

angular.module('ajaxService', [])
  .service('ajaxService', function ($http, $q) {
    'use strict';

    function formatLinks(data) {
      var links = {};
      angular.forEach(data.resource.links, function (val) {
        links[val.rel] = val.href;
      });
      data.resource.links = links;
      return data;
    }

    return {
      create: function (url, data) {
        var deferred = $q.defer();
        $http({
          method: 'POST',
          url: url,
          data: data,
          headers: {
            'Content-Type': 'application/json'
          }
        })
          .success(function (data) {
            deferred.resolve(data);
          })
          .error(function (data) {
            deferred.reject(data);
          });
        return deferred.promise;
      },
      get: function (url, data) {
        if (url) {
          var deferred = $q.defer();
          $http({
            url: url,
            method: 'GET',
            params: data
          })
            .success(function (data) {
              deferred.resolve(formatLinks(data));
            })
            .error(function () {
              deferred.reject('An error occurred while getting projects');
            });

          return deferred.promise;
        }
      },
      post: function (url, data) {
        if (url) {
          var deferred = $q.defer();
          $http({
            url: url,
            method: 'POST',
            params: data
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function () {
              deferred.reject('An error occurred while getting projects');
            });

          return deferred.promise;
        }
      },
      patch: function (url, data) {
        if (url && data) {
          var deferred = $q.defer();

          $http({
            method: 'PATCH',
            url: url,
            data: data,
            headers: {
              'Content-Type': 'application/json'
            }
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function (data) {
              deferred.reject(data);
            });
          return deferred.promise;
        }
      },
      delete: function (url) {
        if (url) {
          var deferred = $q.defer();

          $http({
            method: 'DELETE',
            url: url,
            headers: {
              'Content-Type': 'application/json'
            }
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function (data, status, headers, config) {
              console.log(data);
              console.log(status);
              console.log(headers);
              console.log(config);
              deferred.reject(status);
            });
          return deferred.promise;
        }
      }
    };
  });