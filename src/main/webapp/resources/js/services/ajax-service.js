/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    10:02 AM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.factory('ajaxService', ['$http', '$q', function ($http, $q) {
    return {
      /**
       * create - use to create a new resource on the server
       * @param url
       * @param data
       * @returns callback promise for success and failure.
       */
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
          .success(function (data, status, headers, config) {
          deferred.resolve(headers('location'));
        })
          .error(function (data) {
          // TODO: (JOSH - 2013-05-10) Handle create errors
          deferred.reject(data);
        });
        return deferred.promise;
      },
      /**
       * get- use to get a resource from the server
       * @param url
       * @returns {*}
       */
      get: function (url) {
        if (url) {
          var deferred = $q.defer();
          $http({
            url: url,
            method: 'GET'
          })
            .success(function (data) {
            deferred.resolve(data);
          })
            .error(function () {
            // TODO: (JOSH - 2013-05-10) Handle get errors properly
            deferred.reject('An error occurred during get @ ' + url);
          });

          return deferred.promise;
        }
        return false;
      },
      /**
       * post
       * @param url
       * @returns {*}
       */
      getFastaFile: function (url) {
        if (url) {
          var deferred = $q.defer();
          $http({
            url: url,
            method: 'GET',
            headers: {
              'Accept': 'application/fasta'
            }
          })
            .success(function (data) {
              deferred.resolve(data);
            })
            .error(function () {
              // TODO: (JOSH - 2013-05-10) Handle get errors properly
              deferred.reject('An error occurred during get @ ' + url);
            });

          return deferred.promise;
        }
        return false;
      },
      /**
       * post
       * @param url
       * @param data
       * @returns {*}
       */
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
            // TODO: (JOSH - 2013-05-10) Handle post errors properly
            deferred.reject('An error occurred while posting @ ' + url);
          });

          return deferred.promise;
        }
        return false;
      },
      /**
       * patch - uses to update a resource (usually one field)
       * @param url
       * @param data
       * @returns {*}
       */
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
        return false;
      },
      /**
       * delete - delete resource from server
       * @param url
       * @returns {*}
       */
      deleteItem: function (url) {
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
        return false;
      }
    };
  }]);
})(angular, NGS);