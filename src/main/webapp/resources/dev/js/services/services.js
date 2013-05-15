/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-15
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
angular.module('irida.services', ['http-auth-interceptor-buffer', 'ngCookies'])
/**
 * Redirect user to login page if already logged in.
 */
  .run(['CookieService', '$location', '$rootScope', function (CookieService, $location, $rootScope) {
    'use strict';
    var hasCookie = CookieService.checkLoginCookie();
    if (!hasCookie && $location.path() !== '/login') {
      $rootScope.$broadcast('event:auth-loginRequired');
    }
    else if (hasCookie && $location.path() === '/login') {
      $location.path('/');
    }
  }])
/**
 * Confirms that the session contains a valid cookie for JSESSIONID
 */
  .factory('CookieService', ['$cookies', function ($cookies) {
    'use strict';
    return {
      checkLoginCookie: function () {
        return typeof $cookies.JSESSIONID === 'string';
      }
    };
  }])
  .factory('authService', ['$rootScope', 'httpBuffer', function ($rootScope, httpBuffer) {
    return {
      loginConfirmed: function () {
        $rootScope.$broadcast('event:auth-loginConfirmed');
        httpBuffer.retryAll();
      }
    };
  }])

/**
 * $http interceptor.
 * On 401 response (without 'ignoreAuthModule' option) stores the request
 * and broadcasts 'event:angular-auth-loginRequired'.
 */
  .config(['$httpProvider', function ($httpProvider) {

    var interceptor = ['$rootScope', '$q', 'httpBuffer', function ($rootScope, $q, httpBuffer) {
      function success(response) {
        return response;
      }

      function error(response) {
        if ((response.status === 401 || response.status === 302) && !response.config.ignoreAuthModule) {
          var deferred = $q.defer();
          httpBuffer.append(response.config, deferred);
          $rootScope.$broadcast('event:auth-loginRequired');
          return deferred.promise;
        }
        // otherwise, default behaviour
        return $q.reject(response);
      }

      return function (promise) {
        return promise.then(success, error);
      };

    }];
    $httpProvider.responseInterceptors.push(interceptor);
  }])

  .factory('ajaxService', function ($http, $q) {
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
          .success(function (data) {
            deferred.resolve(data);
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
              deferred.resolve(formatLinks(data));
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
  });
;

/**
 * Private module, an utility, required internally by 'http-auth-interceptor'.
 */
angular.module('http-auth-interceptor-buffer', [])

  .factory('httpBuffer', ['$injector', function ($injector) {
    /** Holds all the requests, so they can be re-requested in future. */
    var buffer = [];

    /** Service initialized later because of circular dependency problem. */
    var $http;

    function retryHttpRequest(config, deferred) {
      $http = $http || $injector.get('$http');
      $http(config).then(function (response) {
        deferred.resolve(response);
      });
    }

    return {
      /**
       * Appends HTTP request configuration object with deferred response attached to buffer.
       */
      append: function (config, deferred) {
        buffer.push({
          config: config,
          deferred: deferred
        });
      },

      /**
       * Retries all the buffered requests clears the buffer.
       */
      retryAll: function () {
        for (var i = 0; i < buffer.length; ++i) {
          retryHttpRequest(buffer[i].config, buffer[i].deferred);
        }
        buffer = [];
      }
    };
  }]);